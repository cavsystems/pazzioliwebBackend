# 📊 Módulo de Reportes POS — Instrucciones para Frontend

## Base URL: `http://localhost:4000/api/reportes`

Todos los endpoints que usan fechas reciben query params opcionales:
```
?inicio=2026-04-01&fin=2026-04-30
```
Si no se envían, el backend usa **el mes actual** por defecto.

---

## 🏗️ ARQUITECTURA DE PANTALLAS

### Pantalla 1: **DASHBOARD PRINCIPAL** (pantalla de inicio de reportes)

> **Endpoint:** `GET /api/reportes/dashboard?inicio=&fin=`

Layout sugerido:

```
┌─────────────────────────────────────────────────────────────────────┐
│  [Selector de Rango de Fechas]   [Hoy] [Esta Semana] [Este Mes]    │
├──────────┬──────────┬──────────┬──────────┬──────────┬──────────────┤
│ 💰 Total │ 🧾 Cant. │ 📈 Venta │ 💵 Efect.│ 💳 Elec. │ 🛒 Ticket   │
│ Ventas   │ Ventas   │ Neta     │          │          │ Promedio     │
│ $XXX     │ XXX      │ $XXX     │ $XXX     │ $XXX     │ $XXX         │
├──────────┴──────────┴──────────┼──────────┴──────────┴──────────────┤
│ 📊 Ventas por Día (líneas)     │ 🍩 Métodos de Pago (dona)         │
│                                │                                    │
│  ___    ___                    │        ┌──────┐                    │
│ /   \__/   \___                │    ┌───┤ EF   ├───┐               │
│/               \               │    │   └──────┘   │               │
│                                │    │   TC    TR    │               │
│                                │    └───────────────┘               │
├────────────────────────────────┼────────────────────────────────────┤
│ 🏆 Top 5 Productos            │ 📦 Alertas Stock Bajo              │
│                                │                                    │
│ 1. Producto A ████████ $XXX    │ ⚠️ Producto X: 2 uds (min: 5)    │
│ 2. Producto B ██████   $XXX    │ ⚠️ Producto Y: 0 uds (min: 3)    │
│ 3. Producto C ████     $XXX    │ ⚠️ Producto Z: 1 uds (min: 10)   │
├────────────────────────────────┼────────────────────────────────────┤
│ 💼 Cartera (CxC)               │ 🏪 Compras vs Ventas              │
│                                │                                    │
│ Pendiente: $XXX (XX)           │ Ventas: $XXX                       │
│ Vencida:   $XXX (XX)           │ Compras: $XXX                      │
│ Parcial:   $XXX (XX)           │ Diferencia: $XXX                   │
└────────────────────────────────┴────────────────────────────────────┘
```

**Campos del response `DashboardResumenDTO`:**
```json
{
  "totalVentas": 5000000,
  "cantidadVentas": 150,
  "totalDevoluciones": 200000,
  "cantidadDevoluciones": 3,
  "ventaNeta": 4800000,
  "totalCosto": 3000000,
  "utilidadBruta": 1800000,
  "margenPorcentaje": 37.5,
  "totalEfectivo": 3000000,
  "totalMediosElectronicos": 1500000,
  "totalCredito": 500000,
  "valorInventario": 25000000,
  "productosConStockBajo": 12,
  "carteraPendiente": 800000,
  "cuentasPorCobrarVencidas": 3,
  "totalClientes": 250,
  "totalProductosActivos": 500,
  "ticketPromedio": 33333,
  "totalCompras": 2000000,
  "cuentasPorPagarPendientes": 1500000
}
```

