package io.swiczka.github.apiorder.dao;

import io.swiczka.github.apiorder.entity.OutboundOrder;

import java.util.List;
import java.util.UUID;

public interface OutboundOrderDAO {
    void save(OutboundOrder order);
    List<OutboundOrder> getOutboundByUser(UUID userId);
}
