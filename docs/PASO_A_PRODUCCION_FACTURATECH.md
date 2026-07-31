# Paso a producción — Facturación Electrónica DIAN vía Facturatech

**Sistema:** Pazzioli WEB (backend Spring Boot multi-tenant + POS React)
**Proveedor tecnológico:** Facturatech (SOAP, insumo XML_SIMPLIFICADO Anexo 1.9)
**Estado actual:** certificado en SET de pruebas C1 (factura de venta contado y crédito emitidas y AUTORIZADAS de punta a punta: POS → folio → XML → Facturatech → CUFE/QR en tirilla). NC y ND implementadas y auditadas contra los manuales, pendientes de prueba en el set.
**Fecha:** 2026-07-31

> **Idea central: el código NO se toca para pasar a producción.** Todo el paso es
> configuración + datos maestros + trámites. Fue un requisito de diseño y está
> verificado por auditoría de código.

---

## 1. Trámites previos (DIAN + Facturatech)

| # | Qué | Con quién | Notas |
|---|-----|-----------|-------|
| 1 | Resolución de facturación electrónica (prefijo, rango, vigencia, **clave técnica**) | DIAN (MUISCA) | La clave técnica es de la resolución de FACTURA. NC/ND no llevan rango DIAN propio, pero sí prefijo. |
| 2 | Registro de la resolución y los prefijos en la plataforma Facturatech | soportews@facturatech.co | **El nodo DRF del XML debe coincidir EXACTO con lo registrado en su plataforma o rechazan con 409.** Los prefijos son obligatorios y **no deben terminar en número**. |
| 3 | Credenciales del **Web Service de producción** | soportews@facturatech.co | Usuario = NIT sin DV. La contraseña WS **NO es la del portal web**. |
| 4 | Proceso de habilitación DIAN: set de habilitación con los **datos reales** de la empresa | Facturatech guía el proceso | El C1 actual usa el NIT demo 901143311. La habilitación se hace con el NIT real; al aprobar, la DIAN asocia el modo de operación "software de proveedor tecnológico" en el MUISCA. |
| 5 | (Opcional) Set aparte para **Documento Soporte** | soportews | El set C1 entregado NO incluye DS ni POS. Si van a comprar a no obligados a facturar, pedir ese set antes de usar DS en producción. |
| 6 | **Tiquete POS electrónico (tipo 20): NO disponible** | — | Facturatech aún no provee el WS del documento equivalente POS. El sistema lo tiene bloqueado por flag para no quemar folios. Toda venta sale como factura electrónica (tipo 01). |

**Sobre el QR:** en el set de pruebas el QR apunta al catálogo de la DIAN y responde
"documento no encontrado" — **eso es normal**, el sandbox no transmite a la DIAN. En
habilitación/producción el QR sí resuelve en `catalogo-vpfe.dian.gov.co`.

---

## 2. Cambios de configuración (lo ÚNICO que se cambia en el software)

Archivo: `web/src/main/resources/application.properties` (o el perfil de producción).

```properties
# ── Proveedor activo ──
facturacion.proveedor=facturatech

# ── PRODUCCIÓN ──
facturatech.ambiente=1                       # 1=producción (hoy: 2=demo)
facturatech.usuario=<NIT_SIN_DV>             # hoy: Financy31052023 (demo)
facturatech.password=<CONTRASENA_WS>         # se hashea SHA-256 automáticamente
facturatech.password-sha256=                 # VACIAR (solo se usa si la entregan ya hasheada)

# ── CRÍTICO: apagar el override del set de pruebas ──
# Si queda en true, TODOS los documentos salen con el NIT demo 901143311.
facturatech.set-pruebas.habilitado=false

# ── POS: dejar apagado hasta que Facturatech entregue el WS del tipo 20 ──
facturacion.tpos.habilitado=false

# Sin cambios (defaults razonables):
# facturatech.url.produccion=https://ws.facturatech.co/v2/pro/index.php
# facturatech.intentos-estado=10 / facturatech.espera-entre-intentos-ms=2000
# facturatech.descargar-pdf=true
```

