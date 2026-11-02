package com.colorai.payload;

import java.util.List;

public class ChatResponse {
    private String reply;
    private List<String> palette;
    private String name;
    private String description;
    private List<String> rationale;
    private String harmonyRule;
    private List<String> gradient;

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public List<String> getPalette() { return palette; }
    public void setPalette(List<String> palette) { this.palette = palette; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getRationale() { return rationale; }
    public void setRationale(List<String> rationale) { this.rationale = rationale; }
    public String getHarmonyRule() { return harmonyRule; }
    public void setHarmonyRule(String harmonyRule) { this.harmonyRule = harmonyRule; }
    public List<String> getGradient() { return gradient; }
    public void setGradient(List<String> gradient) { this.gradient = gradient; }
}
