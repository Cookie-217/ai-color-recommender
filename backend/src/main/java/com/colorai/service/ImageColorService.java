package com.colorai.service;

import com.colorai.payload.ImageExtractResponse;
import com.colorai.util.ColorHarmony;
import com.colorai.util.ColorUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 图片取色 / 品牌色彩提取。
 * 纯 Java 8 + ImageIO 实现：下采样 -> K-Means 颜色聚类 -> 去重 -> 输出主色与品牌色板。
 */
@Service
public class ImageColorService {

    private static final int MAX_DIM = 220;   // 最长边下采样上限，控制计算量
    private static final int K = 6;           // 聚类数
    private static final int ITER = 24;       // K-Means 迭代上限
    private static final double DUP_DIST = 26;// 主色去重的 Lab 距离阈值

    public ImageExtractResponse extract(MultipartFile file, boolean brand) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("未检测到图片，请上传 PNG / JPG");
        if (file.getSize() > 5 * 1024 * 1024) throw new IllegalArgumentException("图片过大，请控制在 5MB 以内");
        String ct = file.getContentType() == null ? "" : file.getContentType().toLowerCase();
        if (!ct.contains("image/")) throw new IllegalArgumentException("仅支持图片文件（PNG / JPG）");

        BufferedImage img;
        try (InputStream in = file.getInputStream()) {
            img = ImageIO.read(in);
        } catch (Exception e) {
            throw new IllegalArgumentException("无法解析该图片，请更换 PNG / JPG");
        }
        if (img == null) throw new IllegalArgumentException("无法解析该图片，请更换 PNG / JPG");

        img = downscale(img, MAX_DIM);
        int w = img.getWidth(), h = img.getHeight();

