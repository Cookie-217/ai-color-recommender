package com.colorai.service;

import com.colorai.payload.*;
import com.colorai.util.ColorHarmony;
import com.colorai.util.ColorUtils;
import com.colorai.util.ContrastUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI 配色推荐 Agent：综合「情绪 / 场景 / 风格」多维度知识库，
 * 编排色彩和谐引擎，产出结构化推荐结果。可选接入大模型增强解读。
 */
@Service
public class PaletteAgentService {

    @Autowired(required = false)
    private LlmAdvisorService llmAdvisor;

    // 情绪 -> 色相中心（度）
    private static final Map<String, Integer> MOOD_HUE = new HashMap<>();
    static {
        MOOD_HUE.put("warm", 25); MOOD_HUE.put("温暖", 25);
        MOOD_HUE.put("cool", 210); MOOD_HUE.put("清凉", 210);
        MOOD_HUE.put("calm", 200); MOOD_HUE.put("平静", 200);
        MOOD_HUE.put("energetic", 350); MOOD_HUE.put("活力", 350);
        MOOD_HUE.put("luxury", 45); MOOD_HUE.put("高级", 45); MOOD_HUE.put("奢华", 45);
        MOOD_HUE.put("playful", 320); MOOD_HUE.put("活泼", 320);
        MOOD_HUE.put("minimal", 0); MOOD_HUE.put("极简", 0);
        MOOD_HUE.put("vintage", 30); MOOD_HUE.put("复古", 30);
        MOOD_HUE.put("tech", 210); MOOD_HUE.put("科技", 210);
        MOOD_HUE.put("nature", 130); MOOD_HUE.put("自然", 130);
        MOOD_HUE.put("romantic", 340); MOOD_HUE.put("浪漫", 340);
        MOOD_HUE.put("balanced", 210); MOOD_HUE.put("均衡", 210);
    }

    // 场景 -> 色相倾向
    private static final Map<String, Integer> SCENARIO_HUE = new HashMap<>();
    static {
        SCENARIO_HUE.put("web", 210); SCENARIO_HUE.put("网页", 210); SCENARIO_HUE.put("ui", 210);
        SCENARIO_HUE.put("fashion", 320); SCENARIO_HUE.put("时尚", 320);
        SCENARIO_HUE.put("interior", 30); SCENARIO_HUE.put("室内", 30); SCENARIO_HUE.put("家居", 30);
        SCENARIO_HUE.put("brand", 210); SCENARIO_HUE.put("品牌", 210);
        SCENARIO_HUE.put("poster", 350); SCENARIO_HUE.put("海报", 350);
        SCENARIO_HUE.put("food", 25); SCENARIO_HUE.put("美食", 25); SCENARIO_HUE.put("餐饮", 25);
        SCENARIO_HUE.put("nature", 130); SCENARIO_HUE.put("自然", 130);
        SCENARIO_HUE.put("social", 280); SCENARIO_HUE.put("社交", 280);
    }

    // 风格 -> 饱和度/明度
    private static final Map<String, double[]> STYLE = new HashMap<>();
    static {
        STYLE.put("vibrant", new double[]{85, 50}); STYLE.put("鲜艳", new double[]{85, 50});
        STYLE.put("muted", new double[]{45, 55}); STYLE.put("柔和", new double[]{45, 55});
        STYLE.put("pastel", new double[]{55, 82}); STYLE.put("粉彩", new double[]{55, 82});
        STYLE.put("dark", new double[]{65, 38}); STYLE.put("暗色", new double[]{65, 38});
        STYLE.put("light", new double[]{60, 82}); STYLE.put("明亮", new double[]{60, 82});
    }

    private static final Map<String, String> SCENARIO_LABEL = new HashMap<>();
    static {
        SCENARIO_LABEL.put("web", "网页 / UI"); SCENARIO_LABEL.put("ui", "网页 / UI");
        SCENARIO_LABEL.put("fashion", "时尚穿搭"); SCENARIO_LABEL.put("interior", "室内空间");
        SCENARIO_LABEL.put("brand", "品牌识别"); SCENARIO_LABEL.put("poster", "海报 / 视觉");
        SCENARIO_LABEL.put("food", "美食餐饮"); SCENARIO_LABEL.put("nature", "自然主题");
        SCENARIO_LABEL.put("social", "社交 / 内容");
    }

