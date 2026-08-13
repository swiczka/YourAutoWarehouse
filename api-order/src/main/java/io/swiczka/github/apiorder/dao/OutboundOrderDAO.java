package io.swiczka.github.apiorder.dao;

import io.swiczka.github.apiorder.entity.OutboundOrder;

public interface OutboundOrderDAO {
    void save(OutboundOrder order);
}
