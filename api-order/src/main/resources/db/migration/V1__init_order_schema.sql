CREATE TABLE InboundOrders (
     id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     operator_id UUID NOT NULL,
     company_id bigint NOT NULL,
     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status TEXT NOT NULL
);

CREATE INDEX idx_inboundorders_operator_id ON InboundOrders(operator_id);

CREATE TABLE OutboundOrders (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    operator_id UUID NOT NULL,
    company_id bigint NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    status TEXT NOT NULL
);

CREATE INDEX idx_outboundorders_operator_id ON OutboundOrders(operator_id);