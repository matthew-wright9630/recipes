ALTER TABLE recipe_ingredients
ADD COLUMN notes TEXT;

ALTER TABLE recipe_ingredients
ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0;

CREATE UNIQUE INDEX users_email_lower_unique
ON users (LOWER(email));