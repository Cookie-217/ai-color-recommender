package com.colorai.payload;

import java.util.List;

/**
 * 图片取色 / 品牌色彩提取接口返回结构。
 */
public class ImageExtractResponse {

    /** 提取出的主色（按占比降序），含 HEX 与 HSL 与占比 */
    private List<ColorEntry> dominantColors;

    /** 基于主色由和谐引擎生成的建议配色 */
    private List<String> suggestedPalette;

    /** 自动生成的配色名称（基于主色色相） */
    private String paletteName;

    /** 仅品牌模式返回：主色 / 辅助色 / 点缀色 / 中性浅 / 中性深 */
    private BrandBoard brandBoard;

    public static class ColorEntry {
        private String hex;
        private double ratio;     // 0~1
        private double hue;       // 0~360
        private double saturation;// 0~100
        private double lightness; // 0~100

        public String getHex() { return hex; }
        public void setHex(String hex) { this.hex = hex; }
        public double getRatio() { return ratio; }
        public void setRatio(double ratio) { this.ratio = ratio; }
        public double getHue() { return hue; }
        public void setHue(double hue) { this.hue = hue; }
        public double getSaturation() { return saturation; }
        public void setSaturation(double saturation) { this.saturation = saturation; }
        public double getLightness() { return lightness; }
        public void setLightness(double lightness) { this.lightness = lightness; }
    }

    public static class BrandBoard {
        private String primary;
        private String secondary;
        private String accent;
        private String neutralLight;
        private String neutralDark;

        public String getPrimary() { return primary; }
        public void setPrimary(String primary) { this.primary = primary; }
        public String getSecondary() { return secondary; }
        public void setSecondary(String secondary) { this.secondary = secondary; }
        public String getAccent() { return accent; }
        public void setAccent(String accent) { this.accent = accent; }
        public String getNeutralLight() { return neutralLight; }
        public void setNeutralLight(String neutralLight) { this.neutralLight = neutralLight; }
        public String getNeutralDark() { return neutralDark; }
        public void setNeutralDark(String neutralDark) { this.neutralDark = neutralDark; }
    }

    public List<ColorEntry> getDominantColors() { return dominantColors; }
    public void setDominantColors(List<ColorEntry> dominantColors) { this.dominantColors = dominantColors; }
    public List<String> getSuggestedPalette() { return suggestedPalette; }
    public void setSuggestedPalette(List<String> suggestedPalette) { this.suggestedPalette = suggestedPalette; }
    public String getPaletteName() { return paletteName; }
    public void setPaletteName(String paletteName) { this.paletteName = paletteName; }
    public BrandBoard getBrandBoard() { return brandBoard; }
    public void setBrandBoard(BrandBoard brandBoard) { this.brandBoard = brandBoard; }
}
