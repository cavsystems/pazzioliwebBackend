# Cómo hacer un cambio de esquema (Flyway, todos los tenants)

## Qué resuelve esto

Antes, un cambio de base de datos se aplicaba a mano por tenant (`db_migrations/*.sql`), y cada
desarrollador terminaba con su schema local desincronizado del otro y del servidor. Ahora **todo cambio de
esquema es un archivo Flyway versionado**, y se aplica solo — a `_tenant_template` y a **todos los tenants
reales** (`cavsystems`, cualquier empresa nueva, etc.) — cada vez que arranca el backend, local o en
servidor. Ya no se edita ni se corre nada a mano.

## Dónde va un cambio nuevo

`common/src/main/resources/db/migration/common/Vn__descripcion_corta.sql`

- `n` = el próximo número libre. Hoy el último es `V10`, así que el siguiente cambio es `V11`. (Hay un
  hueco en `V6` a propósito, Flyway lo tolera — no hay que llenarlo.)
- Un archivo, un cambio. Nombre en snake_case, descriptivo (`V11__agregar_columna_x_terceros.sql`).

## Cómo se aplica

Al arrancar el backend, `TenantTemplateInitializer` (`common/.../services/TenantTemplateInitializer.java`):
1. Migra `_tenant_template` a la última versión (así toda empresa nueva se crea ya al día).
2. Llama a `TenantMigrationRunner.migrateAllTenants()` (`common/.../services/TenantMigrationRunner.java`),
   que enumera todos los schemas reales (`INFORMATION_SCHEMA.SCHEMATA`, excluyendo los de sistema,
   `administrador` y el template) y por cada uno aplica los `Vn` pendientes.
3. Si un tenant falla, se loguea `[TenantMigration] <schema> -> FALLÓ, ...` y se sigue con el resto — un
   tenant roto no tumba el arranque ni bloquea a los demás.

Revisá el log después de arrancar: cada tenant real debe aparecer como
`[TenantMigration] <schema> -> OK, N migraciones aplicadas`.

## Reglas para escribir un `Vn` nuevo

Estos `Vn` corren de verdad contra tenants reales con datos (no solo contra el template desechable), así
que:

1. **Nunca `DROP TABLE` / `CREATE TABLE` sobre una tabla que ya tiene datos.** Eso es lo que hacen `V1`/`V2`
   (son un volcado inicial, ya "quemados") — un `Vn` nuevo solo usa `ALTER TABLE`, `CREATE INDEX`,
   `INSERT`, etc.
2. **MySQL no soporta `CREATE INDEX ... IF NOT EXISTS` ni `ALTER TABLE ... ADD INDEX IF NOT EXISTS`**
   (se probó en 8.0.45: da error de sintaxis). Para que un índice/constraint sea idempotente, usar el
   patrón de chequeo + SQL dinámico que ya usan `V8`/`V9`/`V10`:
   ```sql
   SET @idx := (SELECT COUNT(*) FROM information_schema.STATISTICS
                WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'mi_tabla' AND INDEX_NAME = 'mi_indice');
   SET @sql := IF(@idx = 0, 'CREATE INDEX mi_indice ON mi_tabla (columna)', 'SELECT 1');
   PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
   ```
3. **Datos de catálogo/maestros: siempre `INSERT IGNORE` sobre una clave única**, nunca `INSERT` a secas
   (se ejecuta una sola vez por schema en teoría, pero si alguna vez se re-corre a mano no debe duplicar).
4. **Nunca tocar data transaccional de una empresa** (ventas, compras, facturas, kardex...) — eso es de
   cada tenant, no de la migración.
5. **Un `Vn` que ya se mergeó a `main` es inmutable.** Si estaba mal, se corrige con un `Vn+1` nuevo, nunca
   editando el archivo viejo (eso rompe el checksum de Flyway en todo el que ya lo corrió — ver más abajo).

## Cómo probarlo antes de hacer commit

1. Escribí el `Vn__....sql`.
2. Arrancá el backend local. Mirá el log: tu schema (`cavsystems` u otro) debe pasar de la versión
   anterior a la nueva sin error.
3. Verificá en MySQL que el cambio quedó (columna/índice/dato esperado).
4. Hacé commit del archivo. El otro dev, al hacer `git pull` y arrancar su backend, recibe el mismo cambio
   automáticamente — no hay que mandarle un dump ni un `.sql` para que lo corra a mano.

## Si un tenant falla al migrar

El log dice por qué. Causas típicas ya vistas:
- **El schema no tiene la estructura que el baseline asume** (p.ej. `newschema`, casi vacío) — no es un
  tenant real, se puede ignorar o recrear.
- **El schema ya tenía un `flyway_schema_history` de un experimento previo, con checksums que no calzan
  con los `Vn` actuales** (p.ej. `db_metrolinksas`) — Flyway se frena a propósito en vez de adivinar. Si es
  un schema local sin datos que valga la pena conservar: dropearlo y recrearlo clonando `_tenant_template`
  (ya queda con estructura + tablas maestras correctas). Si es un tenant real de un cliente: investigar
  antes de tocarlo (no usar `flyway.repair()` a la ligera — repara los checksums, no la estructura).

## Primer rollout en un ambiente que nunca corrió esto (servidor)

Cualquier tenant real sin `flyway_schema_history` se baselinea automáticamente en `V7` (el último `Vn`
"destructivo" tipo mysqldump) y de ahí en más `V8+` se ejecutan de verdad. Antes de la primera vez que esto
corra contra el servidor: sacar un `mysqldump` de respaldo de cada tenant real (Flyway no tiene rollback
automático — un `Vn` malo se corrige hacia adelante con un `Vn+1`, nunca editando historial).
