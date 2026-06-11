CREATE TABLE recipe_views (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT DEFAULT NULL,
    recipe_id BIGINT NOT NULL,
    viewed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_recipe_views_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_recipe_views_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipes(id)
);

CREATE INDEX idx_recipe_views_user_viewed_at
    ON recipe_views(user_id, viewed_at DESC);

CREATE INDEX idx_recipe_views_recipe_viewed_at
    ON recipe_views(recipe_id, viewed_at DESC);
