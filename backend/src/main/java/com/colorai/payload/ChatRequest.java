package com.colorai.payload;

import java.util.List;

public class ChatRequest {
    /** 用户的自然语言描述，如"帮我做一个温暖高级感的咖啡品牌配色" */
    private String message;
    /** 可选：对话上下文已有的颜色 */
    private List<String> contextColors;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<String> getContextColors() { return contextColors; }
    public void setContextColors(List<String> contextColors) { this.contextColors = contextColors; }
}
