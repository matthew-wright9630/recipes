ALTER TABLE recipe_ingredients
ADD COLUMN notes TEXT;

ALTER TABLE recipe_ingredients
ADD COLUMN sort_order INTEGER NOT NULL DEFAULT 0;