package com.colorai.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 配色和谐引擎：基于色彩理论的多种经典和谐规则生成调色板。
 */
public final class ColorHarmony {

    private ColorHarmony() {}

    public enum Rule {
        COMPLEMENTARY("互补色"),
        ANALOGOUS("邻近色"),
        TRIADIC("三角色"),
        TETRADIC("四角色"),
        SPLIT_COMPLEMENTARY("分裂互补"),
        MONOCHROMATIC("单色阶"),
        SHADES("暗色调"),
        TINTS("亮色调"),
        SQUARE("正方形");

        public final String label;
        Rule(String label) { this.label = label; }
    }

    /** 生成某规则下的完整调色板（HEX 列表） */
    public static List<String> generate(String baseHex, Rule rule) {
        ColorUtils.Hsl base = ColorUtils.rgbToHsl(ColorUtils.hexToRgb(baseHex));
        switch (rule) {
            case COMPLEMENTARY:
                return list(baseHex, rotate(base, 180));
            case ANALOGOUS:
                return list(rotate(base, -30), rotate(base, -15), baseHex, rotate(base, 15), rotate(base, 30));
            case TRIADIC:
                return list(baseHex, rotate(base, 120), rotate(base, 240));
            case TETRADIC:
                return list(baseHex, rotate(base, 90), rotate(base, 180), rotate(base, 270));
            case SQUARE:
                return list(baseHex, rotate(base, 90), rotate(base, 180), rotate(base, 270));
            case SPLIT_COMPLEMENTARY:
                return list(baseHex, rotate(base, 150), rotate(base, 210));
            case MONOCHROMATIC:
                return list(
                        hsl(base.h, base.s, 25), hsl(base.h, base.s, 40),
                        hsl(base.h, base.s, 55), hsl(base.h, base.s, 70),
                        hsl(base.h, base.s, 85));
            case SHADES:
                return list(
                        hsl(base.h, base.s, 80), hsl(base.h, base.s, 65),
                        hsl(base.h, base.s, 50), hsl(base.h, base.s, 35),
                        hsl(base.h, base.s, 20));
            case TINTS:
                return list(
                        hsl(base.h, Math.min(base.s, 60), 30), hsl(base.h, Math.min(base.s, 60), 45),
                        hsl(base.h, Math.min(base.s, 60), 60), hsl(base.h, Math.min(base.s, 60), 75),
                        hsl(base.h, Math.min(base.s, 60), 90));
            default:
                return list(baseHex);
        }
    }

    /** 所有规则一次性生成（用于"一键全方案"） */
    public static List<PalettePreview> generateAll(String baseHex) {
        List<PalettePreview> out = new ArrayList<>();
        for (Rule r : Rule.values()) {
            out.add(new PalettePreview(r.name(), r.label, generate(baseHex, r)));
        }
        return out;
    }

    /** 在指定色相区间采样，生成渐变/自然过渡色 */
    public static List<String> gradient(String fromHex, String toHex, int steps) {
        ColorUtils.Rgb a = ColorUtils.hexToRgb(fromHex);
        ColorUtils.Rgb b = ColorUtils.hexToRgb(toHex);
        List<String> out = new ArrayList<>();
        steps = Math.max(2, steps);
        for (int i = 0; i < steps; i++) {
            double t = (double) i / (steps - 1);
            int r = (int) Math.round(a.r + (b.r - a.r) * t);
            int g = (int) Math.round(a.g + (b.g - a.g) * t);
            int bl = (int) Math.round(a.b + (b.b - a.b) * t);
            out.add(ColorUtils.rgbToHex(new ColorUtils.Rgb(r, g, bl)));
        }
        return out;
    }

    private static String rotate(ColorUtils.Hsl base, double deg) {
        return ColorUtils.rgbToHex(ColorUtils.hslToRgb(new ColorUtils.Hsl(base.h + deg, base.s, base.l)));
    }

    private static String hsl(double h, double s, double l) {
        return ColorUtils.rgbToHex(ColorUtils.hslToRgb(new ColorUtils.Hsl(h, s, l)));
    }

    private static List<String> list(String... hexes) {
        List<String> out = new ArrayList<>();
        for (String h : hexes) out.add(ColorUtils.hexToHex(h));
        return out;
    }

    public static class PalettePreview {
        public final String rule;
        public final String label;
        public final List<String> colors;
        public PalettePreview(String rule, String label, List<String> colors) {
            this.rule = rule; this.label = label; this.colors = colors;
        }
    }
}
