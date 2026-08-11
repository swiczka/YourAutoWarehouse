package io.swiczka.github.apiwarehouse.dao;

import io.swiczka.github.apiwarehouse.entity.Layout;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public class JpaLayoutDAO implements LayoutDAO{

    private final EntityManager entityManager;

    @Autowired
    public JpaLayoutDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional //spring.framework transactional
    public void save(Layout layout) {
        entityManager.persist(layout);
    }

    @Override
    public Layout findByUserId(UUID userId) {
        TypedQuery<Layout> query = entityManager.createQuery(
                "FROM Layout WHERE userId = :userId " +
                        "ORDER BY createdAt DESC", Layout.class)
                .setParameter("userId", userId);
        try {
            Layout latestLayout = query.getResultList().getFirst();
            return latestLayout;
        } catch (Exception e) {
            return null;
        }

    }
}
