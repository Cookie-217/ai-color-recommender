package com.colorai.payload;

import java.util.List;

public class RecommendationResponse {
    private List<String> palette;
    private String name;
    private String harmonyRule;
    private String description;
    private List<String> rationale;
    private String mood;
    private String scenario;
    private List<String> accessibility;
    private List<String> gradient;
    private List<Alternative> alternatives;

    public static class Alternative {
        private String rule;
        private String label;
        private List<String> colors;
        public Alternative(String rule, String label, List<String> colors) {
            this.rule = rule; this.label = label; this.colors = colors;
        }
        public String getRule() { return rule; }
        public void setRule(String rule) { this.rule = rule; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public List<String> getColors() { return colors; }
        public void setColors(List<String> colors) { this.colors = colors; }
    }

    // getters / setters
    public List<String> getPalette() { return palette; }
    public void setPalette(List<String> palette) { this.palette = palette; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHarmonyRule() { return harmonyRule; }
    public void setHarmonyRule(String harmonyRule) { this.harmonyRule = harmonyRule; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getRationale() { return rationale; }
    public void setRationale(List<String> rationale) { this.rationale = rationale; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public List<String> getAccessibility() { return accessibility; }
    public void setAccessibility(List<String> accessibility) { this.accessibility = accessibility; }
    public List<String> getGradient() { return gradient; }
    public void setGradient(List<String> gradient) { this.gradient = gradient; }
    public List<Alternative> getAlternatives() { return alternatives; }
    public void setAlternatives(List<Alternative> alternatives) { this.alternatives = alternatives; }
}
