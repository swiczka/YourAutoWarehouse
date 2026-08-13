CREATE TABLE Packages (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    inbound_order_id bigint NOT NULL,
    outbound_order_id bigint,
    x int,
    y int
);

CREATE INDEX idx_packages_inbound_order_id ON Packages(inbound_order_id);
CREATE INDEX idx_packages_outbound_order_id ON Packages(outbound_order_id);