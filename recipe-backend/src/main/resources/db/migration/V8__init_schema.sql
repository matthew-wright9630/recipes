ALTER TABLE ingredients
ADD COLUMN normalized_name TEXT;

UPDATE ingredients
SET normalized_name = LOWER(
    REGEXP_REPLACE(
        TRIM(name),
        '\s+',
        ' ',
        'g'
    )
);

ALTER TABLE ingredients
ALTER COLUMN normalized_name SET NOT NULL;

CREATE UNIQUE INDEX ux_ingredients_normalized_name
ON ingredients(normalized_name);

-- Drop existing composite primary key first
ALTER TABLE recipe_ingredients
DROP CONSTRAINT recipe_ingredients_pkey;

-- Then add new id column as primary key
ALTER TABLE recipe_ingredients
ADD COLUMN id SERIAL PRIMARY KEY;