package com.colorai.util;

import java.util.Objects;

/**
 * 色彩模型转换工具：HEX / RGB / HSL / HSV / CMYK / LAB
 * 所有方法均为纯函数，无副作用。
 */
public final class ColorUtils {

    private ColorUtils() {}

    public static class Rgb {
        public final int r, g, b;
        public Rgb(int r, int g, int b) { this.r = clamp(r); this.g = clamp(g); this.b = clamp(b); }
    }

    public static class Hsl {
        public final double h; // 0-360
        public final double s; // 0-100
        public final double l; // 0-100
        public Hsl(double h, double s, double l) { this.h = mod360(h); this.s = clamp(s, 0, 100); this.l = clamp(l, 0, 100); }
    }

    public static class Hsv {
        public final double h, s, v;
        public Hsv(double h, double s, double v) { this.h = mod360(h); this.s = clamp(s, 0, 100); this.v = clamp(v, 0, 100); }
    }

    public static class Cmyk {
        public final double c, m, y, k;
        public Cmyk(double c, double m, double y, double k) {
            this.c = clamp(c, 0, 100); this.m = clamp(m, 0, 100);
            this.y = clamp(y, 0, 100); this.k = clamp(k, 0, 100);
        }
    }

    public static class Lab {
        public final double l, a, b;
        public Lab(double l, double a, double b) { this.l = l; this.a = a; this.b = b; }
    }

