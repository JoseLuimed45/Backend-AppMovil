# 📦 Plan de Migración de Productos

Este documento detalla la estrategia para migrar los productos hardcodeados desde el proyecto Android legacy (`Polera_ecommerce`) hacia el nuevo Backend (MongoDB + Cloudinary).

## 1. Análisis del Origen de Datos
-   **Datos:** Los productos no están en un JSON, sino hardcodeados en el archivo Kotlin:
    `Polera_ecommerce/app/src/main/java/com/example/appajicolorgrupo4/data/CatalogoProductos.kt`
-   **Imágenes:** Las imágenes residen en la carpeta de recursos de Android:
    `Polera_ecommerce/app/src/main/res/drawable/`
-   **Formato:** Los nombres de archivo coinciden con los IDs de recurso (ej: `R.drawable.polera_beastie_boys` -> `polera_beastie_boys.png`).

## 2. Estrategia de Migración

### Paso 1: Extracción de Datos (Manual/Script)
Crearemos un archivo intermedio `products.json` en la carpeta `Backend/scripts` que replique la estructura de `CatalogoProductos.kt`.
-   **Mapeo:**
    -   `id` -> `id`
    -   `nombre` -> `nombre`
    -   `precio` -> `precio`
    -   `categoria` -> `categoria` (SERIGRAFIA, DTF, ACCESORIOS)
    -   `stock` -> `stock`
    -   **`imageFilename`**: Se derivará del nombre del recurso (ej: `polera_beastie_boys.png`).

### Paso 2: Configuración del Script de Migración
Actualizaremos `migrateWithCloudinary.js` para:
1.  Leer este nuevo `products.json`.
2.  Buscar las imágenes en la ruta absoluta de los recursos de Android:
    `../../Polera_ecommerce/app/src/main/res/drawable/`
3.  Subir cada imagen a Cloudinary (`folder: "ajicolor_products"`).
4.  Guardar el producto en MongoDB con la `imagenUrl` retornada por Cloudinary.

## 3. Estructura del JSON Intermedio
El archivo `products.json` tendrá este formato:
```json
[
  {
    "id": "prod_001",
    "nombre": "Polera Beastie Boys",
    "descripcion": "Polera diseño personalizado...",
    "precio": 15000,
    "categoria": "SERIGRAFIA",
    "stock": 50,
    "imageFilename": "polera_beastie_boys.png"
  },
  ...
]
```

## 4. Ejecución
1.  Generar `products.json`.
2.  Ejecutar `node migrateWithCloudinary.js`.
3.  Verificar en MongoDB Atlas y Cloudinary.

## 5. Validación
-   Script `verifyCloudinaryUrls.js` para confirmar que las URLs son remotas.
-   Prueba visual en la App Android (Admin) para asegurar que cargan correctamente.
