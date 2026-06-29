ALTER TABLE recipe_directions
DROP CONSTRAINT recipe_directions_recipe_id_fkey;

ALTER TABLE recipe_directions
ADD CONSTRAINT recipe_directions_recipe_id_fkey
FOREIGN KEY (recipe_id)
REFERENCES recipes(id)
ON DELETE CASCADE;

ALTER TABLE recipe_ingredients
DROP CONSTRAINT recipe_ingredients_recipe_id_fkey;

ALTER TABLE recipe_ingredients
ADD CONSTRAINT recipe_ingredients_recipe_id_fkey
FOREIGN KEY (recipe_id)
REFERENCES recipes(id)
ON DELETE CASCADE;