    // ---------- HEX ----------
    public static Rgb hexToRgb(String hex) {
        String h = normalizeHex(hex);
        int rgb = Integer.parseInt(h, 16);
        return new Rgb((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

    public static String rgbToHex(Rgb rgb) {
        return String.format("#%02X%02X%02X", rgb.r, rgb.g, rgb.b);
    }

    public static String hexToHex(String hex) {
        return rgbToHex(hexToRgb(hex));
    }

    // ---------- RGB <-> HSL ----------
    public static Hsl rgbToHsl(Rgb rgb) {
        double r = rgb.r / 255.0, g = rgb.g / 255.0, b = rgb.b / 255.0;
        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double h, s, l = (max + min) / 2.0;

        if (max == min) {
            h = s = 0;
        } else {
            double d = max - min;
            s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
            if (max == r) h = (g - b) / d + (g < b ? 6 : 0);
            else if (max == g) h = (b - r) / d + 2;
            else h = (r - g) / d + 4;
            h /= 6;
        }
        return new Hsl(h * 360, s * 100, l * 100);
    }

    public static Rgb hslToRgb(Hsl hsl) {
        double h = hsl.h / 360.0, s = hsl.s / 100.0, l = hsl.l / 100.0;
        double r, g, b;
        if (s == 0) {
            r = g = b = l;
        } else {
            double q = l < 0.5 ? l * (1 + s) : l + s - l * s;
            double p = 2 * l - q;
            r = hue2rgb(p, q, h + 1.0 / 3);
            g = hue2rgb(p, q, h);
            b = hue2rgb(p, q, h - 1.0 / 3);
        }
        return new Rgb((int) Math.round(r * 255), (int) Math.round(g * 255), (int) Math.round(b * 255));
    }

    // ---------- RGB <-> HSV ----------
    public static Hsv rgbToHsv(Rgb rgb) {
        double r = rgb.r / 255.0, g = rgb.g / 255.0, b = rgb.b / 255.0;
        double max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
        double d = max - min;
        double h = 0, s = max == 0 ? 0 : d / max, v = max;
        if (d != 0) {
            if (max == r) h = (g - b) / d + (g < b ? 6 : 0);
            else if (max == g) h = (b - r) / d + 2;
            else h = (r - g) / d + 4;
            h /= 6;
        }
        return new Hsv(h * 360, s * 100, v * 100);
    }

    public static Rgb hsvToRgb(Hsv hsv) {
        double h = hsv.h / 360.0, s = hsv.s / 100.0, v = hsv.v / 100.0;
        int i = (int) Math.floor(h * 6);
        double f = h * 6 - i;
        double p = v * (1 - s), q = v * (1 - f * s), t = v * (1 - (1 - f) * s);
        double r, g, b;
        switch (i % 6) {
            case 0: r = v; g = t; b = p; break;
            case 1: r = q; g = v; b = p; break;
            case 2: r = p; g = v; b = t; break;
            case 3: r = p; g = q; b = v; break;
            case 4: r = t; g = p; b = v; break;
            default: r = v; g = p; b = q; break;
        }
        return new Rgb((int) Math.round(r * 255), (int) Math.round(g * 255), (int) Math.round(b * 255));
    }

    // ---------- RGB <-> CMYK ----------
    public static Cmyk rgbToCmyk(Rgb rgb) {
        double r = rgb.r / 255.0, g = rgb.g / 255.0, b = rgb.b / 255.0;
        double k = 1 - Math.max(r, Math.max(g, b));
        if (k == 1) return new Cmyk(0, 0, 0, 100);
        double c = (1 - r - k) / (1 - k);
        double m = (1 - g - k) / (1 - k);
        double y = (1 - b - k) / (1 - k);
        return new Cmyk(c * 100, m * 100, y * 100, k * 100);
    }

    public static Rgb cmykToRgb(Cmyk cmyk) {
        double c = cmyk.c / 100, m = cmyk.m / 100, y = cmyk.y / 100, k = cmyk.k / 100;
        return new Rgb(
                (int) Math.round(255 * (1 - c) * (1 - k)),
                (int) Math.round(255 * (1 - m) * (1 - k)),
                (int) Math.round(255 * (1 - y) * (1 - k)));
    }

    // ---------- RGB <-> LAB (CIE76) ----------
    public static Lab rgbToLab(Rgb rgb) {
        double r = pivotRgb(rgb.r / 255.0);
        double g = pivotRgb(rgb.g / 255.0);
        double b = pivotRgb(rgb.b / 255.0);
        double x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.95047;
        double y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.0;
        double z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.08883;
        x = pivotXyz(x); y = pivotXyz(y); z = pivotXyz(z);
        return new Lab(116 * y - 16, 500 * (x - y), 200 * (y - z));
    }

    public static double colorDistanceLab(Rgb a, Rgb b) {
        Lab la = rgbToLab(a), lb = rgbToLab(b);
        return Math.sqrt((la.l - lb.l) * (la.l - lb.l) + (la.a - lb.a) * (la.a - lb.a) + (la.b - lb.b) * (la.b - lb.b));
    }

    // ---------- 辅助 ----------
    public static boolean isValidHex(String hex) {
        if (hex == null) return false;
        String h = hex.trim();
        if (h.startsWith("#")) h = h.substring(1);
        return h.matches("^([0-9a-fA-F]{6}|[0-9a-fA-F]{3})$");
    }

    private static String normalizeHex(String hex) {
        String h = hex.trim();
        if (h.startsWith("#")) h = h.substring(1);
        if (h.length() == 3) h = "" + h.charAt(0) + h.charAt(0) + h.charAt(1) + h.charAt(1) + h.charAt(2) + h.charAt(2);
        if (h.length() != 6) throw new IllegalArgumentException("非法HEX颜色: " + hex);
        return h;
    }

    private static double hue2rgb(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0 / 6) return p + (q - p) * 6 * t;
        if (t < 1.0 / 2) return q;
        if (t < 2.0 / 3) return p + (q - p) * (2.0 / 3 - t) * 6;
        return p;
    }

    private static double pivotRgb(double v) {
        return v > 0.04045 ? Math.pow((v + 0.055) / 1.055, 2.4) : v / 12.92;
    }

    private static double pivotXyz(double v) {
        return v > 0.008856 ? Math.cbrt(v) : (7.787 * v + 16.0 / 116);
    }

    public static int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    public static double clamp(double v, double lo, double hi) { return Math.max(lo, Math.min(hi, v)); }

    public static double mod360(double h) { double m = h % 360; return m < 0 ? m + 360 : m; }

    public static int clampHueIndex(int idx) { return ((idx % 360) + 360) % 360; }
}
