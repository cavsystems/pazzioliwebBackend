# Seed del template de tenants (`_tenant_template`)

Regenera el schema plantilla `_tenant_template` como una **empresa en blanco correcta**: estructura
actual completa + datos maestros + config por defecto, con las tablas transaccionales vacías.
Cada empresa nueva se **clona** de este template (`TenantService.clonarSchemaDesdeTemplate`), así que
lo que esté aquí es lo que reciben todas las empresas nuevas.

## Por qué
El template estaba desactualizado: le faltaban ~41 tablas y varias tablas existentes tenían columnas
viejas (cambios aplicados a los tenants sin migración). Además faltaban los datos maestros
(precios, unidades, clasificaciones, tipo_contacto, tipo_producto, PUC, métodos de pago, etc.) y las
config single-row (`configuracion_contabilidad`, `configuracion_compras`). Una empresa creada así salía rota.

## Archivos (aplicar EN ESTE ORDEN)
1. `00_estructura_completa.sql` — estructura de TODAS las tablas (DROP+CREATE). Toma la estructura
   actual de `cavsystems`. **Destructivo para las tablas del template** (no toca `flyway_schema_history`).
2. `02_datos_maestros.sql` — datos de las tablas MAESTRAS (catálogos genéricos + PUC + permisos/roles).
   `INSERT IGNORE`. NO incluye datos transaccionales ni de ninguna empresa.
3. `03_config_defaults.sql` — `configuracion_contabilidad` (activa=1), `configuracion_compras`
   (sin cajero) y `metodos_pago` base (sin cuentas). Saneado, sin datos company-specific.

## Cómo aplicar (local o servidor)
```bash
mysql -u root -p _tenant_template < 00_estructura_completa.sql
mysql -u root -p _tenant_template < 02_datos_maestros.sql
{ echo "SET FOREIGN_KEY_CHECKS=0;"; cat 03_config_defaults.sql; } | mysql -u root -p _tenant_template
```
Luego **reiniciar el backend**: `TenantTemplateInitializer` corre Flyway y aplica `V2`
(`configuracion_contabilidad`) de forma idempotente.

## Notas
- `configuracion_contable_mapa` queda vacía: el mapeo de cuentas clave del PUC lo configura cada
  empresa **con contabilidad** (paso de setup contable). Las cuentas del PUC sí quedan sembradas.
- Las empresas POS puro reciben el PUC igual (inofensivo, oculto por UI); no generan asientos.
- Los TENANTS YA EXISTENTES no se tocan con esto (siguen con su estructura). Para que un tenant
  existente pueda pasar a POS, aplicar `db_migrations/2026_07_18_configuracion_contabilidad_modopos.sql`.
- Fuente de verdad de la estructura/maestras: `cavsystems`. Si cambia el esquema, regenerar `00`/`02`.
