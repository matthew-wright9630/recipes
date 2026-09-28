CREATE TABLE recipe_notes (
    id          SERIAL PRIMARY KEY,
    description TEXT,
    step_number INTEGER NOT NULL,
    recipe_id   INTEGER REFERENCES recipes(id) ON DELETE CASCADE
);