**Tarjetas KPI sugeridas (parte superior):**
| Tarjeta | Campo | Color | Ícono |
|---------|-------|-------|-------|
| Total Ventas | `totalVentas` | Verde | 💰 |
| Cantidad Ventas | `cantidadVentas` | Azul | 🧾 |
| Venta Neta | `ventaNeta` | Verde oscuro | 📈 |
| Utilidad Bruta | `utilidadBruta` | Dorado | ⭐ |
| Margen % | `margenPorcentaje` | Badge en utilidad | % |
| Ticket Promedio | `ticketPromedio` | Morado | 🛒 |
| Total Efectivo | `totalEfectivo` | Verde claro | 💵 |
| Total Electrónico | `totalMediosElectronicos` | Azul | 💳 |
| Stock Bajo | `productosConStockBajo` | Rojo/Naranja | ⚠️ |
| Cartera Pendiente | `carteraPendiente` | Naranja | 📋 |
| Total Compras | `totalCompras` | Rojo | 🏪 |
| Cuentas x Pagar | `cuentasPorPagarPendientes` | Rojo | 📑 |

---

### Pantalla 2: **ANÁLISIS DE VENTAS** (sub-pantalla con tabs)

#### Tab 2.1: Ventas por Día (gráfica de líneas)
> `GET /api/reportes/ventas-por-dia?inicio=&fin=`

```json
[
  { "periodo": "2026-04-01", "total": 500000, "cantidad": 15, "costoTotal": 300000, "utilidad": 200000 },
  { "periodo": "2026-04-02", "total": 750000, "cantidad": 22, "costoTotal": 450000, "utilidad": 300000 }
]
```
- **Gráfica:** Line chart con 2 líneas → `total` (azul) y `utilidad` (verde)
- **Eje X:** `periodo` (fecha)
- **Eje Y:** Valores monetarios
- **Tooltip:** mostrar `cantidad`, `total`, `costoTotal`, `utilidad`

#### Tab 2.2: Ventas por Mes (gráfica de barras)
> `GET /api/reportes/ventas-por-mes?inicio=2026-01-01&fin=2026-12-31`

- **Gráfica:** Bar chart vertical
- **Eje X:** mes (`2026-01`, `2026-02`...)
- Formatear como "Ene", "Feb", "Mar"...

#### Tab 2.3: Ventas por Hora (horarios pico)
> `GET /api/reportes/ventas-por-hora?inicio=&fin=`

```json
[
  { "periodo": "8", "total": 150000, "cantidad": 5, ... },
  { "periodo": "12", "total": 900000, "cantidad": 30, ... }
]
```
- **Gráfica:** Bar chart — formatear periodo como "8:00", "9:00", "12:00"
- **Insight:** Destacar la hora con más ventas con color diferente

#### Tab 2.4: Ventas por Día de la Semana (radar)
> `GET /api/reportes/ventas-por-dia-semana?inicio=&fin=`

```json
[
  { "periodo": "Lunes", "total": 1200000, "cantidad": 35, ... },
  { "periodo": "Martes", "total": 800000, "cantidad": 22, ... }
]
```
- **Gráfica:** Radar chart o Bar chart horizontal
- **Insight:** ¿Qué día se vende más?

---

### Pantalla 3: **RANKING DE PRODUCTOS**

#### 3.1: Top Productos Más Vendidos (barras horizontales)
> `GET /api/reportes/top-productos?inicio=&fin=&topN=10`

```json
[
  {
    "codigoProducto": "SKU-001",
    "descripcion": "Camiseta Roja XL",
    "linea": "Ropa",
    "grupo": "Camisetas",
    "cantidadVendida": 150,
    "totalVendido": 4500000,
    "costoTotal": 2700000,
    "utilidad": 1800000,
    "imagen": "https://..."
  }
]
```
- **Gráfica:** Horizontal bar chart
- **Tabla debajo:** con imagen miniatura, descripción, cantidad, total, utilidad
- `topN` controla cuántos productos traer (selector: 5, 10, 20, 50)

#### 3.2: Rentabilidad por Producto (tabla con barras de progreso)
> `GET /api/reportes/rentabilidad-productos?inicio=&fin=&topN=20`