    private static final Map<String, String> MOOD_LABEL = new HashMap<>();
    static {
        MOOD_LABEL.put("warm", "温暖"); MOOD_LABEL.put("cool", "清凉"); MOOD_LABEL.put("calm", "平静");
        MOOD_LABEL.put("energetic", "活力"); MOOD_LABEL.put("luxury", "高级感"); MOOD_LABEL.put("playful", "活泼");
        MOOD_LABEL.put("minimal", "极简"); MOOD_LABEL.put("vintage", "复古"); MOOD_LABEL.put("tech", "科技");
        MOOD_LABEL.put("nature", "自然"); MOOD_LABEL.put("romantic", "浪漫"); MOOD_LABEL.put("balanced", "均衡");
    }

    public RecommendationResponse recommend(RecommendationRequest req) {
        ColorUtils.Hsl base = resolveBase(req);
        base = applyStyle(base, req.getStyle());
        String baseHex = ColorUtils.rgbToHex(ColorUtils.hslToRgb(base));

        ColorHarmony.Rule rule = chooseRule(req);
        List<String> palette = ColorHarmony.generate(baseHex, rule);
        palette = fitCount(palette, req.getCount(), base.h);

        RecommendationResponse res = new RecommendationResponse();
        res.setPalette(palette);
        res.setHarmonyRule(rule.name());
        res.setName(buildName(req, rule));
        res.setMood(req.getMood());
        res.setScenario(req.getScenario());
        res.setDescription(buildDescription(req, rule, palette));
        res.setRationale(buildRationale(req, rule, base, palette));
        res.setAccessibility(buildAccessibility(palette));
        res.setGradient(ColorHarmony.gradient(palette.get(0), palette.get(palette.size() - 1), 6));
        if (req.isIncludeAlternatives()) {
            res.setAlternatives(buildAlternatives(baseHex, rule));
        }

        enrichWithLlm(res, req, rule, palette);
        return res;
    }

    public ChatResponse chat(ChatRequest req) {
        RecommendationRequest rreq = parseFromMessage(req.getMessage());
        if (req.getContextColors() != null && !req.getContextColors().isEmpty()
                && ColorUtils.isValidHex(req.getContextColors().get(0))) {
            rreq.setBaseColor(req.getContextColors().get(0));
        }
        RecommendationResponse rec = recommend(rreq);

        ChatResponse res = new ChatResponse();
        res.setPalette(rec.getPalette());
        res.setName(rec.getName());
        res.setDescription(rec.getDescription());
        res.setRationale(rec.getRationale());
        res.setHarmonyRule(rec.getHarmonyRule());
        res.setGradient(rec.getGradient());

        String reply = buildChatReply(req.getMessage(), rec);
        if (llmAdvisor != null && llmAdvisor.isEnabled()) {
            String llm = llmAdvisor.advise(
                    "你是专业色彩顾问，用简体中文、亲切专业的语气，给用户的配色方案写一段 2-3 句点评。",
                    "用户需求：" + req.getMessage() + "\n生成的配色（HEX）：" + rec.getPalette() +
                            "\n方案名：" + rec.getName() + "\n和谐规则：" + rec.getHarmonyRule());
            if (llm != null && !llm.trim().isEmpty()) reply = llm.trim();
        }
        res.setReply(reply);
        return res;
    }

    // ---------------- 内部逻辑 ----------------

    private ColorUtils.Hsl resolveBase(RecommendationRequest req) {
        if (req.getBaseColor() != null && ColorUtils.isValidHex(req.getBaseColor())) {
            return ColorUtils.rgbToHsl(ColorUtils.hexToRgb(req.getBaseColor()));
        }
        Integer hue = SCENARIO_HUE.getOrDefault(normalize(req.getScenario()), null);
        if (hue == null) hue = MOOD_HUE.getOrDefault(normalize(req.getMood()), 210);
        return new ColorUtils.Hsl(hue, 70, 50);
    }

    private ColorUtils.Hsl applyStyle(ColorUtils.Hsl base, String style) {
        double[] sl = STYLE.getOrDefault(normalize(style), new double[]{80, 52});
        // 极简情绪强制低饱和、高明度，贴近性冷淡风
        if ("minimal".equals(normalize(style))) sl = new double[]{12, 88};
        return new ColorUtils.Hsl(base.h, sl[0], sl[1]);
    }

