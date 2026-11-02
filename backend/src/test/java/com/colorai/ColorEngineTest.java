package com.colorai;

import com.colorai.payload.RecommendationRequest;
import com.colorai.payload.RecommendationResponse;
import com.colorai.service.ColorService;
import com.colorai.service.PaletteAgentService;
import com.colorai.util.ColorHarmony;
import com.colorai.util.ColorUtils;
import com.colorai.util.ContrastUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ColorEngineTest {

    @Test
    void hexRgbRoundTrip() {
        ColorUtils.Rgb rgb = ColorUtils.hexToRgb("#3498db");
        assertEquals("#3498DB", ColorUtils.rgbToHex(rgb));
    }

    @Test
    void hslRoundTrip() {
        ColorUtils.Rgb rgb = ColorUtils.hexToRgb("#FF0000");
        ColorUtils.Hsl hsl = ColorUtils.rgbToHsl(rgb);
        assertEquals(0.0, hsl.h, 0.5);
        ColorUtils.Rgb back = ColorUtils.hslToRgb(hsl);
        assertEquals(255, back.r);
    }

    @Test
    void harmonyGeneratesExpectedSizes() {
        List<String> comp = ColorHarmony.generate("#3498db", ColorHarmony.Rule.COMPLEMENTARY);
        assertEquals(2, comp.size());
        List<String> tri = ColorHarmony.generate("#3498db", ColorHarmony.Rule.TRIADIC);
        assertEquals(3, tri.size());
        List<ColorHarmony.PalettePreview> all = ColorHarmony.generateAll("#3498db");
        assertEquals(ColorHarmony.Rule.values().length, all.size());
    }

    @Test
    void contrastBlackWhiteIs21() {
        double ratio = ContrastUtils.contrastRatio("#000000", "#FFFFFF");
        assertEquals(21.0, ratio, 0.01);
    }

    @Test
    void colorServiceConvert() {
        ColorService svc = new ColorService();
        assertTrue(svc.convert("#ABCDEF").getHsl().s >= 0);
        assertFalse(svc.contrast("#000000", "#FFFFFF").isAaPass() == false);
    }

    @Test
    void agentRecommendProducesPalette() {
        PaletteAgentService agent = new PaletteAgentService();
        RecommendationRequest req = new RecommendationRequest();
        req.setScenario("brand");
        req.setMood("luxury");
        req.setStyle("dark");
        req.setBaseColor("#1D3557");
        RecommendationResponse res = agent.recommend(req);
        assertNotNull(res.getName());
        assertFalse(res.getPalette().isEmpty());
        assertNotNull(res.getHarmonyRule());
        assertFalse(res.getRationale().isEmpty());
    }

    @Test
    void agentChatParsesMood() {
        PaletteAgentService agent = new PaletteAgentService();
        com.colorai.payload.ChatRequest chat = new com.colorai.payload.ChatRequest();
        chat.setMessage("帮我做一个温暖高级感的咖啡品牌配色");
        com.colorai.payload.ChatResponse res = agent.chat(chat);
        assertFalse(res.getPalette().isEmpty());
        assertNotNull(res.getReply());
    }
}
