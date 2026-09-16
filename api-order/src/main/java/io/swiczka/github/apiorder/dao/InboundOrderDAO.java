package io.swiczka.github.apiorder.dao;

import io.swiczka.github.apiorder.entity.InboundOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InboundOrderDAO {
    void save(InboundOrder order);
    List<InboundOrder> getInboundByUser(UUID userId);
    List<InboundOrder> getInboundByLayoutAndUser(Long layoutId, UUID userId);
    Optional<InboundOrder> getInboundById(Long id);
}