    private ColorHarmony.Rule chooseRule(RecommendationRequest req) {
        String mood = normalize(req.getMood());
        if ("minimal".equals(mood)) return ColorHarmony.Rule.MONOCHROMATIC;
        if ("luxury".equals(mood)) return ColorHarmony.Rule.COMPLEMENTARY;
        if ("playful".equals(mood)) return ColorHarmony.Rule.TRIADIC;
        switch (normalize(req.getScenario())) {
            case "web": case "ui": return ColorHarmony.Rule.ANALOGOUS;
            case "fashion": return ColorHarmony.Rule.ANALOGOUS;
            case "interior": return ColorHarmony.Rule.ANALOGOUS;
            case "brand": return ColorHarmony.Rule.COMPLEMENTARY;
            case "poster": return ColorHarmony.Rule.TRIADIC;
            case "food": return ColorHarmony.Rule.COMPLEMENTARY;
            case "nature": return ColorHarmony.Rule.ANALOGOUS;
            case "social": return ColorHarmony.Rule.TRIADIC;
            default: return ColorHarmony.Rule.COMPLEMENTARY;
        }
    }

    private List<String> fitCount(List<String> palette, int count, double baseHue) {
        if (count <= 0) count = 5;
        if (palette.size() == count) return palette;
        List<String> out = new ArrayList<>(palette);
        if (out.size() > count) return new ArrayList<>(out.subList(0, count));
        int step = 1;
        while (out.size() < count) {
            double h = ColorUtils.mod360(baseHue + 30 * step);
            out.add(ColorUtils.rgbToHex(ColorUtils.hslToRgb(new ColorUtils.Hsl(h, 70, 55))));
            step++;
        }
        return out;
    }

    private String buildName(RecommendationRequest req, ColorHarmony.Rule rule) {
        String mood = MOOD_LABEL.getOrDefault(normalize(req.getMood()), "均衡");
        String scene = SCENARIO_LABEL.getOrDefault(normalize(req.getScenario()), "通用");
        return mood + "·" + scene + " " + rule.label + "方案";
    }

    private String buildDescription(RecommendationRequest req, ColorHarmony.Rule rule, List<String> palette) {
        String mood = MOOD_LABEL.getOrDefault(normalize(req.getMood()), "均衡");
        String scene = SCENARIO_LABEL.getOrDefault(normalize(req.getScenario()), "通用");
        String style = normalize(req.getStyle());
        String styleWord = STYLE.containsKey(style) ? (style.equals("dark") ? "暗调" : style.equals("light") ? "明亮" :
                style.equals("pastel") ? "粉彩" : style.equals("muted") ? "柔和" : style.equals("vibrant") ? "鲜艳" : "标准") : "标准";
        return String.format("为「%s」场景打造的%s%s配色，采用%s和谐规则，由 %s 主导，" +
                "主色 %s，整体传达%s的气质，适合落地于界面、品牌或视觉物料。",
                scene, mood, styleWord, rule.label, palette.get(0), palette.get(0), mood);
    }

    private List<String> buildRationale(RecommendationRequest req, ColorHarmony.Rule rule,
                                        ColorUtils.Hsl base, List<String> palette) {
        List<String> r = new ArrayList<>();
        r.add("情绪锚点：以「" + MOOD_LABEL.getOrDefault(normalize(req.getMood()), "均衡") + "」为基调，色相 " +
                Math.round(base.h) + "°，契合目标感受。");
        r.add("和谐规则：" + rule.label + "（" + rule.name() + "），在色环上保持视觉平衡，降低杂乱感。");
        r.add("场景适配：针对「" + SCENARIO_LABEL.getOrDefault(normalize(req.getScenario()), "通用") +
                "」优化——该场景更依赖" + (rule == ColorHarmony.Rule.ANALOGOUS ? "邻近过渡" : "对比张力") + "。");
        String bg = palette.get(0);
        r.add("可读性：主色 " + bg + " 上建议文字用 " + ContrastUtils.bestTextColor(bg) +
                "，对比等级 " + ContrastUtils.wcagLevel(ContrastUtils.bestTextColor(bg), bg) + "。");
        r.add("应用建议：将第 1 个颜色作为主色，第 2 个作辅色，末尾颜色作点缀，可保证层级清晰。");
        return r;
    }

    private List<String> buildAccessibility(List<String> palette) {
        List<String> a = new ArrayList<>();
        for (int i = 0; i < Math.min(palette.size(), 3); i++) {
            String c = palette.get(i);
            String txt = ContrastUtils.bestTextColor(c);
            a.add(c + " → 文字 " + txt + "（" + ContrastUtils.wcagLevel(txt, c) + "）");
        }
        return a;
    }

