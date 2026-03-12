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


-- INGREDIENTS
CREATE TABLE ingredients (
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR(255) NOT NULL UNIQUE,
    calories            NUMERIC NOT NULL DEFAULT 0,
    total_fat           NUMERIC NOT NULL DEFAULT 0,
    saturated_fat       NUMERIC NOT NULL DEFAULT 0,
    trans_fat           NUMERIC NOT NULL DEFAULT 0,
    cholesterol         NUMERIC NOT NULL DEFAULT 0,
    sodium              NUMERIC NOT NULL DEFAULT 0,
    total_carbohydrates NUMERIC NOT NULL DEFAULT 0,
    dietary_fiber       NUMERIC NOT NULL DEFAULT 0,
    total_sugars        NUMERIC NOT NULL DEFAULT 0,
    protein             NUMERIC NOT NULL DEFAULT 0,
    vitamin_d           NUMERIC NOT NULL DEFAULT 0,
    calcium             NUMERIC NOT NULL DEFAULT 0,
    iron                NUMERIC NOT NULL DEFAULT 0,
    potassium           NUMERIC NOT NULL DEFAULT 0,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    fdcId               BIGINT,
    dataType            VARCHAR(255),
    brandName           VARCHAR(255),
    brandOwner          VARCHAR(255)
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