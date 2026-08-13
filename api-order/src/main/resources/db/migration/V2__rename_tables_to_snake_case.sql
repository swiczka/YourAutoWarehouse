ALTER TABLE InboundOrders RENAME TO inbound_orders;
ALTER INDEX idx_inboundorders_operator_id RENAME TO idx_inbound_orders_operator_id;

ALTER TABLE OutboundOrders RENAME TO outbound_orders;
ALTER INDEX idx_outboundorders_operator_id RENAME TO idx_outbound_orders_operator_id;