**Regla de seguridad del sistema:** si faltan credenciales, el sistema NO falla — emite
en modo `SIMULADA` con CUFE `SIMULADO-...` (reconocible, no se imprime en tirilla).
Sirve para tenants que aún no facturan electrónicamente.

---

## 3. Datos maestros que deben estar completos (POR TENANT)

El generador valida mucho, pero estos datos deben existir o los documentos se rechazan:

### 3.1 Empresa (pantalla Empresa)
- NIT + dígito de verificación (el DV se calcula solo si falta, pero regístrenlo).
- Razón social, dirección, **municipio y departamento** (de los catálogos — los códigos DANE salen de ahí), código postal.
- **Teléfono y correo** — el correo del emisor (CDE_4) es OBLIGATORIO para Facturatech; el teléfono va con relleno `0000000` si falta (con warning en log).
- Responsable de IVA sí/no, responsabilidad fiscal, tipo de contribuyente.

### 3.2 Terceros (clientes)
- Tipo de identificación con **código DIAN** correcto (13=CC, 31=NIT, etc.).
- **Persona natural**: nombres y apellidos SEPARADOS (nombre_1/apellido_1) — el WS los exige (regla no documentada, descubierta en certificación: sin ellos rechaza con "ADQ_6 no informado").
- **Persona jurídica (NIT)**: razón social + DV (si falta el DV, se calcula).
- Correo del cliente (CDA_4 obligatorio; si falta, va el de la empresa como fallback).
- **Consumidor Final** sirve para vender: debe existir el tercero 222222222222 con nombre_1=Consumidor, apellido_1=Final, tipo persona Natural.
- Cliente a crédito: cupo y **plazo** (días) — el plazo calcula la fecha de pago (MEP_3).

### 3.3 Comprobantes (Contabilidad → Comprobantes) — el corazón de la numeración

**Cómo elige el sistema el comprobante (importante para configurarlos bien):**
- **FC** (venta contado) y **VC** (venta crédito) se asignan **POR CAJERO**: cada cajero
  debe estar en la lista de cajeros de AMBOS comprobantes. Un cajero sin el VC asignado
  no puede vender a crédito (error "comprobante no configurado").
- **NC, ND, DS y NADS** se asignan **sin cajero**: el sistema toma el PRIMER comprobante
  ACTIVO (no legacy) de ese tipo, ordenado por id. Por eso debe existir **UNO solo activo
  por tipo** — si hay dos NC activos, siempre gana el de menor id y el otro solo confunde.

**Reglas de PREFIJOS (donde más se equivoca la gente):**

| Regla | Por qué |
|-------|---------|
| **FC y VC: MISMO prefijo y MISMA resolución** | Ante la DIAN contado y crédito son el mismo documento (factura 01) con UNA sola serie de folios. El sistema numera por prefijo (lock + uniques en BD) asumiendo esto. Prefijos distintos = dos series donde la DIAN autorizó una. |
| NC y ND: prefijos PROPIOS, distintos entre sí y del de factura | Cada tipo de nota lleva su serie. |
| El prefijo NO debe terminar en número | Facturatech no procesa esos prefijos. |
| El prefijo debe estar registrado EXACTO en la plataforma Facturatech | El nodo DRF se valida contra lo registrado; si difiere → rechazo 409. |
| No cambiar un prefijo en caliente | La serie se calcula por prefijo: cambiarlo reinicia la numeración al inicio del rango. Cambio de prefijo = solo con cambio de resolución, y con el equipo avisado. |

**Qué ajustar en cada comprobante al pasar a producción** (hoy tienen los datos del SET C1):

