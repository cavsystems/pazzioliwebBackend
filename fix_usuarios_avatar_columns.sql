-- Add missing avatar columns to usuarios table
ALTER TABLE usuarios 
ADD COLUMN avatar LONGBLOB,
ADD COLUMN avatar_tipo VARCHAR(100);
