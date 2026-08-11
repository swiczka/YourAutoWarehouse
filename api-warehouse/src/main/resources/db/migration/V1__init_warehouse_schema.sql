CREATE TABLE Layouts (
     id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     user_id UUID NOT NULL,
     created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
     grid_data JSONB NOT NULL
);

CREATE INDEX idx_layouts_user_uuid ON Layouts(user_id);