| Campo | Hoy (SET de pruebas C1) | En producción |
|-------|------------------------|---------------|
| Prefijo FC/VC | TCFA (ambos) | El de la resolución real (mismo en los dos) |
| Prefijo NC / ND | TCNC / TCND | Los acordados y registrados con Facturatech |
| Nº de resolución | 201911110152 | El de la resolución DIAN real (FC/VC); para NC/ND el dato que indique Facturatech |
| Clave técnica | (la demo) | Solo FC/VC la llevan — viene en la resolución de la DIAN |
| Vigencia (inicio/fin) | 2019-11-11 → 2030-12-31 | Fechas reales de la resolución |
| Rango (desde/hasta) | 28301 → 28400 | Rango real autorizado |
| Siguiente consecutivo | (avanzado por las pruebas) | **= consecutivo_desde** del rango real |
| Activo / Legacy | — | activo=sí, legacy=no; UN solo activo por tipo en NC/ND/DS/NADS |
| Cajeros (solo FC/VC) | cajero de pruebas | TODOS los cajeros que van a vender, en ambos |

**Protecciones que ya trae el sistema** (no hay que operarlas, solo saber que existen):
- Si la resolución está **incompleta** (faltan fechas o rango), el sistema se NIEGA a
  emitir con mensaje claro y **sin consumir folio** — es intencional, no es un bug.
- Si el rango se **agota**, corta con "numeración agotada, configure nueva resolución".
- Avisa por log cuando faltan ≤30 días de vigencia o ≤500 folios.
- **Nunca ajustar `siguiente_consecutivo` a mano en producción**: la numeración real de
  facturas sale del MAX por prefijo con lock; tocarlo solo crea huecos o choques.

### 3.4 Métodos de pago
Siglas reconocidas y su código DIAN (Tabla 5): `EF`→10 Efectivo, `TC`→48 T. crédito,
`TD`→49 T. débito, `TR`→47 Transferencia. Cualquier otra sigla viaja como `ZZZ` (Otro) —
válido, pero si usan uno específico (consignación, cheque...) avisar para mapearlo.

---

## 4. Base de datos — migraciones a ejecutar POR TENANT en el servidor

Carpeta `db_migrations/` (ya aplicadas en dev: cavsystems, _tenant_template, db_metrolinksas):

```bash
mysql -u <user> -p <tenant> < db_migrations/2026_07_30_facturas_unique_folio.sql
mysql -u <user> -p <tenant> < db_migrations/2026_07_30_empresa_direccion_varchar.sql
```

1. **`facturas_unique_folio`**: uniques `(comprobante_id,consecutivo)`, `(venta_id)`,
   `(prefijo,consecutivo)` — el respaldo en BD contra folios duplicados y doble factura
   por venta. Antes de aplicar, verificar duplicados con los SELECT comentados en el script.
2. **`empresa_direccion_varchar`**: la columna `empresa.direccion` estaba como DOUBLE;
   sin este fix no se puede guardar la dirección de la empresa.

La plantilla de tenant nuevo (`_tenant_template_seed/00_estructura_completa.sql`) ya
incluye los uniques — los tenants creados de ahora en adelante no necesitan la migración 1.

---

## 5. Cómo funciona en operación (para soporte)

**Flujo normal:** al completar la venta en el POS, el backend genera la factura solo
(evento interno), la envía a Facturatech y espera la firma. La tirilla sale con CUFE,
QR y resolución. **El POS espera ese round-trip (~5–20 s) por diseño** — así la tirilla
sale completa. Si en producción la espera molesta, se puede volver asíncrono (cambio de
front, no afecta la certificación).

**Estados de la factura:** `AUTORIZADA` (con CUFE) · `ENVIADA` (Facturatech aún
firmando — consultar estado luego) · `RECHAZADA` (con el motivo en mensaje) ·
`SIMULADA` (sin credenciales) · `PENDIENTE` (no se ha enviado).

**Herramientas de operación (endpoints ya existentes):**

