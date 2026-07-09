ALTER TABLE recipe_views
ADD COLUMN visitor_id TEXT;

ALTER TABLE recipe_views
ADD CONSTRAINT check_user_or_visitor CHECK (
    (user_id IS NOT NULL AND visitor_id IS NULL)
    OR
    (user_id IS NULL AND visitor_id IS NOT NULL)
);