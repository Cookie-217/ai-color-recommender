package com.colorai.controller;

import com.colorai.payload.ApiResponse;
import com.colorai.payload.ChatRequest;
import com.colorai.payload.ChatResponse;
import com.colorai.payload.RecommendationRequest;
import com.colorai.payload.RecommendationResponse;
import com.colorai.service.PaletteAgentService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final PaletteAgentService agentService;

    public AgentController(PaletteAgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/recommend")
    public ApiResponse<RecommendationResponse> recommend(@Valid @RequestBody RecommendationRequest req) {
        return ApiResponse.ok(agentService.recommend(req));
    }

    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest req) {
        return ApiResponse.ok(agentService.chat(req));
    }
}
