# GUÍA RÁPIDA: Corregir MongoDB URI en Vercel

## 🎯 EL PROBLEMA EN 10 SEGUNDOS

Vercel tiene un usuario/contraseña de MongoDB **incorrecto**.

## ✅ LA SOLUCIÓN EN 3 CLICS

### PASO 1️⃣ 
Abre esta URL:
https://vercel.com/dashboard/joses-projects-7d87f6dc/ajicolor_backend/settings

### PASO 2️⃣
Busca **Environment Variables** en el menú izquierdo.
Encuentra `MONGO_URI` y haz click en el icono ❌ para **eliminarla**.

### PASO 3️⃣  
Haz click en **"New Environment Variable"**.

Copia estos valores EXACTAMENTE:

```
🔑 Key:    MONGO_URI

📝 Value:  mongodb+srv://ajicolor:ajicolor123@ajicolor.6byd9.mongodb.net/BDAjicolor?retryWrites=true&w=majority

✅ Check:  Production, Preview, Development (todos)
```

Haz click **SAVE**.

---

## 🔍 VER QUÉ CAMBIÓ

| Campo | ❌ ANTES | ✅ DESPUÉS |
|-------|--------|---------|
| Usuario | `ajicolor_db_use` | `ajicolor` |
| Contraseña | `Bbc35203520` | `ajicolor123` |
| Cluster | `cluster` | `ajicolor.6byd9` |
| Base de datos | `BDAjicolor` | `BDAjicolor` |

---

## ⏱️ CUÁNTO TARDA

- Cambio en Vercel: 10 segundos
- Redeploy automático: 30-60 segundos
- Propagación: 1-2 minutos

---

## ✔️ CÓMO VERIFICAR QUE FUNCIONÓ

Abre en el navegador:
```
https://ajicolorbackend.vercel.app/health
```

Debe aparecer:
```json
{"status":"OK","timestamp":"2025-12-06T06:15:00.000Z"}
```

Si ves esto ✅ **LISTO. Vercel está correcto.**

---

## 🚀 PRÓXIMO PASO

Prueba crear un producto:

```
POST https://ajicolorbackend.vercel.app/api/v1/productos

Body:
{
  "nombre":"Polera Test",
  "precio":19990,
  "categoria":"Poleras",
  "stock":50
}
```

Si retorna `201` con producto JSON ✅ **TODO FUNCIONA**

Si retorna `503` ❌ **Espera 2-3 minutos y reintenta**

---

## 📍 UBICACIÓN EXACTA EN VERCEL

1. Dashboard: https://vercel.com/dashboard
2. Proyecto: `ajicolor_backend`
3. Tab: `Settings`
4. Sección: `Environment Variables`
5. Variable: `MONGO_URI`