    private List<RecommendationResponse.Alternative> buildAlternatives(String baseHex, ColorHarmony.Rule chosen) {
        List<RecommendationResponse.Alternative> out = new ArrayList<>();
        int n = 0;
        for (ColorHarmony.Rule r : ColorHarmony.Rule.values()) {
            if (r == chosen) continue;
            out.add(new RecommendationResponse.Alternative(r.name(), r.label, ColorHarmony.generate(baseHex, r)));
            if (++n >= 4) break;
        }
        return out;
    }

    private void enrichWithLlm(RecommendationResponse res, RecommendationRequest req,
                               ColorHarmony.Rule rule, List<String> palette) {
        if (llmAdvisor == null || !llmAdvisor.isEnabled()) return;
        String llm = llmAdvisor.advise(
                "你是专业色彩顾问，用简体中文为配色方案写 3 条要点（每条一句），说明为何这样搭配。",
                "场景：" + req.getScenario() + "，情绪：" + req.getMood() + "，风格：" + req.getStyle() +
                        "，规则：" + rule.label + "，颜色：" + palette);
        if (llm != null && !llm.trim().isEmpty()) {
            List<String> lines = new ArrayList<>();
            for (String line : llm.split("[\\n\\r]+")) {
                line = line.replaceAll("^\\s*\\d+[.、)\\-]\\s*", "").trim();
                if (!line.isEmpty()) lines.add(line);
            }
            if (!lines.isEmpty()) res.setRationale(lines);
        }
    }

    private RecommendationRequest parseFromMessage(String message) {
        RecommendationRequest r = new RecommendationRequest();
        if (message == null) return r;
        String m = message.toLowerCase();
        // 抽取 HEX
        java.util.regex.Matcher mt = java.util.regex.Pattern.compile("#?([0-9a-fA-F]{6})").matcher(message);
        if (mt.find()) r.setBaseColor("#" + mt.group(1));
        // 情绪 / 场景关键词（中英文）
        for (String k : MOOD_HUE.keySet()) if (m.contains(k.toLowerCase())) { r.setMood(k); break; }
        for (String k : SCENARIO_HUE.keySet()) if (m.contains(k.toLowerCase())) { r.setScenario(k); break; }
        // 风格
        for (String k : STYLE.keySet()) if (m.contains(k.toLowerCase())) { r.setStyle(k); break; }
        // 中文关键词兜底
        if (m.contains("温暖") || m.contains("暖")) r.setMood("warm");
        if (m.contains("清凉") || m.contains("冷") || m.contains("凉爽")) r.setMood("cool");
        if (m.contains("高级") || m.contains("奢华") || m.contains("轻奢")) r.setMood("luxury");
        if (m.contains("活泼") || m.contains("活泼") || m.contains("可爱")) r.setMood("playful");
        if (m.contains("极简") || m.contains("简约") || m.contains("性冷淡")) r.setMood("minimal");
        if (m.contains("复古")) r.setMood("vintage");
        if (m.contains("科技") || m.contains("未来")) r.setMood("tech");
        if (m.contains("自然") || m.contains("森林") || m.contains("植物")) r.setMood("nature");
        if (m.contains("浪漫") || m.contains("甜") || m.contains("少女")) r.setMood("romantic");
        if (m.contains("美食") || m.contains("餐饮") || m.contains("咖啡") || m.contains("食物")) r.setScenario("food");
        if (m.contains("品牌")) r.setScenario("brand");
        if (m.contains("海报") || m.contains("宣传")) r.setScenario("poster");
        if (m.contains("网页") || m.contains("网站") || m.contains("ui") || m.contains("界面")) r.setScenario("web");
        if (m.contains("穿搭") || m.contains("时尚") || m.contains("衣服")) r.setScenario("fashion");
        if (m.contains("室内") || m.contains("家居") || m.contains("装修")) r.setScenario("interior");
        return r;
    }

    private String buildChatReply(String message, RecommendationResponse rec) {
        return "已根据你的需求「" + (message == null ? "" : message) + "」生成配色方案「" + rec.getName() +
                "」。主色 " + rec.getPalette().get(0) + "，采用" + rec.getHarmonyRule() + "和谐规则，" +
                "共 " + rec.getPalette().size() + " 个颜色，可直接用于你的设计或继续让我微调。";
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }
}
