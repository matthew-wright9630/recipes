ALTER TABLE recipe_ingredients
ALTER COLUMN sort_order DROP NOT NULL;
ALTER TABLE recipe_ingredients
ALTER COLUMN sort_order DROP DEFAULT;