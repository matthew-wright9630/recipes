DELETE FROM cookbook_recipes
WHERE cookbook_id IN (
    SELECT id
    FROM cookbooks
    WHERE type = 'LIKED_RECIPES'
);

DELETE FROM cookbooks
WHERE type = 'LIKED_RECIPES';

ALTER TABLE cookbooks
DROP CONSTRAINT cookbooks_type_check;

ALTER TABLE cookbooks
ADD CONSTRAINT cookbooks_type_check
CHECK (type IN ('NORMAL', 'BOOKMARK'));