```json
[
  {
    "codigoProducto": "SKU-001",
    "descripcion": "Camiseta Roja XL",
    "linea": "Ropa",
    "cantidadVendida": 150,
    "ingresos": 4500000,
    "costo": 2700000,
    "utilidad": 1800000,
    "margenPorcentaje": 40.0
  }
]
```
- **Tabla** con columna de margen como barra de progreso con color:
  - 🟢 > 30% = verde
  - 🟡 15-30% = amarillo
  - 🔴 < 15% = rojo
- **Gráfica:** Treemap o barras apiladas (ingresos vs costo)

#### 3.3: Ventas por Línea/Categoría (torta)
> `GET /api/reportes/ventas-por-linea?inicio=&fin=`

```json
[
  { "categoria": "Ropa", "cantidadItems": 350, "totalVendido": 12000000, "porcentaje": 45.2 },
  { "categoria": "Accesorios", "cantidadItems": 200, "totalVendido": 5000000, "porcentaje": 18.8 }
]
```
- **Gráfica:** Doughnut/Pie chart con leyenda
- **Colores:** Asignar un color fijo por categoría

---

### Pantalla 4: **EQUIPO DE VENTAS**

#### 4.1: Ventas por Vendedor (barras horizontales + ranking)
> `GET /api/reportes/ventas-por-vendedor?inicio=&fin=`

```json
[
  { "vendedorId": 1, "vendedorNombre": "Juan Pérez", "cantidadVentas": 80, "totalVendido": 3000000, "ticketPromedio": 37500 }
]
```
- **Gráfica:** Horizontal bar chart ordenado de mayor a menor
- **Badge:** 🥇🥈🥉 para los 3 primeros
- **Columna extra:** Ticket promedio

#### 4.2: Ventas por Cajero (barras)
> `GET /api/reportes/ventas-por-cajero?inicio=&fin=`

```json
[
  { "cajeroId": 9, "cajeroNombre": "CajaAdminSede1", "cantidadVentas": 120, "totalVendido": 5000000, "totalEfectivo": 0, "totalElectronico": 0 }
]
```
- **Gráfica:** Bar chart agrupado

---

### Pantalla 5: **CLIENTES**

#### 5.1: Top Clientes (tabla + barras)
> `GET /api/reportes/top-clientes?inicio=&fin=&topN=10`

```json
[
  { "clienteId": 1, "identificacion": "900123456", "nombre": "Consumidor Final", "cantidadVentas": 50, "totalComprado": 8000000, "saldoCartera": 0 }
]
```
- **Tabla** ordenada por `totalComprado`
- **Gráfica:** Horizontal bar chart

---

### Pantalla 6: **MÉTODOS DE PAGO** (dona + tabla)
> `GET /api/reportes/ventas-por-metodo-pago?inicio=&fin=`

```json
[
  { "metodoPagoId": 1, "metodoPagoNombre": "Efectivo", "sigla": "EF", "cantidadTransacciones": 100, "totalMonto": 3000000, "porcentaje": 60.0 },
  { "metodoPagoId": 2, "metodoPagoNombre": "Tarjeta Crédito", "sigla": "TC", "cantidadTransacciones": 30, "totalMonto": 1500000, "porcentaje": 30.0 }
]
```
- **Gráfica:** Doughnut chart con total en el centro
- **Colores sugeridos:**
  - Efectivo → 🟢 Verde
  - Tarjeta → 🔵 Azul
  - Transferencia → 🟣 Morado
  - Crédito → 🟠 Naranja
- **Tabla debajo:** Método, Cant. Transacciones, Total, %

---

### Pantalla 7: **INVENTARIO Y STOCK**

#### 7.1: Productos con Stock Bajo (tabla de alertas)
> `GET /api/reportes/stock-bajo?limite=20`

```json
[
  {
    "varianteId": 123,
    "sku": "SKU-001",
    "descripcion": "Camiseta Roja XL",
    "linea": "Ropa",
    "bodega": "Bodega Principal",
    "existenciaActual": 2,
    "stockMinimo": 5,
    "stockMaximo": 50,
    "costo": 18000,
    "imagen": "https://..."
  }
]
```
- **Tabla** con semáforo de colores:
  - 🔴 `existenciaActual == 0` → Agotado
  - 🟠 `existenciaActual <= stockMinimo / 2` → Crítico
  - 🟡 `existenciaActual <= stockMinimo` → Bajo
