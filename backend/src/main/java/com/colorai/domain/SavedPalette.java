package com.colorai.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "saved_palette")
public class SavedPalette {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 80)
    private String name;

    /** 颜色以逗号分隔的 HEX 存储，如 #FF0000,#00FF00 */
    @Column(nullable = false, length = 400)
    private String colors;

    @Column(length = 40)
    private String scenario;

    @Column(length = 500)
    private String description;

    @Column(length = 300)
    private String tags;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public SavedPalette() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getColorList() {
        if (colors == null || colors.isEmpty()) return java.util.Collections.emptyList();
        return Arrays.stream(colors.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }
    public void setColors(List<String> list) {
        this.colors = list == null ? "" : String.join(",", list);
    }
    public String getColors() { return colors; }
    public void setColors(String colors) { this.colors = colors; }

    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTagList() {
        if (tags == null || tags.isEmpty()) return java.util.Collections.emptyList();
        return Arrays.stream(tags.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }
    public void setTags(List<String> list) {
        this.tags = list == null ? "" : String.join(",", list);
    }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
