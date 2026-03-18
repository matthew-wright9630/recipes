-- ENUM Types
CREATE TYPE user_role AS ENUM ('Admin', 'User');
CREATE TYPE cookbook_permission AS ENUM ('read', 'read_write');


-- USERS
CREATE TABLE users (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    role        user_role NOT NULL DEFAULT 'User',
    deactivated BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE ingredients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- INGREDIENTS
CREATE TABLE usda_ingredients (
    id SERIAL PRIMARY KEY,
    fdc_id BIGINT UNIQUE,
    name TEXT,
    calories NUMERIC,
    total_fat NUMERIC,
    saturated_fat NUMERIC,
    trans_fat NUMERIC,
    cholesterol NUMERIC,
    sodium NUMERIC,
    total_carbohydrates NUMERIC,
    dietary_fiber NUMERIC,
    total_sugars NUMERIC,
    protein NUMERIC,
    vitamin_d NUMERIC,
    calcium NUMERIC,
    iron NUMERIC,
    potassium NUMERIC,
    datatype VARCHAR(50),
    brandName VARCHAR(255),
    brandOwner VARCHAR(255)
);

-- Mapping table to link our ingredients to USDA data (if available)
CREATE TABLE ingredient_nutrition_map (
    ingredient_id INTEGER REFERENCES ingredients(id),
    usda_id INTEGER REFERENCES usda_ingredients(id),
    PRIMARY KEY (ingredient_id, usda_id)
);

-- RECIPES
CREATE TABLE recipes (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    notes       TEXT,
    servings    INTEGER,
    prep_time   INTEGER,   -- in minutes
    cook_time   INTEGER,   -- in minutes
    created_by  INTEGER REFERENCES users(id) ON DELETE SET NULL,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    version     INTEGER NOT NULL DEFAULT 1,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Recipe <-> Ingredient join table (the "list of ingredients" for a recipe)
CREATE TABLE recipe_ingredients (
    recipe_id     INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    ingredient_id INTEGER NOT NULL REFERENCES ingredients(id) ON DELETE RESTRICT,
    quantity      NUMERIC,
    unit          VARCHAR(50),
    PRIMARY KEY (recipe_id, ingredient_id)
);


-- COOKBOOKS
CREATE TABLE cookbooks (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    owner_id   INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Cookbook <-> Recipe join table
CREATE TABLE cookbook_recipes (
    cookbook_id INTEGER NOT NULL REFERENCES cookbooks(id) ON DELETE CASCADE,
    recipe_id   INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    added_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (cookbook_id, recipe_id)
);

-- Cookbook sharing + permissions
CREATE TABLE cookbook_access (
    cookbook_id INTEGER NOT NULL REFERENCES cookbooks(id) ON DELETE CASCADE,
    user_id     INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    permission  cookbook_permission NOT NULL DEFAULT 'read',
    granted_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (cookbook_id, user_id)
);

CREATE VIEW active_cookbooks AS
  SELECT * FROM cookbooks WHERE deleted = FALSE;

CREATE VIEW active_recipes AS
  SELECT * FROM recipes WHERE deleted = FALSE;