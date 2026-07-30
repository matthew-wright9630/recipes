INSERT INTO cookbook_access (
    cookbook_id,
    user_id,
    permission,
    granted_at
)
SELECT
    c.id,
    c.owner_id,
    'OWNER'::cookbook_permission,
    NOW()
FROM cookbooks c
WHERE c.type = 'LIKED_RECIPES'
  AND NOT EXISTS (
      SELECT 1
      FROM cookbook_access ca
      WHERE ca.cookbook_id = c.id
        AND ca.user_id = c.owner_id
  );