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
    'Bookmarked Recipes',
    'BOOKMARK',
    NOW(),
    NOW(),
    false,
    'A collection of recipes you''ve bookmarked to easily find them again.',
    'bookmarked-recipes'
FROM users u
WHERE NOT EXISTS (
    SELECT 1
    FROM cookbooks c
    WHERE c.owner_id = u.id
      AND c.type = 'BOOKMARK'
);