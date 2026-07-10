-- ═══════════════════════════════════════════════════════════════════════
-- Centros de costo — Fase 2
-- Aplicar en CADA schema de tenant (cavsystems, etc.). NO en 'administrador'.
-- Aditivo: la columna estado se agrega con DEFAULT 'ACTIVO', las filas
-- existentes quedan activas automáticamente.
-- ═══════════════════════════════════════════════════════════════════════
USE cavsystems;

ALTER TABLE centrocosto
    ADD COLUMN estado VARCHAR(15) NOT NULL DEFAULT 'ACTIVO';
