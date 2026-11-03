package com.colorai.controller;

import com.colorai.payload.ApiResponse;
import com.colorai.service.ColorService;
import com.colorai.service.ImageColorService;
import com.colorai.util.ColorHarmony;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/color")
public class ColorController {

    private final ColorService colorService;
    private final ImageColorService imageColorService;

    public ColorController(ColorService colorService, ImageColorService imageColorService) {
        this.colorService = colorService;
        this.imageColorService = imageColorService;
    }

    @GetMapping("/convert")
    public ApiResponse<?> convert(@RequestParam String hex) {
        return ApiResponse.ok(colorService.convert(hex));
    }

    @GetMapping("/contrast")
    public ApiResponse<?> contrast(@RequestParam String fg, @RequestParam String bg) {
        return ApiResponse.ok(colorService.contrast(fg, bg));
    }

    @GetMapping("/harmony")
    public ApiResponse<?> harmony(@RequestParam String base, @RequestParam(defaultValue = "COMPLEMENTARY") String rule) {
        return ApiResponse.ok(colorService.harmony(base, rule));
    }

    @GetMapping("/all")
    public ApiResponse<?> all(@RequestParam String base) {
        return ApiResponse.ok(colorService.all(base));
    }

    @GetMapping("/gradient")
    public ApiResponse<?> gradient(@RequestParam String from, @RequestParam String to,
                                   @RequestParam(defaultValue = "6") int steps) {
        return ApiResponse.ok(colorService.gradient(from, to, Math.min(24, Math.max(2, steps))));
    }

    @GetMapping("/rules")
    public ApiResponse<?> rules() {
        List<String> names = java.util.Arrays.stream(ColorHarmony.Rule.values())
                .map(Enum::name).collect(java.util.stream.Collectors.toList());
        return ApiResponse.ok(names);
    }

    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> extract(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(imageColorService.extract(file, false));
    }

    @PostMapping(value = "/brand", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> brand(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(imageColorService.extract(file, true));
    }
}
