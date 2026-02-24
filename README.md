# Sistema de Gestión de Almacén

![Java](https://img.shields.io/badge/Java-11+-ED8B00?style=flat&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-FF6600?style=flat)
![SQLite](https://img.shields.io/badge/SQLite-3.45-003B57?style=flat&logo=sqlite)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-green.svg)

Sistema de gestión de inventario y facturación desarrollado en **Java** con **JavaFX**, que permite administrar productos, gestionar ventas y generar facturas en formato PDF.

## Tabla de Contenidos

1. [Características Principales](#características-principales)
2. [Tecnologías Utilizadas](#tecnologías-utilizadas)
3. [Arquitectura del Sistema](#arquitectura-del-sistema)
4. [Estructura del Proyecto](#estructura-del-proyecto)
5. [Funcionalidades](#funcionalidades)
6. [Instalación y Ejecución](#instalación-y-ejecución)
7. [Base de Datos](#base-de-datos)
8. [API de Componentes](#api-de-componentes)
9. [Contribuciones](#contribuciones)
10. [Licencia](#licencia)

---

## Características Principales

- **Gestión de Productos**: CRUD completo de productos con búsqueda inteligente
- **Categorías y Marcas**: Organización jerárquica de productos
- **Control de Stock**: Seguimiento automático del inventario
- **Ventas Rápidas**: Proceso de venta optimizado con búsqueda de productos
- **Facturación PDF**: Generación de facturas profesionales en PDF
- **Dashboard**: Métricas y estadísticas en tiempo real
- **Interfaz Moderna**: Diseño fluido con JavaFX y CSS
- **Base de Datos SQLite**: Almacenamiento local sin configuración

---

## Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 11+ | Lenguaje de programación |
| JavaFX | 21.0.4 | Framework de interfaz gráfica |
| SQLite | 3.45.1 | Base de datos embebida |
| Maven | 3.9+ | Gestión de dependencias |
| iText PDF | 8.0.2 | Generación de documentos PDF |
| FlatLaf | 3.5.2 | Temas modernos (legacy) |

---

## Arquitectura del Sistema

El proyecto sigue una **arquitectura multicapa** con separación clara de responsabilidades:

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  ┌─────────────┐ ┌──────────────┐ ┌─────────────────────┐   │
│  │  Dashboard  │ │ ProductCatalog│ │ QuickSaleModal     │   │
│  │  Controller │ │ Controller    │ │ Controller         │   │
│  └─────────────┘ └──────────────┘ └─────────────────────┘   │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              AlmacenFX (Application)                  │  │
│  └───────────────────────────────────────────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                      SERVICE LAYER                          │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────────┐     │
│  │ProductService│ │InvoiceService│ │Brand/Category    │     │
│  │              │ │              │ │   Service        │     │
│  └──────────────┘ └──────────────┘ └──────────────────┘     │
├─────────────────────────────────────────────────────────────┤
│                       DATA LAYER                            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐      │
│  │ProductDAO│ │InvoiceDAO│ │Category  │ │  BrandDAO  │      │
│  │          │ │          │ │   DAO    │ │            │      │
│  └──────────┘ └──────────┘ └──────────┘ └────────────┘      │
├─────────────────────────────────────────────────────────────┤
│                   DATABASE (SQLite)                         │
└─────────────────────────────────────────────────────────────┘
```

### Patrones de Diseño Aplicados

- **MVC (Model-View-Controller)**: Separación entre modelo, vista y controlador
- **DAO (Data Access Object)**: Abstracción del acceso a datos
- **Service Layer**: Lógica de negocio encapsulada en servicios
- **Dependency Injection**: Inyección de dependencias en controladores

---

## Estructura del Proyecto

```
src/main/
├── java/com/mycompany/almacen/
│   ├── AlmacenFX.java                    # Punto de entrada JavaFX
│   ├── controller/                       # Controladores FXML
│   │   ├── DashboardController.java      # Dashboard y métricas
│   │   ├── ProductCatalogController.java # Catálogo de productos
│   │   ├── ProductModalController.java   # Modal de producto
│   │   └── QuickSaleModalController.java # Venta rápida
│   ├── service/                          # Capa de negocio
│   │   ├── ProductService.java           # Lógica de productos
│   │   ├── InvoiceService.java           # Lógica de facturas
│   │   ├── CategoryService.java          # Gestión categorías
│   │   └── BrandService.java              # Gestión marcas
│   ├── dao/                              # Acceso a datos
│   │   ├── ProductDAO.java
│   │   ├── CategoryDAO.java
│   │   ├── BrandDAO.java
│   │   ├── InvoiceDAO.java
│   │   └── InvoiceItemDAO.java
│   ├── model/                            # Entidades
│   │   ├── Product.java
│   │   ├── Category.java
│   │   ├── Brand.java
│   │   ├── Invoice.java
│   │   └── InvoiceItem.java
│   ├── database/
│   │   └── DatabaseManager.java          # Configuración BD
│   ├── exception/                        # Excepciones personalizadas
│   │   ├── AlmacenException.java
│   │   ├── ValidationException.java
│   │   └── SecurityException.java
│   ├── gui/components/                   # Componentes reutilizables
│   │   ├── ProductCard.java
│   │   └── StockBadge.java
│   └── util/
│       └── PdfGenerator.java              # Generación PDF
└── resources/
    ├── fxml/                             # Vistas FXML
    │   ├── dashboard.fxml
    │   ├── product-catalog.fxml
    │   ├── product-modal.fxml
    │   └── quick-sale-modal.fxml
    └── styles/
        └── modern-theme.css              # Estilos CSS
```

---

## Funcionalidades

### Dashboard
- Visualización de métricas en tiempo real
- Total de productos en inventario
- Valor total del inventario
- Número de ventas realizadas
-最近的 ventas

### Gestión de Productos
- **Crear**: Agregar nuevos productos con nombre, descripción, precio, stock, categoría y marca
- **Leer**: Ver catálogo completo con búsqueda inteligente
- **Actualizar**: Editar información de productos existentes
- **Eliminar**: Remover productos del inventario
- **Buscar**: Filtrado por nombre, categoría o marca

### Ventas Rápidas
- Selección de productos del catálogo
- Ajuste de cantidades
- Cálculo automático de totales
- Generación de factura PDF
- Actualización automática del stock

### Facturación
- Registro de ventas con detalles completos
- Asociación de items a facturas
- Control de inventario en tiempo real
- Exportación a PDF profesional

---

## Instalación y Ejecución

### Requisitos Previos

- **Java Development Kit (JDK)**: Versión 11 o superior
- **Apache Maven**: Versión 3.6.0 o superior

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/tu-usuario/almacen.git
   cd almacen
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn javafx:run
   ```

4. **Generar JAR ejecutable**
   ```bash
   mvn package
   java -jar target/almacen-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

### Configuración de Variables de Entorno

Para sistemas Linux/Mac, agregue al `~/.bashrc` o `~/.zshrc`:
```bash
export JAVA_HOME=/path/to/jdk-11
export PATH=$JAVA_HOME/bin:$PATH
```

---

## Base de Datos

### Esquema de Tablas

```sql
-- Categorías de productos
CREATE TABLE categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    description TEXT
);

-- Marcas de productos
CREATE TABLE brands (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE
);

-- Productos
CREATE TABLE products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price REAL NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    category_id INTEGER,
    brand_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    FOREIGN KEY (brand_id) REFERENCES brands(id)
);

-- Facturas
CREATE TABLE invoices (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    client_name TEXT,
    client_id TEXT,
    subtotal REAL NOT NULL,
    tax REAL NOT NULL,
    total REAL NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Items de factura
CREATE TABLE invoice_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price REAL NOT NULL,
    subtotal REAL NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### Datos de Prueba

El sistema carga automáticamente datos de ejemplo al iniciar por primera vez:
- 3 categorías ejemplo
- 3 marcas ejemplo
- 10 productos de prueba

---

## API de Componentes

### ProductService

```java
public class ProductService {
    public void addProduct(Product product) throws AlmacenException
    public Product getProductById(int id) throws AlmacenException
    public List<Product> getAllProducts() throws AlmacenException
    public void updateProduct(Product product) throws AlmacenException
    public void deleteProduct(int id) throws AlmacenException
    public List<Product> searchProductsByName(String name) throws AlmacenException
}
```

### InvoiceService

```java
public class InvoiceService {
    public int addInvoice(Invoice invoice) throws AlmacenException
    public Invoice getInvoiceById(int id) throws AlmacenException
    public List<Invoice> getAllInvoices() throws AlmacenException
    public void addInvoiceItem(InvoiceItem item) throws AlmacenException
    public int processSale(Invoice invoice, List<InvoiceItem> items) throws AlmacenException
}
```

---

## Contribución

Las contribuciones son bienvenidas. Por favor:

1. Fork el repositorio
2. Cree una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit sus cambios (`git commit -am 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Cree un Pull Request

---

## Licencia

Este proyecto está bajo la Licencia MIT. Vea el archivo [LICENSE](LICENSE) para más detalles.

---

**Desarrollado con ❤️ por Luis Magro**

*Sistema de Gestión de Almacén v1.0*
