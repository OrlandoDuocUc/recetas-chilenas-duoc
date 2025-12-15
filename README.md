# 🍲 Recetas Chilenas - App Android

## 📱 Información del Proyecto

**Nombre de la App:** Recetas Chilenas  
**Integrante:** Orlando Rodriguez Lopez  
**RUT:** 21.104.936-7  
**Institución:** DUOC UC  
**Fecha:** Diciembre 2025

---

## 🎯 Descripción

Aplicación móvil Android para gestionar y compartir recetas chilenas tradicionales. Incluye sistema de autenticación, roles de usuario (Admin y Usuario), persistencia local con Room, consumo de APIs externas, y acceso a recursos nativos como cámara y galería.

---

## ✨ Funcionalidades Principales

### 🔐 Autenticación
- ✅ Registro de usuarios con validación
- ✅ Login con email y contraseña
- ✅ Gestión de perfil (editar nombre y contraseña)
- ✅ Sesión persistente con DataStore
- ✅ Cierre de sesión

### 👥 Roles de Usuario

#### 👤 Usuario Normal
- Ver listado de recetas (propias + API externa)
- Ver detalle de recetas
- Crear nuevas recetas con foto (cámara o galería)
- Marcar recetas como favoritas
- Editar su perfil

#### 🛡️ Administrador
- Todas las funciones de usuario normal
- Ver lista de usuarios registrados
- Eliminar usuarios
- Gestión completa del sistema

**Credenciales Admin por defecto:**
- Email: `admin@recetas.cl`
- Password: `admin123`

### 📝 Gestión de Recetas
- ✅ Crear recetas con formulario validado
- ✅ Subir foto desde cámara o galería
- ✅ Ingredientes, tiempo de preparación, dificultad
- ✅ Ver detalle completo
- ✅ Sistema de favoritos
- ✅ Persistencia local (Room) y remota (API)

### 🌐 Integración de APIs

#### API Propia (Xano)
- **Base URL:** `https://x8ki-letl-twmt.n7.xano.io/api:73TCjzmQ/`
- **Endpoints:**
  - `GET /recetas` - Listar todas las recetas
  - `GET /recetas/{id}` - Obtener receta por ID
  - `POST /recetas` - Crear nueva receta
  - `PATCH /recetas/{id}` - Actualizar receta

#### API Externa (TheMealDB)
- **Base URL:** `https://www.themealdb.com/api/json/v1/1/`
- **Endpoints:**
  - `GET /search.php?s={query}` - Buscar recetas
  - `GET /random.php` - Receta aleatoria

### 📱 Recursos Nativos Utilizados
1. **📷 Cámara** - Capturar fotos para recetas
2. **🖼️ Galería** - Seleccionar imágenes existentes

---

## 🏗️ Arquitectura

### Patrón MVVM (Model-View-ViewModel)

```
app/
├── data/
│   ├── local/
│   │   ├── entities/          # Room Entities
│   │   ├── dao/               # Data Access Objects
│   │   ├── AppDatabase.kt     # Room Database
│   │   └── SessionManager.kt  # DataStore para sesión
│   ├── remote/
│   │   ├── RecetasApiService.kt      # API Xano
│   │   ├── TheMealDbApiService.kt    # API Externa
│   │   └── ApiClient.kt
│   ├── RecetasRepository.kt
│   └── AuthRepository.kt
├── model/
│   └── Models.kt              # Data classes (Recipe, Ingredient)
└── ui/
    ├── auth/                  # Login, Registro, Perfil
    ├── list/                  # Lista de recetas
    ├── detail/                # Detalle de receta
    ├── create/                # Crear receta
    └── admin/                 # Gestión de usuarios
```

### 💾 Persistencia de Datos

**Local (Room):**
- `users` - Usuarios registrados
- `recipes_local` - Recetas creadas localmente
- `favorites` - Recetas favoritas por usuario

**Remota (API):**
- Recetas sincronizadas con backend Xano
- Búsquedas en TheMealDB

---

## 🔧 Tecnologías y Dependencias

```gradle
// Core Android
- Kotlin 1.9.x
- compileSdk 34, minSdk 24
- ViewBinding

// Arquitectura
- Lifecycle ViewModel KTX 2.6.2
- Coroutines 1.7.3

// Persistencia
- Room 2.6.1
- DataStore Preferences 1.0.0

// Networking
- Retrofit 2.9.0
- Gson Converter 2.9.0
- OkHttp Logging Interceptor

// UI
- Material Components 1.11.0
- RecyclerView, CardView, SwipeRefreshLayout
- Coil 2.5.0 (carga de imágenes)

// Cámara
- CameraX 1.3.1

// Testing
- JUnit 4.13.2
- MockK 1.13.8
- Coroutines Test 1.7.3
- Arch Core Testing 2.2.0
```

