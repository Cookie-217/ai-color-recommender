package com.colorai.service;

import com.colorai.domain.SavedPalette;
import com.colorai.domain.User;
import com.colorai.payload.SavedPaletteRequest;
import com.colorai.payload.SavedPaletteResponse;
import com.colorai.repository.PaletteRepository;
import com.colorai.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaletteService {

    private final PaletteRepository paletteRepository;
    private final UserRepository userRepository;

    public PaletteService(PaletteRepository paletteRepository, UserRepository userRepository) {
        this.paletteRepository = paletteRepository;
        this.userRepository = userRepository;
    }

    private User currentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("用户不存在"));
    }

    public SavedPaletteResponse save(String username, SavedPaletteRequest req) {
        User user = currentUser(username);
        SavedPalette p = new SavedPalette();
        p.setUser(user);
        p.setName(req.getName());
        p.setColors(req.getColors());
        p.setScenario(req.getScenario());
        p.setDescription(req.getDescription());
        p.setTags(req.getTags());
        p.setCreatedAt(LocalDateTime.now());
        p = paletteRepository.save(p);
        return toResponse(p);
    }

    public Page<SavedPaletteResponse> list(String username, Pageable pageable) {
        User user = currentUser(username);
        return paletteRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::toResponse);
    }

    public SavedPaletteResponse get(String username, Long id) {
        SavedPalette p = paletteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("调色板不存在"));
        if (!p.getUser().getUsername().equals(username)) throw new SecurityException("无权访问");
        return toResponse(p);
    }

    public void delete(String username, Long id) {
        SavedPalette p = paletteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("调色板不存在"));
        if (!p.getUser().getUsername().equals(username)) throw new SecurityException("无权访问");
        paletteRepository.delete(p);
    }

    public List<SavedPaletteResponse> recent(String username, int limit) {
        User user = currentUser(username);
        return paletteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .limit(limit).map(this::toResponse).collect(Collectors.toList());
    }

    private SavedPaletteResponse toResponse(SavedPalette p) {
        SavedPaletteResponse r = new SavedPaletteResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setColors(p.getColorList());
        r.setScenario(p.getScenario());
        r.setDescription(p.getDescription());
        r.setTags(p.getTagList());
        r.setUsername(p.getUser().getUsername());
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
