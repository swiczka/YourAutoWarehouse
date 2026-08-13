package io.swiczka.github.apiorder.dao;

import io.swiczka.github.apiorder.entity.OutboundOrder;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JpaOutboundOrderDAO implements OutboundOrderDAO{

    private final EntityManager entityManager;

    @Autowired
    public JpaOutboundOrderDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(OutboundOrder order) {
        entityManager.persist(order);
    }
}
