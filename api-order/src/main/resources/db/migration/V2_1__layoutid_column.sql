ALTER TABLE inbound_orders ADD COLUMN layout_id bigint NOT NULL default -1;
ALTER TABLE outbound_orders ADD COLUMN layout_id bigint NOT NULL default -1;

CREATE INDEX idx_inboundorders_layout_id ON inbound_orders(layout_id);
CREATE INDEX idx_outboundorders_layout_id ON outbound_orders(layout_id);