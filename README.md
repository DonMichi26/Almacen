# Sistema de Gestión de Almacén

Un sistema de gestión de inventario y facturación desarrollado en Java con interfaz gráfica Swing, que permite administrar productos, crear recibos de venta y servicios, y generar facturas en formato PDF.

## Características Principales

- **Gestión de Productos**: Agregar, editar, eliminar y buscar productos en el inventario
- **Categorías de Productos**: Organización de productos en categorías personalizadas
- **Control de Stock**: Seguimiento automático del stock disponible
- **Facturación**: Creación de recibos de venta y servicios
- **Generación de PDF**: Exportación de recibos en formato PDF
- **Importación/Exportación CSV**: Soporte para importar y exportar productos en formato CSV
- **Interfaz Moderna**: Diseño limpio con colores y estilos profesionales
- **Modo Oscuro/Claro**: Soporte para cambiar entre temas claros y oscuros
- **Base de Datos SQLite**: Almacenamiento local seguro y eficiente

## Tecnologías Utilizadas

- **Java 11+**: Lenguaje de programación principal
- **Swing**: Framework para la interfaz gráfica
- **FlatLaf**: Biblioteca para temas modernos de interfaz
- **SQLite**: Base de datos embebida para almacenamiento
- **Maven**: Gestión de dependencias y construcción del proyecto
- **iText7**: Generación de documentos PDF

## Estructura del Proyecto

```
src/
├── main/
│   └── java/
│       └── com/mycompany/almacen/
│           ├── Almacen.java              # Clase principal de la aplicación
│           ├── dao/                      # Acceso a datos
│           │   ├── ProductDAO.java
│           │   ├── CategoryDAO.java
│           │   ├── InvoiceDAO.java
│           │   └── InvoiceItemDAO.java
│           ├── database/
│           │   └── DatabaseManager.java  # Configuración de la base de datos
│           ├── gui/                      # Interfaces gráficas
│           │   ├── ProductManagementGUI.java
│           │   ├── InvoiceManagementGUI.java
│           │   ├── ModernButton.java     # Botones con estilo moderno
│           │   └── SidebarPanel.java     # Panel de navegación lateral
│           ├── model/                    # Modelos de datos
│           │   ├── Product.java
│           │   ├── Category.java
│           │   ├── Invoice.java
│           │   └── InvoiceItem.java
│           └── util/
│               ├── PdfGenerator.java     # Generador de PDFs
│               ├── CsvManager.java       # Gestor de importación/exportación CSV
│               └── ThemeManager.java     # Gestor de temas de interfaz
```

## Funcionalidades

### Gestión de Productos
- **Agregar Productos**: Nombre, descripción, precio, stock y categoría
- **Editar Productos**: Actualizar información de productos existentes
- **Eliminar Productos**: Remover productos del inventario
- **Buscar Productos**: Filtrar productos por nombre
- **Categorías**: Organización de productos en categorías personalizadas
- **Importación/Exportación CSV**: Importar y exportar productos en formato CSV
- **Visualización**: Tabla con información detallada de productos

### Facturación
- **Ventas de Productos**: Crear recibos de venta con selección de productos y cantidades
- **Servicios**: Crear recibos para servicios de mantenimiento u otros servicios
- **Seguimiento de Stock**: Actualización automática del inventario al crear ventas
- **Historial de Recibos**: Visualización de todos los recibos creados

### Generación de PDF
- **Recibos de Venta**: Documentos PDF con detalles de productos vendidos
- **Recibos de Servicio**: Documentos PDF para servicios prestados
- **Formato Profesional**: Diseño limpio y profesional para impresión

### Interfaz de Usuario
- **Modo Oscuro/Claro**: Cambio dinámico entre temas claros y oscuros
- **Navegación Lateral**: Panel de navegación para acceso rápido a diferentes secciones
- **Botones Modernos**: Estilo consistente con bordes y efectos hover
- **Diseño Responsivo**: Interfaz que se adapta a diferentes tamaños de ventana

## Instalación y Ejecución

### Requisitos
- Java 11 o superior
- Maven 3.6.0 o superior

### Pasos para Ejecutar

1. **Clonar el Repositorio**
   ```bash
   git clone https://github.com/tu-usuario/almacen.git
   cd almacen
   ```

2. **Compilar el Proyecto**
   ```bash
   mvn clean compile
   ```

3. **Empaquetar el Proyecto**
   ```bash
   mvn package
   ```

4. **Ejecutar la Aplicación**
   ```bash
   mvn exec:java -Dexec.mainClass="com.mycompany.almacen.Almacen"
   ```

   O alternativamente:
   ```bash
   java -jar target/almacen-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Base de Datos

El sistema utiliza SQLite como base de datos embebida, lo que significa que no requiere instalación adicional de servidor de base de datos. La base de datos se crea automáticamente al iniciar la aplicación.

### Tablas
- **categories**: Almacena las categorías de productos
- **products**: Almacena la información de los productos (con referencia a categorías)
- **invoices**: Registra los recibos de venta y servicios
- **invoice_items**: Detalles de los productos en cada recibo de venta

Los datos de ejemplo se cargan automáticamente al iniciar la aplicación por primera vez.

## Importación/Exportación CSV

La aplicación permite importar y exportar productos en formato CSV con las siguientes columnas:
- id: Identificador único del producto
- name: Nombre del producto
- description: Descripción del producto
- price: Precio del producto
- stock: Cantidad en stock
- category_id: ID de la categoría a la que pertenece
- category_name: Nombre de la categoría (opcional, se creará si no existe)

### Importar productos
1. Prepara un archivo CSV con los productos que deseas importar
2. En la interfaz de gestión de productos, haz clic en "Importar CSV"
3. Selecciona el archivo CSV
4. Los productos se agregarán a la base de datos

### Exportar productos
1. En la interfaz de gestión de productos, haz clic en "Exportar CSV"
2. Selecciona la ubicación donde deseas guardar el archivo
3. Todos los productos se exportarán en formato CSV

## Temas de Interfaz

La aplicación incluye soporte para cambiar entre temas claros y oscuros:

- **Tema Claro**: Diseño con colores claros para uso en ambientes bien iluminados
- **Tema Oscuro**: Diseño con colores oscuros para reducir la fatiga visual en ambientes con poca luz
- **Cambio Dinámico**: El tema puede cambiarse en tiempo de ejecución sin reiniciar la aplicación

Para cambiar de tema, haz clic en el botón "Modo Oscuro/Claro" en la barra superior de la aplicación.

## Estilo de Código

- **Patrón MVC**: Separación clara entre modelo, vista y controlador
- **DAO Pattern**: Capa de acceso a datos para abstracción de la base de datos
- **Estilo Consistente**: Uso uniforme de colores y tipografía (Segoe UI)
- **Manejo de Excepciones**: Control adecuado de errores y mensajes amigables
- **Documentación**: Comentarios claros en el código fuente

## Contribuciones

Las contribuciones son bienvenidas. Para cambios importantes, por favor abre un issue primero para discutir qué te gustaría cambiar.

## Licencia

Este proyecto es de código abierto y está disponible bajo la [Licencia MIT](LICENSE).

---

Desarrollado con ❤️ por Luis Magro
