ALTER TABLE cookbooks DROP COLUMN owner;
ALTER TABLE cookbooks ADD COLUMN TEXT image_url;
ALTER TABLE cookbooks ADD COLUMN TEXT description;
