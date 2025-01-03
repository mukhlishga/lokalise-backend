CREATE TABLE IF NOT EXISTS pages
(
    id BIGSERIAL,
    name VARCHAR NOT NULL,
    image_link VARCHAR,
    annotated_image_link VARCHAR,
    locale jsonb NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    PRIMARY KEY (id)
);