- **Barra de progreso:** `existenciaActual / stockMaximo * 100`
- Mostrar imagen miniatura del producto

---

### Pantalla 8: **CARTERA (Cuentas por Cobrar)**
> `GET /api/reportes/cartera`

```json
[
  { "estado": "PENDIENTE", "cantidad": 15, "totalValorNeto": 5000000, "totalSaldo": 4500000 },
  { "estado": "VENCIDA", "cantidad": 3, "totalValorNeto": 800000, "totalSaldo": 800000 },
  { "estado": "PARCIAL", "cantidad": 5, "totalValorNeto": 2000000, "totalSaldo": 1200000 },
  { "estado": "PAGADA", "cantidad": 50, "totalValorNeto": 15000000, "totalSaldo": 0 }
]
```
- **Gráfica:** Horizontal bar chart o stacked bar
- **Colores:**
  - PENDIENTE → 🟡 Amarillo
  - VENCIDA → 🔴 Rojo
  - PARCIAL → 🟠 Naranja
  - PAGADA → 🟢 Verde

---

### Pantalla 9: **COMPRAS**

#### 9.1: Compras por Proveedor (barras horizontales)
> `GET /api/reportes/compras-por-proveedor?inicio=&fin=&topN=10`

```json
[
  { "proveedorId": 5, "proveedorNombre": "Proveedor ABC", "cantidadOrdenes": 12, "totalComprado": 8000000 }
]
```

#### 9.2: Compras vs Ventas por Mes (barras agrupadas comparativas)
> `GET /api/reportes/compras-vs-ventas?inicio=2026-01-01&fin=2026-12-31`

```json
[
  { "periodo": "2026-01", "totalVentas": 5000000, "totalCompras": 3000000, "diferencia": 2000000 },
  { "periodo": "2026-02", "totalVentas": 6000000, "totalCompras": 2500000, "diferencia": 3500000 }
]
```
- **Gráfica:** Grouped bar chart (barras lado a lado)
  - 🟢 Ventas | 🔴 Compras
- **Línea adicional opcional:** `diferencia` como área sombreada

---

### Pantalla 10: **MOVIMIENTOS DE CAJA** (barras apiladas)
> `GET /api/reportes/movimientos-caja?inicio=&fin=`

```json
[
  { "tipoMovimiento": "VENTA", "cantidad": 120, "totalMonto": 5000000, "totalEfectivo": 3000000, "totalElectronico": 2000000 },
  { "tipoMovimiento": "DEVOLUCION", "cantidad": 3, "totalMonto": 200000, "totalEfectivo": 200000, "totalElectronico": 0 },
  { "tipoMovimiento": "EGRESO", "cantidad": 5, "totalMonto": 150000, "totalEfectivo": 150000, "totalElectronico": 0 }
]
```
- **Gráfica:** Stacked bar chart (efectivo + electrónico)
- **Colores por tipo:**
  - VENTA → Verde
  - DEVOLUCION → Rojo
  - EGRESO → Naranja
  - INGRESO_EFECTIVO → Azul
  - ANULACION → Gris
  - CUENTA_POR_COBRAR → Morado

---

## 🎨 RECOMENDACIONES DE UI/UX

### Librería de Gráficas Recomendada
- **Chart.js** con `react-chartjs-2` (simple, ligero)
- O **Recharts** (más React-native, mejor para dashboards complejos)
- O **ApexCharts** con `react-apexcharts` (más bonito out-of-the-box)

### Selector de Fechas Global
Crear un componente `<DateRangePicker>` reutilizable que esté en el **header** de la página de reportes. Todos los endpoints usan los mismos params `inicio` y `fin`.

**Botones rápidos:**
- Hoy | Ayer | Esta Semana | Semana Pasada | Este Mes | Mes Pasado | Este Año | Personalizado

