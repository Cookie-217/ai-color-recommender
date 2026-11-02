package com.colorai.util;

/**
 * 无障碍对比度工具：基于 WCAG 2.1 计算相对亮度与对比度。
 */
public final class ContrastUtils {

    private ContrastUtils() {}

    /** 相对亮度 (0-1) */
    public static double relativeLuminance(ColorUtils.Rgb rgb) {
        double r = lin(rgb.r / 255.0);
        double g = lin(rgb.g / 255.0);
        double b = lin(rgb.b / 255.0);
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    /** 两个颜色的对比度 (1.0 - 21.0) */
    public static double contrastRatio(String hex1, String hex2) {
        double l1 = relativeLuminance(ColorUtils.hexToRgb(hex1));
        double l2 = relativeLuminance(ColorUtils.hexToRgb(hex2));
        double lighter = Math.max(l1, l2);
        double darker = Math.min(l1, l2);
        return (lighter + 0.05) / (darker + 0.05);
    }

    /** 在给定背景上推荐使用黑色还是白色文字 */
    public static String bestTextColor(String bgHex) {
        double ratioBlack = contrastRatio(bgHex, "#000000");
        double ratioWhite = contrastRatio(bgHex, "#FFFFFF");
        return ratioWhite >= ratioBlack ? "#FFFFFF" : "#000000";
    }

    /** 评估某前景/背景组合的无障碍等级 */
    public static String wcagLevel(String fg, String bg) {
        double ratio = contrastRatio(fg, bg);
        if (ratio >= 7) return "AAA";
        if (ratio >= 4.5) return "AA";
        if (ratio >= 3) return "AA Large";
        return "Fail";
    }

    private static double lin(double c) {
        return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }
}
