package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.apiwarehouse.entity.Layout;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaLayoutDAO implements LayoutDAO{

    private final EntityManager entityManager;

    @Autowired
    public JpaLayoutDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(Layout layout) {
        entityManager.merge(layout);
    }

    @Override
    public Optional<Layout> findById(Long id) {
        return Optional.of(entityManager.find(Layout.class, id));
    }

    @Override
    public Optional<Layout> findByUserId(UUID userId) {
        List<Layout> layouts = entityManager.createQuery(
                "FROM Layout WHERE userId = :userId " +
                        "ORDER BY createdAt DESC", Layout.class)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .getResultList();
        return layouts.isEmpty() ? Optional.empty() : Optional.of(layouts.getFirst());
    }
}
