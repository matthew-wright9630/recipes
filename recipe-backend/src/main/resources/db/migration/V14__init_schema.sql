CREATE TABLE user_image (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    base_key VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);