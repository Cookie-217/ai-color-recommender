package com.colorai.controller;

import com.colorai.payload.ApiResponse;
import com.colorai.payload.AuthRequest;
import com.colorai.payload.JwtResponse;
import com.colorai.payload.RegisterRequest;
import com.colorai.service.AuthService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req);
        return ApiResponse.ok("注册成功，请登录");
    }

    @PostMapping("/login")
    public ApiResponse<JwtResponse> login(@Valid @RequestBody AuthRequest req) {
        return ApiResponse.ok(authService.login(req));
    }
}
