package com.colorai.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class SavedPaletteRequest {
    @NotBlank
    @Size(max = 80)
    private String name;

    @Size(min = 1, max = 16)
    private List<String> colors;

    private String scenario;
    private String description;
    private List<String> tags;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
