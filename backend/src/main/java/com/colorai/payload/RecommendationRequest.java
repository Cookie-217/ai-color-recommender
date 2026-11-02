package com.colorai.payload;

import java.util.List;

public class RecommendationRequest {
    /** 基准色（可选），如 #3498db */
    private String baseColor;
    /** 使用场景：web / fashion / interior / brand / poster / food / nature / social */
    private String scenario = "web";
    /** 情绪关键词：warm / cool / calm / energetic / luxury / playful / minimal / vintage / tech */
    private String mood = "balanced";
    /** 风格：vibrant / muted / pastel / dark / light */
    private String style = "vibrant";
    /** 生成颜色数量 */
    private int count = 5;
    /** 是否同时返回其他和谐规则的备选方案 */
    private boolean includeAlternatives = true;

    public String getBaseColor() { return baseColor; }
    public void setBaseColor(String baseColor) { this.baseColor = baseColor; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public boolean isIncludeAlternatives() { return includeAlternatives; }
    public void setIncludeAlternatives(boolean includeAlternatives) { this.includeAlternatives = includeAlternatives; }
}
