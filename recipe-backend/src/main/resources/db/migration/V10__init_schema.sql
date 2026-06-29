ALTER TABLE recipes
RENAME COLUMN  parent_recipe_id TO root_recipe_id;

ALTER TABLE recipes
RENAME CONSTRAINT recipes_parent_recipe_id_fkey
TO recipes_root_recipe_id_fkey;