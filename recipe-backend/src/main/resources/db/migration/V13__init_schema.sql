CREATE TABLE recipe_like (
    user_id BIGINT REFERENCES users(id),
    recipe_id BIGINT REFERENCES recipe(id),
    created_at TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, recipe_id)
);