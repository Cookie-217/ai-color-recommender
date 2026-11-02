package com.colorai.service;

import com.colorai.domain.User;
import com.colorai.payload.AuthRequest;
import com.colorai.payload.JwtResponse;
import com.colorai.payload.RegisterRequest;
import com.colorai.repository.UserRepository;
import com.colorai.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public void register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("用户名已被占用");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("邮箱已被注册");
        User user = new User(req.getUsername(), req.getEmail(),
                passwordEncoder.encode(req.getPassword()));
        user.setDisplayName(req.getDisplayName() == null || req.getDisplayName().isEmpty()
                ? req.getUsername() : req.getDisplayName());
        userRepository.save(user);
    }

    public JwtResponse login(AuthRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = tokenProvider.generateToken(auth);
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        List<String> roles = Collections.singletonList("ROLE_USER");
        return new JwtResponse(token, user.getUsername(), user.getEmail(),
                user.getDisplayName(), roles);
    }
}