        // 采样像素（跳过透明）
        List<double[]> samples = new ArrayList<>();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = img.getRGB(x, y);
                if (((argb >> 24) & 0xFF) < 125) continue;
                samples.add(new double[]{(argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF});
            }
        }
        if (samples.isEmpty()) throw new IllegalArgumentException("图片没有可识别的颜色");

        double[][] centers = kmeans(samples, K, ITER);
        int[] counts = new int[centers.length];
        for (double[] s : samples) counts[nearest(s, centers)]++;
        int total = samples.size();

        // 构造主色并按占比降序
        List<ImageExtractResponse.ColorEntry> entries = new ArrayList<>();
        for (int i = 0; i < centers.length; i++) {
            ColorUtils.Rgb rgb = new ColorUtils.Rgb(
                    (int) Math.round(centers[i][0]),
                    (int) Math.round(centers[i][1]),
                    (int) Math.round(centers[i][2]));
            ColorUtils.Hsl hsl = ColorUtils.rgbToHsl(rgb);
            ImageExtractResponse.ColorEntry e = new ImageExtractResponse.ColorEntry();
            e.setHex(ColorUtils.rgbToHex(rgb));
            e.setRatio((double) counts[i] / total);
            e.setHue(round(hsl.h));
            e.setSaturation(round(hsl.s));
            e.setLightness(round(hsl.l));
            entries.add(e);
        }
        entries.sort(Comparator.comparingDouble(ImageExtractResponse.ColorEntry::getRatio).reversed());

        // 去重（Lab 距离过近视为同一色）
        List<ImageExtractResponse.ColorEntry> cleaned = new ArrayList<>();
        for (ImageExtractResponse.ColorEntry e : entries) {
            boolean dup = false;
            for (ImageExtractResponse.ColorEntry c : cleaned) {
                if (ColorUtils.colorDistanceLab(ColorUtils.hexToRgb(e.getHex()), ColorUtils.hexToRgb(c.getHex())) < DUP_DIST) {
                    dup = true; break;
                }
            }
            if (!dup) cleaned.add(e);
            if (cleaned.size() >= K) break;
        }

        String primary = cleaned.get(0).getHex();
        List<String> suggested = ColorHarmony.generate(primary, ColorHarmony.Rule.ANALOGOUS);

        ImageExtractResponse res = new ImageExtractResponse();
        res.setDominantColors(cleaned);
        res.setSuggestedPalette(suggested);
        res.setPaletteName(paletteName(cleaned));
        if (brand) res.setBrandBoard(buildBrandBoard(cleaned, primary));
        return res;
    }

    // ---------- 工具方法 ----------

    private BufferedImage downscale(BufferedImage src, int maxDim) {
        int w = src.getWidth(), h = src.getHeight();
        int longest = Math.max(w, h);
        if (longest <= maxDim) return src;
        double scale = (double) maxDim / longest;
        int nw = Math.max(1, (int) Math.round(w * scale));
        int nh = Math.max(1, (int) Math.round(h * scale));
        BufferedImage out = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = out.createGraphics();
        g.drawImage(src, 0, 0, nw, nh, null);
        g.dispose();
        return out;
    }

    private double[][] kmeans(List<double[]> points, int k, int iter) {
        int n = points.size();
        k = Math.min(k, n);
        double[][] centers = new double[k][3];
        // 初始化：均匀分布取点
        for (int i = 0; i < k; i++) {
            double[] p = points.get((i * n) / k);
            centers[i] = Arrays.copyOf(p, 3);
        }
        int[] assign = new int[n];
        for (int it = 0; it < iter; it++) {
            boolean changed = false;
            for (int i = 0; i < n; i++) {
                int nn = nearest(points.get(i), centers);
                if (assign[i] != nn) { assign[i] = nn; changed = true; }
            }
            double[][] sum = new double[k][3];
            int[] cnt = new int[k];
            for (int i = 0; i < n; i++) {
                double[] p = points.get(i);
                int a = assign[i];
                sum[a][0] += p[0]; sum[a][1] += p[1]; sum[a][2] += p[2];
                cnt[a]++;
            }
            for (int c = 0; c < k; c++) {
                if (cnt[c] > 0) {
                    centers[c][0] = sum[c][0] / cnt[c];
                    centers[c][1] = sum[c][1] / cnt[c];
                    centers[c][2] = sum[c][2] / cnt[c];
                }
            }
            if (!changed && it > 0) break;
        }
        return centers;
    }

    private int nearest(double[] p, double[][] centers) {
        int best = 0;
        double bestD = Double.MAX_VALUE;
        for (int i = 0; i < centers.length; i++) {
            double d = (p[0] - centers[i][0]) * (p[0] - centers[i][0])
                    + (p[1] - centers[i][1]) * (p[1] - centers[i][1])
                    + (p[2] - centers[i][2]) * (p[2] - centers[i][2]);
            if (d < bestD) { bestD = d; best = i; }
        }
        return best;
    }

    private ImageExtractResponse.BrandBoard buildBrandBoard(List<ImageExtractResponse.ColorEntry> cleaned, String primary) {
        ImageExtractResponse.BrandBoard board = new ImageExtractResponse.BrandBoard();
        board.setPrimary(primary);
        board.setSecondary(cleaned.size() > 1 ? cleaned.get(1).getHex() : shiftHue(primary, 28));
        board.setAccent(cleaned.size() > 2 ? cleaned.get(2).getHex() : shiftHue(primary, -32, 70));
        board.setNeutralLight(adjustHsl(primary, 0, 14, 94));
        board.setNeutralDark(adjustHsl(primary, 0, 34, 18));
        return board;
    }

    private String shiftHue(String hex, double dh) { return adjustHsl(hex, dh, 0, 0); }
    private String shiftHue(String hex, double dh, double ds) { return adjustHsl(hex, dh, ds, 0); }

    private String adjustHsl(String hex, double dh, double ds, double dl) {
        ColorUtils.Rgb rgb = ColorUtils.hexToRgb(hex);
        ColorUtils.Hsl hsl = ColorUtils.rgbToHsl(rgb);
        double h = ColorUtils.mod360(hsl.h + dh);
        double s = ColorUtils.clamp(hsl.s + ds, 0, 100);
        double l = dl == 0 ? hsl.l : ColorUtils.clamp(dl, 0, 100);
        return ColorUtils.rgbToHex(ColorUtils.hslToRgb(new ColorUtils.Hsl(h, s, l)));
    }

    private String paletteName(List<ImageExtractResponse.ColorEntry> cleaned) {
        double hue = cleaned.get(0).getHue();
        String[] names = {"红", "橙", "黄", "黄绿", "绿", "青绿", "青", "蓝", "蓝紫", "紫", "品红", "玫红"};
        int idx = (int) Math.round(hue / 30) % 12;
        double light = cleaned.get(0).getLightness();
        String tone = light > 70 ? "明亮" : light < 35 ? "深邃" : "自然";
        return names[idx] + "色系 · " + tone + "配色";
    }

    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
}