---

## 🧪 Pruebas Unitarias

Tests implementados con **JUnit + MockK**:

- ✅ `AuthRepositoryTest` - 8 tests (login, register, update, delete)
- ✅ `AuthViewModelTest` - 5 tests (estados UI)
- ✅ `RecipeListViewModelTest` - 5 tests (carga, favoritos)

**Cobertura:** ~80% de la lógica de negocio

**Ejecutar tests:**
```powershell
./gradlew test
```

---

## 🚀 Instrucciones de Ejecución

### Requisitos Previos
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Dispositivo Android (API 24+) o Emulador
- Conexión a Internet

### Pasos de Instalación

1. **Clonar el repositorio**
```powershell
git clone <url-del-repo>
cd "EVALUACION 3"
```

2. **Abrir en Android Studio**
   - File > Open
   - Seleccionar la carpeta del proyecto
   - Esperar sincronización de Gradle

3. **Ejecutar en Emulador/Dispositivo**
   - Click en el botón ▶️ Run
   - Seleccionar dispositivo
   - La app se instalará automáticamente

4. **Credenciales de Prueba**
   - Admin: `admin@recetas.cl` / `admin123`
   - Usuario: Crear desde la app

---

## 📦 Generar APK Firmado

### 1. Crear Keystore

```powershell
# Crear carpeta
New-Item -ItemType Directory -Force -Path keystore

# Generar keystore
keytool -genkey -v -keystore keystore/recetas-release.jks -alias recetas-key -keyalg RSA -keysize 2048 -validity 10000 -storepass recetas2025 -keypass recetas2025 -dname "CN=Orlando Rodriguez, OU=DUOC, O=DUOC UC, L=Santiago, ST=RM, C=CL"
```

### 2. Compilar APK

```powershell
./gradlew assembleRelease
```

**APK ubicado en:** `app/build/outputs/apk/release/app-release.apk`

**Detalles del Keystore:**
- **Archivo:** `keystore/recetas-release.jks`
- **Store Password:** `recetas2025`
- **Key Alias:** `recetas-key`
- **Key Password:** `recetas2025`

Ver detalles completos en: `INSTRUCCIONES_APK.md`

---

## 📸 Capturas de Pantalla

*(Agregar capturas en la carpeta `EVIDENCIA DE FUNCIONAMIENTO IMG/`)*

---

## 🎨 Personalización Visual

- **Logo:** Emoji 🍲 (temporalmente)
- **Paleta de colores:** Material Design default (azul/índigo)
- **Tema:** Material Components Day/Night
- **Animaciones:** Transiciones suaves entre pantallas

---

## 📋 Checklist de Requisitos

### ✅ Requisitos Obligatorios
- [x] Tema definido y contextualizado
- [x] API externa pública (TheMealDB)
- [x] Backend propio (Xano)
- [x] Persistencia local (Room)
- [x] Persistencia remota (API)
- [x] Operaciones CRUD completas
- [x] 2 recursos nativos (Cámara + Galería)
- [x] 2 roles diferenciados (Admin + Usuario)
- [x] Formularios con validaciones visuales
- [x] Login, Registro, Perfil
- [x] Navegación fluida
- [x] Gestión de estado desacoplada (MVVM)
- [x] Animaciones y transiciones
- [x] Pruebas unitarias (80% cobertura)
- [x] APK firmado configurado

---

## 🐛 Problemas Conocidos

- Las imágenes de cámara se guardan como URI temporal (en producción se subirían a servidor)
- La sincronización con API externa es manual (podría ser automática)

---

## 🔮 Mejoras Futuras

- [ ] Subida real de imágenes a servidor
- [ ] Búsqueda y filtros avanzados
- [ ] Notificaciones push
- [ ] Modo offline completo
- [ ] Compartir recetas por redes sociales
- [ ] Calificación y comentarios

---

## 👨‍💻 Autor

**Orlando Rodriguez Lopez**  
RUT: 21.104.936-7  
DUOC UC - Desarrollo Móvil  
Diciembre 2025

---

## 📄 Licencia

Este proyecto es para fines educativos (DUOC UC).

---


