package io.swiczka.github.apiorder.dao;

import io.swiczka.github.apiorder.entity.OutboundOrder;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

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

    @Override
    public List<OutboundOrder> getOutboundByUser(UUID userId) {
        List<OutboundOrder> orders = entityManager.createQuery(
                        "FROM OutboundOrder io WHERE io.operatorId=:userId", OutboundOrder.class)
                .setParameter("userId", userId)
                .getResultList();
        return orders;
    }

    @Override
    public List<OutboundOrder> getOutboundByLayoutAndUser(final Long layoutId, final UUID userId) {
        final List<OutboundOrder> orders = entityManager.createQuery(
                        "FROM OutboundOrder io WHERE io.layoutId = :layoutId AND io.operatorId = :userId",
                        OutboundOrder.class)
                .setParameter("layoutId", layoutId)
                .setParameter("userId", userId)
                .getResultList();
        return orders;
    }
}
