ALTER TABLE cookbook_recipes
ADD CONSTRAINT unique_cookbook_recipe
UNIQUE (cookbook_id, recipe_id);

INSERT INTO cookbooks (
    owner_id,
    name,
    type,
    created_at,
    updated_at,
    deleted,
    description,
    image_url
)
SELECT
    u.id,
    'Liked Recipes',
    'LIKED_RECIPES'::cookbook_type,
    NOW(),
    NOW(),
    false,
    'A collection of recipes you''ve liked, making it easy to find your favorites again.',
    'liked-recipes'
FROM users u
WHERE NOT EXISTS (
    SELECT 1
    FROM cookbooks c
    WHERE c.owner_id = u.id
      AND c.type = 'LIKED_RECIPES'
);

INSERT INTO cookbook_recipes (
    cookbook_id,
    recipe_id,
    added_at
)
SELECT
    c.id,
    rl.recipe_id,
    NOW()
FROM recipe_like rl
JOIN cookbooks c
    ON c.owner_id = rl.user_id
   AND c.type = 'LIKED_RECIPES'
WHERE NOT EXISTS (
    SELECT 1
    FROM cookbook_recipes cr
    WHERE cr.cookbook_id = c.id
      AND cr.recipe_id = rl.recipe_id
);
