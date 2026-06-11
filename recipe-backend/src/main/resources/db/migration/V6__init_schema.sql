ALTER TABLE recipe_views
ADD COLUMN actor_id TEXT;

CHECK (
    (user_id IS NOT NULL AND visitor_id IS NULL)
    OR
    (user_id IS NULL AND visitor_id IS NOT NULL)
)