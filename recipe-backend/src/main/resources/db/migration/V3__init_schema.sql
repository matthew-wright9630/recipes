-- ingredient_nutrition_map: add NOT NULL and ON DELETE CASCADE
ALTER TABLE ingredient_nutrition_map
    ALTER COLUMN ingredient_id SET NOT NULL,
    ALTER COLUMN usda_id SET NOT NULL;

ALTER TABLE ingredient_nutrition_map
    DROP CONSTRAINT ingredient_nutrition_map_ingredient_id_fkey,
    ADD CONSTRAINT ingredient_nutrition_map_ingredient_id_fkey
        FOREIGN KEY (ingredient_id) REFERENCES ingredients(id) ON DELETE CASCADE;

ALTER TABLE ingredient_nutrition_map
    DROP CONSTRAINT ingredient_nutrition_map_usda_id_fkey,
    ADD CONSTRAINT ingredient_nutrition_map_usda_id_fkey
        FOREIGN KEY (usda_id) REFERENCES usda_ingredients(id) ON DELETE CASCADE;

-- cookbooks: fix unique constraint and add is_default
ALTER TABLE cookbooks DROP CONSTRAINT unique_owner_name;
ALTER TABLE cookbooks ADD CONSTRAINT unique_owner_name UNIQUE (owner_id, name);
ALTER TABLE cookbooks ADD COLUMN is_default BOOLEAN NOT NULL DEFAULT FALSE;

-- user_auth_providers: new table for OAuth support
CREATE TABLE user_auth_providers (
    id            SERIAL PRIMARY KEY,
    user_id       INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    provider      VARCHAR(50) NOT NULL,
    provider_id   TEXT,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (provider, provider_id)
);