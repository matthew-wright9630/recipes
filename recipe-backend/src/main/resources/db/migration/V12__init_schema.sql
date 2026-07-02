ALTER TABLE recipes
ADD COLUMN image_url TEXT;

UPDATE recipes SET image_url = 'food-PLACEHOLDER.jpg' WHERE image_url is NULL;

ALTER TABLE recipes
ALTER COLUMN image_url SET NOT NULL;
