package com.colorai.service;

import com.colorai.payload.ColorConvertResponse;
import com.colorai.payload.ContrastResponse;
import com.colorai.util.ColorHarmony;
import com.colorai.util.ColorUtils;
import com.colorai.util.ContrastUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColorService {

    public ColorConvertResponse convert(String hex) {
        if (!ColorUtils.isValidHex(hex)) throw new IllegalArgumentException("非法的 HEX 颜色: " + hex);
        ColorUtils.Rgb rgb = ColorUtils.hexToRgb(hex);
        ColorUtils.Hsl hsl = ColorUtils.rgbToHsl(rgb);
        ColorUtils.Hsv hsv = ColorUtils.rgbToHsv(rgb);
        ColorUtils.Cmyk cmyk = ColorUtils.rgbToCmyk(rgb);
        ColorUtils.Lab lab = ColorUtils.rgbToLab(rgb);
        ColorConvertResponse r = new ColorConvertResponse();
        r.setHex(ColorUtils.rgbToHex(rgb));
        r.setRgb(new ColorConvertResponse.Rgb(rgb.r, rgb.g, rgb.b));
        r.setHsl(new ColorConvertResponse.Hsl(round(hsl.h), round(hsl.s), round(hsl.l)));
        r.setHsv(new ColorConvertResponse.Hsv(round(hsv.h), round(hsv.s), round(hsv.v)));
        r.setCmyk(new ColorConvertResponse.Cmyk(round(cmyk.c), round(cmyk.m), round(cmyk.y), round(cmyk.k)));
        r.setLab(new ColorConvertResponse.Lab(round(lab.l), round(lab.a), round(lab.b)));
        r.setBestTextColor(ContrastUtils.bestTextColor(r.getHex()));
        r.setLuminance(round(ContrastUtils.relativeLuminance(rgb)));
        return r;
    }

    public ContrastResponse contrast(String fg, String bg) {
        if (!ColorUtils.isValidHex(fg) || !ColorUtils.isValidHex(bg))
            throw new IllegalArgumentException("非法颜色，请使用 HEX，如 #FF0000");
        ContrastResponse r = new ContrastResponse();
        r.setForeground(ColorUtils.hexToHex(fg));
        r.setBackground(ColorUtils.hexToHex(bg));
        double ratio = ContrastUtils.contrastRatio(fg, bg);
        r.setRatio(round(ratio));
        r.setLevel(ContrastUtils.wcagLevel(fg, bg));
        r.setAaPass(ratio >= 4.5);
        r.setAaaPass(ratio >= 7);
        r.setBestTextColor(ContrastUtils.bestTextColor(bg));
        return r;
    }

    public List<String> harmony(String base, String ruleName) {
        if (!ColorUtils.isValidHex(base)) throw new IllegalArgumentException("非法的基准色 HEX: " + base);
        ColorHarmony.Rule rule = parseRule(ruleName);
        return ColorHarmony.generate(base, rule);
    }

    public List<ColorHarmony.PalettePreview> all(String base) {
        if (!ColorUtils.isValidHex(base)) throw new IllegalArgumentException("非法的基准色 HEX: " + base);
        return ColorHarmony.generateAll(base);
    }

    public List<String> gradient(String from, String to, int steps) {
        if (!ColorUtils.isValidHex(from) || !ColorUtils.isValidHex(to))
            throw new IllegalArgumentException("非法颜色 HEX");
        return ColorHarmony.gradient(from, to, steps);
    }

    private ColorHarmony.Rule parseRule(String name) {
        if (name == null || name.isEmpty()) return ColorHarmony.Rule.COMPLEMENTARY;
        try {
            return ColorHarmony.Rule.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("未知和谐规则: " + name +
                    "，可选: " + java.util.Arrays.toString(ColorHarmony.Rule.values()));
        }
    }

    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
}
