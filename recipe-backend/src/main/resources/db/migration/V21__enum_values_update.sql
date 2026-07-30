-- Drop unused views
DROP VIEW active_recipes;
DROP VIEW active_cookbooks;

ALTER TABLE users ALTER COLUMN role DROP DEFAULT;
ALTER TABLE users
  ALTER COLUMN role TYPE varchar(20) USING role::text;
ALTER TABLE users ALTER COLUMN role SET DEFAULT 'USER';
ALTER TABLE users
  ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN','USER'));

ALTER TABLE cookbook_access ALTER COLUMN permission DROP DEFAULT;
ALTER TABLE cookbook_access
  ALTER COLUMN permission TYPE varchar(20) USING permission::text;
ALTER TABLE cookbook_access ALTER COLUMN permission SET DEFAULT 'READ';
ALTER TABLE cookbook_access
  ADD CONSTRAINT cookbook_access_permission_check
    CHECK (permission IN ('READ','READ_WRITE','OWNER','REVOKED'));

ALTER TABLE recipes ALTER COLUMN status DROP DEFAULT;
ALTER TABLE recipes
  ALTER COLUMN status TYPE varchar(20) USING status::text;
ALTER TABLE recipes ALTER COLUMN status SET DEFAULT 'DRAFT';
ALTER TABLE recipes
  ADD CONSTRAINT recipes_status_check
    CHECK (status IN ('DRAFT','PUBLISHED','ARCHIVED','REMOVED','SUPERSEDED'));

ALTER TABLE cookbooks ALTER COLUMN type DROP DEFAULT;
ALTER TABLE cookbooks
  ALTER COLUMN type TYPE varchar(20) USING type::text;
ALTER TABLE cookbooks ALTER COLUMN type SET DEFAULT 'NORMAL';
ALTER TABLE cookbooks
  ADD CONSTRAINT cookbooks_type_check
    CHECK (type IN ('NORMAL','LIKED_RECIPES'));

DROP TYPE user_role;
DROP TYPE cookbook_permission;
DROP TYPE recipe_status;
DROP TYPE cookbook_type;