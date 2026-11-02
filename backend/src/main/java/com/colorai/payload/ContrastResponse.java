package com.colorai.payload;

public class ContrastResponse {
    private String foreground;
    private String background;
    private double ratio;
    private String level;       // AAA / AA / AA Large / Fail
    private boolean aaPass;     // 普通文本 AA (>=4.5)
    private boolean aaaPass;    // 普通文本 AAA (>=7)
    private String bestTextColor;

    public String getForeground() { return foreground; }
    public void setForeground(String foreground) { this.foreground = foreground; }
    public String getBackground() { return background; }
    public void setBackground(String background) { this.background = background; }
    public double getRatio() { return ratio; }
    public void setRatio(double ratio) { this.ratio = ratio; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public boolean isAaPass() { return aaPass; }
    public void setAaPass(boolean aaPass) { this.aaPass = aaPass; }
    public boolean isAaaPass() { return aaaPass; }
    public void setAaaPass(boolean aaaPass) { this.aaaPass = aaaPass; }
    public String getBestTextColor() { return bestTextColor; }
    public void setBestTextColor(String bestTextColor) { this.bestTextColor = bestTextColor; }
}
