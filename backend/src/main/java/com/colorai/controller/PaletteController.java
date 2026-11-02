package com.colorai.controller;

import com.colorai.payload.ApiResponse;
import com.colorai.payload.SavedPaletteRequest;
import com.colorai.payload.SavedPaletteResponse;
import com.colorai.service.PaletteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/palettes")
public class PaletteController {

    private final PaletteService paletteService;

    public PaletteController(PaletteService paletteService) {
        this.paletteService = paletteService;
    }

    private String currentUser(Authentication auth) {
        return auth.getName();
    }

    @GetMapping
    public ApiResponse<Page<SavedPaletteResponse>> list(Authentication auth,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(50, Math.max(1, size)),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.ok(paletteService.list(currentUser(auth), pageable));
    }

    @GetMapping("/recent")
    public ApiResponse<List<SavedPaletteResponse>> recent(Authentication auth,
                                                          @RequestParam(defaultValue = "6") int limit) {
        return ApiResponse.ok(paletteService.recent(currentUser(auth), Math.min(20, Math.max(1, limit))));
    }

    @PostMapping
    public ApiResponse<SavedPaletteResponse> save(Authentication auth, @Valid @RequestBody SavedPaletteRequest req) {
        return ApiResponse.ok("已保存", paletteService.save(currentUser(auth), req));
    }

    @GetMapping("/{id}")
    public ApiResponse<SavedPaletteResponse> get(Authentication auth, @PathVariable Long id) {
        return ApiResponse.ok(paletteService.get(currentUser(auth), id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(Authentication auth, @PathVariable Long id) {
        paletteService.delete(currentUser(auth), id);
        return ApiResponse.ok("已删除");
    }
}
