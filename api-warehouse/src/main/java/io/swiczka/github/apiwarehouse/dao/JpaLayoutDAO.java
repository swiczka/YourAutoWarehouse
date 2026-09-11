package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.apiwarehouse.entity.Layout;
import io.swiczka.github.apiwarehouse.layout.dto.LayoutOwnerDto;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaLayoutDAO implements LayoutDAO {

    private final EntityManager entityManager;

    @Autowired
    public JpaLayoutDAO(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(final Layout layout) {
        if (layout.getId() == null) {
            entityManager.persist(layout);
            return;
        }
        entityManager.merge(layout);
    }

    @Override
    public Optional<Layout> findById(final Long id) {
        return Optional.ofNullable(entityManager.find(Layout.class, id));
    }

    @Override
    public Optional<LayoutOwnerDto> findOwnerById(final Long id) {
        final List<LayoutOwnerDto> results = entityManager.createQuery(
                "SELECT new io.swiczka.github.apiwarehouse.layout.dto.LayoutOwnerDto(l.id, l.userId) " +
                        "FROM Layout l WHERE l.id = :id", LayoutOwnerDto.class)
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.getFirst());
    }

    @Override
    public Optional<Layout> findLatestByUserId(final UUID userId) {
        final List<Layout> layouts = entityManager.createQuery(
                "FROM Layout WHERE userId = :userId " +
                        "ORDER BY createdAt DESC", Layout.class)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .getResultList();
        if (layouts.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(layouts.getFirst());
    }
}
