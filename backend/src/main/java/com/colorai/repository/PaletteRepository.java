package com.colorai.repository;

import com.colorai.domain.SavedPalette;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaletteRepository extends JpaRepository<SavedPalette, Long> {
    Page<SavedPalette> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<SavedPalette> findByUserIdOrderByCreatedAtDesc(Long userId);
    void deleteByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}
