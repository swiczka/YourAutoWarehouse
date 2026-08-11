package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.apiwarehouse.entity.Layout;

import java.util.UUID;

public interface LayoutDAO {
    void save(Layout layout);
    Layout findByUserId(UUID userId);
}
