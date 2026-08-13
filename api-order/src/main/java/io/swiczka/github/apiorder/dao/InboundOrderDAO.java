package io.swiczka.github.apiorder.dao;

import io.swiczka.github.apiorder.entity.InboundOrder;

import java.util.List;
import java.util.UUID;

public interface InboundOrderDAO {
    void save(InboundOrder order);
    List<InboundOrder> getInboundByUser(UUID userId);
}