| Situación | Qué hacer |
|-----------|-----------|
| Venta completada sin factura (error puntual, reinicio del server) | `GET /api/facturacion-electronica/ventas-sin-factura?dias=30` lista los huecos → regenerar cada una con `POST /api/facturacion-electronica/generar` `{ventaId}` |
| Factura RECHAZADA (dato del cliente malo, etc.) | Corregir el dato → `POST /api/facturacion-electronica/{facturaId}/reenviar` — **reutiliza el MISMO folio** (los rechazos no queman folio) |
| Factura ENVIADA (quedó "en proceso") | `GET /api/facturacion-electronica/{facturaId}/estado-dian` — consulta a Facturatech y actualiza. El reenvío también pre-consulta solo (nunca pisa una autorizada). No hay job automático todavía: revisar las ENVIADAS del día. |
| NC fallida o pendiente | `POST /api/notas-electronicas/devolucion/{id}/reenviar` — es idempotente: no duplica una NC autorizada ni una que quedó en proceso |
| **Anular una venta facturada** | **NO se puede anular directo** (bloqueado a propósito: la factura quedaría viva ante la DIAN). El camino es registrar una **DEVOLUCIÓN TOTAL**, que emite la Nota Crédito que la anula fiscalmente. |
| Descargar XML firmado / PDF | `GET /api/facturacion-electronica/{id}/xml` y `/{id}/pdf` |

**Reglas que ya cuida el sistema (no requieren operación):** numeración por prefijo con
lock y uniques; una factura por venta (garantizado hasta con doble clic); folio nunca
consumido en validaciones locales fallidas; mensajes de error truncados a 500; la NC
calca forma/medios de pago de la factura original; la anulación total (concepto 2)
replica la factura completa; ND exige factura AUTORIZADA; fechas de emisión de notas
siempre = día de emisión.

---

## 6. Checklist de go-live (imprimir y tachar)

**Trámites**
- [ ] Resolución DIAN real (prefijo/rango/vigencia/clave técnica) obtenida
- [ ] Resolución y prefijos registrados en la plataforma Facturatech
- [ ] Credenciales WS de producción recibidas de soportews
- [ ] Habilitación DIAN completada con el NIT real (sets de habilitación aprobados)

**Configuración**
- [ ] `facturatech.ambiente=1`, usuario/password de producción
- [ ] `facturatech.password-sha256` VACÍO (la clave real va en `facturatech.password`)
- [ ] `facturatech.set-pruebas.habilitado=false`  ← **el olvido más peligroso**
- [ ] `facturacion.tpos.habilitado=false`

**Datos por tenant**
- [ ] Empresa completa (NIT+DV, dirección, municipio/depto, postal, teléfono, CORREO)
- [ ] Comprobantes FC y VC con el MISMO prefijo, la resolución real completa (nº + clave técnica + vigencia + rango) y `siguiente_consecutivo` = inicio del rango
- [ ] TODOS los cajeros que venden asignados a FC **y** a VC
- [ ] Comprobantes NC y ND con prefijos propios registrados en Facturatech, UNO solo activo por tipo
- [ ] Tercero Consumidor Final correcto (nombres/apellidos separados)
- [ ] Métodos de pago con siglas EF/TC/TD/TR

**Base de datos**
- [ ] `2026_07_30_facturas_unique_folio.sql` aplicada en cada tenant
- [ ] `2026_07_30_empresa_direccion_varchar.sql` aplicada en cada tenant

**Prueba de humo (primer día)**
- [ ] 1 venta contado a Consumidor Final → tirilla con CUFE y QR que RESUELVE en la DIAN
- [ ] 1 venta a crédito a cliente con plazo → factura AUTORIZADA con vencimiento correcto
- [ ] 1 devolución pequeña → NC AUTORIZADA referenciando el CUFE
- [ ] Verificar el documento en catalogo-vpfe.dian.gov.co (CUFE) y en el portal Facturatech

**Pendientes conocidos (no bloquean el go-live)**
- [ ] Tiquete POS (tipo 20): esperar WS de Facturatech
- [ ] Documento Soporte: certificar con set propio antes de usar
- [ ] Job automático de re-consulta de facturas ENVIADA (hoy es manual)
- [ ] Envío del PDF/XML por correo al cliente (si se requiere, definir con Facturatech
  quién lo envía — su plataforma puede hacerlo)

---

*Documento generado a partir de la certificación C1 (2026-07-30/31) y las auditorías de
código del módulo `facturacionmodule`. Detalle técnico de las reglas del WS descubiertas
en certificación: ver memoria del proyecto y comentarios en `FacturatechXmlGenerator.java`.*
