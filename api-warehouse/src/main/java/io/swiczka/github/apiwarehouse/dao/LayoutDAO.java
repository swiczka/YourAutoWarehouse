package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.layout.dto.LayoutOwnerDto;

import java.util.Optional;
import java.util.UUID;

public interface LayoutDAO {
    void save(final Layout layout);
    Optional<Layout> findById(final Long id);
    Optional<LayoutOwnerDto> findOwnerById(final Long id);
    Optional<Layout> findLatestByUserId(final UUID userId);
}
