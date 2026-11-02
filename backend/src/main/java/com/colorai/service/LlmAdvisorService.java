package com.colorai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * 可选的大模型顾问：当配置了兼容 OpenAI 的 API（环境变量）时，
 * 用其生成更自然的配色解读；未配置或调用失败时返回 null，由规则引擎兜底。
 */
@Service
public class LlmAdvisorService {

    @Value("${app.llm.enabled:false}")
    private boolean enabled;

    @Value("${app.llm.base-url:https://api.openai.com/v1}")
    private String baseUrl;

    @Value("${app.llm.api-key:}")
    private String apiKey;

    @Value("${app.llm.model:gpt-4o-mini}")
    private String model;

    @Value("${app.llm.timeout-ms:8000}")
    private int timeoutMs;

    private final RestTemplate rest = new RestTemplate();

    public boolean isEnabled() {
        return enabled && apiKey != null && !apiKey.isEmpty();
    }

    /**
     * 向大模型发起一次对话补全。失败返回 null（调用方应兜底）。
     */
    public String advise(String systemPrompt, String userPrompt) {
        if (!isEnabled()) return null;
        try {
            String url = baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";
            Map<String, Object> msgSys = new HashMap<>();
            msgSys.put("role", "system");
            msgSys.put("content", systemPrompt);
            Map<String, Object> msgUser = new HashMap<>();
            msgUser.put("role", "user");
            msgUser.put("content", userPrompt);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", Arrays.asList(msgSys, msgUser));
            body.put("temperature", 0.7);
            body.put("max_tokens", 400);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            rest.getInterceptors().clear();
            ResponseEntity<Map> resp = rest.exchange(url, HttpMethod.POST, entity, Map.class);
            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) resp.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
        } catch (RestClientException | ClassCastException e) {
            // 静默失败，交由规则引擎兜底
            return null;
        }
        return null;
    }
}
