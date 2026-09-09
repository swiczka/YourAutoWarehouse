ALTER TABLE Packages ADD COLUMN layout_id bigint NOT NULL REFERENCES Layouts(id) ON DELETE CASCADE;

CREATE INDEX idx_packages_layout_id ON Packages(layout_id);
CREATE INDEX idx_packages_layout_inbound_order ON Packages(layout_id, inbound_order_id);
CREATE INDEX idx_packages_layout_outbound_order ON Packages(layout_id, outbound_order_id);
CREATE INDEX idx_packages_layout_status ON Packages(layout_id, status);