### Formato de Números
- Montos → `$1.234.567` (formato colombiano con punto como separador de miles)
- Porcentajes → `37.5%`
- Cantidades → `1.234`

### Color Palette Sugerida
```css
--color-primary:    #4F46E5;  /* Indigo — barras principales */
--color-success:    #10B981;  /* Verde — ventas, utilidad */
--color-danger:     #EF4444;  /* Rojo — devoluciones, vencido */
--color-warning:    #F59E0B;  /* Amarillo — pendiente, alertas */
--color-info:       #3B82F6;  /* Azul — electrónico */
--color-purple:     #8B5CF6;  /* Morado — crédito */
--color-gray:       #6B7280;  /* Gris — anulaciones */
--color-orange:     #F97316;  /* Naranja — egresos */
```

### Responsive
- **Desktop:** Grid de 2-3 columnas para tarjetas KPI, gráficas lado a lado
- **Tablet:** Grid de 2 columnas
- **Mobile:** Stack vertical, gráficas 100% ancho

### Loading States
Cada gráfica debe tener su propio skeleton loader. No bloquear toda la pantalla — cada panel carga independientemente.

### Navegación
Sidebar o Tabs dentro de la sección de reportes:
```
📊 Reportes
├── 🏠 Dashboard
├── 📈 Análisis de Ventas
│   ├── Por Día
│   ├── Por Mes
│   ├── Por Hora
│   └── Por Día Semana
├── 🏆 Productos
│   ├── Más Vendidos
│   ├── Rentabilidad
│   └── Por Categoría
├── 👥 Equipo
│   ├── Por Vendedor
│   └── Por Cajero
├── 🧑‍💼 Clientes
├── 💳 Métodos de Pago
├── 📦 Inventario (Stock Bajo)
├── 📋 Cartera (CxC)
├── 🏪 Compras
│   ├── Por Proveedor
│   └── Compras vs Ventas
└── 💰 Movimientos de Caja
```

---

## 📡 RESUMEN DE TODOS LOS ENDPOINTS (17 total)

| # | Método | Endpoint | Params | Gráfica |
|---|--------|----------|--------|---------|
| 1 | GET | `/api/reportes/dashboard` | `inicio, fin` | Tarjetas KPI |
| 2 | GET | `/api/reportes/ventas-por-dia` | `inicio, fin` | Líneas |
| 3 | GET | `/api/reportes/ventas-por-mes` | `inicio, fin` | Barras |
| 4 | GET | `/api/reportes/ventas-por-hora` | `inicio, fin` | Barras |
| 5 | GET | `/api/reportes/top-productos` | `inicio, fin, topN` | Barras H + Tabla |
| 6 | GET | `/api/reportes/ventas-por-vendedor` | `inicio, fin` | Barras H |
| 7 | GET | `/api/reportes/ventas-por-cajero` | `inicio, fin` | Barras |
| 8 | GET | `/api/reportes/ventas-por-metodo-pago` | `inicio, fin` | Dona |
| 9 | GET | `/api/reportes/top-clientes` | `inicio, fin, topN` | Tabla + Barras |
| 10 | GET | `/api/reportes/ventas-por-linea` | `inicio, fin` | Torta |
| 11 | GET | `/api/reportes/cartera` | — | Barras H |
| 12 | GET | `/api/reportes/compras-por-proveedor` | `inicio, fin, topN` | Barras H |
| 13 | GET | `/api/reportes/compras-vs-ventas` | `inicio, fin` | Barras agrupadas |
| 14 | GET | `/api/reportes/stock-bajo` | `limite` | Tabla alertas |
| 15 | GET | `/api/reportes/movimientos-caja` | `inicio, fin` | Barras apiladas |
| 16 | GET | `/api/reportes/rentabilidad-productos` | `inicio, fin, topN` | Tabla + barras |
| 17 | GET | `/api/reportes/ventas-por-dia-semana` | `inicio, fin` | Radar / Barras |

