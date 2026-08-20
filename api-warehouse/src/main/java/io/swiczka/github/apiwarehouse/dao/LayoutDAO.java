package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.apiwarehouse.entity.Layout;

import java.util.Optional;
import java.util.UUID;

public interface LayoutDAO {
    void save(Layout layout);
    Optional<Layout> findById(Long id);
    Optional<Layout> findByUserId(UUID userId);
}
