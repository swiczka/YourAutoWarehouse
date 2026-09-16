package io.swiczka.github.apiorder.dao;

import io.swiczka.github.apiorder.entity.InboundOrder;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaInboundOrderDAO implements InboundOrderDAO {

    private final EntityManager entityManager;

    @Autowired
    public JpaInboundOrderDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void save(InboundOrder order) {
        if(order.getId() == null){
            entityManager.persist(order);
            return;
        }
        entityManager.merge(order);
    }

    @Override
    public List<InboundOrder> getInboundByUser(UUID userId) {
        List<InboundOrder> orders = entityManager.createQuery(
                "FROM InboundOrder io WHERE io.operatorId=:userId", InboundOrder.class)
                .setParameter("userId", userId)
                .getResultList();
        return orders;
    }

    @Override
    public List<InboundOrder> getInboundByLayoutAndUser(final Long layoutId, final UUID userId) {
        final List<InboundOrder> orders = entityManager.createQuery(
                "FROM InboundOrder io WHERE io.layoutId = :layoutId AND io.operatorId = :userId",
                InboundOrder.class)
                .setParameter("layoutId", layoutId)
                .setParameter("userId", userId)
                .getResultList();
        return orders;
    }

    @Override
    public Optional<InboundOrder> getInboundById(Long id) {
        return Optional.ofNullable(entityManager.find(InboundOrder.class, id));
    }
}
