CREATE TYPE cookbook_type AS ENUM ('NORMAL', 'LIKED_RECIPES');

ALTER TABLE cookbooks ADD COLUMN type cookbook_type NOT NULL DEFAULT 'NORMAL';
-- UPDATE cookbooks SET type = 'NORMAL';