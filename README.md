# Recetas Chilenas - Aplicacion Android

## Informacion del Proyecto

Nombre de la App: Recetas Chilenas  
Integrante: Orlando Rodriguez Lopez  
RUT: 21.104.936-7  
Institucion: DUOC UC  
Fecha: Diciembre 2025

## Descripcion

Aplicacion movil Android para gestionar y compartir recetas chilenas tradicionales. El sistema permite a los usuarios registrarse, iniciar sesion, crear recetas con imagenes, y consultar informacion de recetas tanto locales como de fuentes externas. Se implementaron dos roles de usuario: administrador y usuario regular, cada uno con distintos permisos de acceso.

## Funcionalidades Implementadas

Autenticacion de Usuarios:
- Registro de nuevos usuarios con validacion de datos
- Inicio de sesion mediante email y contraseña
- Gestion de perfil (edicion de nombre y contraseña)
- Persistencia de sesion mediante DataStore
- Cierre de sesion

Roles de Usuario:

Usuario Normal:
- Visualizacion del listado completo de recetas
- Acceso al detalle de cada receta
- Creacion de nuevas recetas incluyendo fotografia
- Marcado de recetas favoritas
- Edicion de informacion personal

Administrador:
- Acceso a todas las funcionalidades de usuario normal
- Visualizacion de lista completa de usuarios registrados
- Eliminacion de usuarios del sistema
- Gestion administrativa general

Credenciales de administrador predefinidas:
Email: admin@recetas.cl
Contraseña: admin123

Gestion de Recetas:
- Formulario de creacion con validaciones
- Carga de imagenes desde camara o galeria
- Campos: ingredientes, tiempo de preparacion, nivel de dificultad
- Visualizacion de detalle completo
- Sistema de marcado de favoritos
- Almacenamiento local (Room) y remoto (API)

Integracion con APIs:

API Propia (Xano)
Base URL: https://x8ki-letl-twmt.n7.xano.io/api:73TCjzmQ/
Endpoints disponibles:
- GET /recetas (listado completo)
- GET /recetas/{id} (detalle por ID)
- POST /recetas (crear nueva)
- PATCH /recetas/{id} (actualizar existente)

API Externa (TheMealDB)
Base URL: https://www.themealdb.com/api/json/v1/1/
Endpoints utilizados:
- GET /search.php?s={query} (busqueda)
- GET /random.php (receta aleatoria)

Recursos Nativos:
1. Camara para captura de fotografias
2. Galeria para seleccion de imagenes existentes

## Arquitectura

Se utilizo el patron MVVM (Model-View-ViewModel) para separar la logica de negocio de la interfaz de usuario

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

Estructura de la Base de Datos:

Local (Room):
- Tabla users: almacena usuarios registrados
- Tabla recipes_local: recetas creadas localmente
- Tabla favorites: relacion entre usuarios y sus recetas favoritas

Remota (API):
- Sincronizacion de recetas con backend Xano
- Consultas a base de datos externa TheMealDB

## Tecnologias y Dependencias

Core Android:
- Lenguaje: Kotlin 1.9.x
- SDK: compileSdk 34, minSdk 24
- ViewBinding habilitado

Arquitectura:
- Lifecycle ViewModel KTX 2.6.2
- Kotlin Coroutines 1.7.3

Persistencia de Datos:
- Room Database 2.6.1
- DataStore Preferences 1.0.0

Networking:
- Retrofit 2.9.0
- Gson Converter 2.9.0
- OkHttp Logging Interceptor

Interfaz de Usuario:
- Material Components 1.11.0
- RecyclerView, CardView, SwipeRefreshLayout
- Coil 2.5.0 para carga de imagenes

Camara:
- CameraX 1.3.1

Testing:
- JUnit 4.13.2
- MockK 1.13.8
- Coroutines Test 1.7.3
- Arch Core Testing 2.2.0

## Pruebas Unitarias

Se implementaron pruebas unitarias utilizando JUnit y MockK:

- AuthRepositoryTest: 8 pruebas (login, registro, actualizacion, eliminacion)
- AuthViewModelTest: 5 pruebas (validacion de estados de UI)
- RecipeListViewModelTest: 5 pruebas (carga de datos, favoritos)

Cobertura aproximada: 80% de la logica de negocio

Para ejecutar las pruebas:
./gradlew test

## Instrucciones de Ejecucion

Requisitos del Sistema:
- Android Studio Hedgehog (2023.1.1) o version superior
- JDK 17
- Dispositivo Android con API 24 o superior, o emulador configurado
- Conexion activa a Internet

Pasos de Instalacion:

1. Clonar el repositorio desde GitHub

2. Abrir el proyecto en Android Studio
   - Seleccionar File > Open
   - Navegar hasta la carpeta del proyecto
   - Aguardar la sincronizacion automatica de Gradle

3. Ejecutar la aplicacion
   - Presionar el boton Run (icono play verde)
   - Seleccionar el dispositivo de destino
   - Esperar la instalacion automatica

4. Credenciales de acceso
   - Administrador: admin@recetas.cl / admin123
   - Usuario regular: crear cuenta desde la aplicacion

## Generacion de APK Firmado

### 1. Crear Keystore

```powershell
# Crear carpeta
New-Item -ItemType Directory -Force -Path keystore

# Generar keystore
keytool -genkey -v -keystore keystore/recetas-release.jks -alias recetas-key -keyalg RSA -keysize 2048 -validity 10000 -storepass recetas2025 -keypass recetas2025 -dname "CN=Orlando Rodriguez, OU=DUOC, O=DUOC UC, L=Santiago, ST=RM, C=CL"
```

### 2. Compilar APK

```powershell
Para compilar el APK firmado ejecutar:
./gradlew assembleRelease

El archivo APK resultante se encuentra en: app/build/outputs/apk/release/app-release.apk

Configuracion del Keystore:
- Ubicacion: keystore/recetas-release.jks
- Contraseña del almacen: recetas2025
- Alias de la clave: recetas-key
- Contraseña de la clave: recetas2025

## Personalizacion Visual

Se implemento una interfaz basada en Material Design con los siguientes elementos:
- Paleta de colores: tonos azul e indigo
- Soporte para tema claro y oscuro (Day/Night)
- Animaciones de transicion entre pantallas
- Iconos y componentes Material

## Requisitos Cumplidos

Tema: Aplicacion de recetas chilenas con contexto cultural local
API Externa: Integracion con TheMealDB para recetas internacionales
Backend Propio: Sistema Xano para almacenamiento de recetas propias
Persistencia Local: Base de datos Room con 3 tablas relacionales
Persistencia Remota: Sincronizacion con API REST
CRUD Completo: Operaciones Create, Read, Update, Delete implementadas
Recursos Nativos: Acceso a camara y galeria del dispositivo
Roles: Sistema de autorizacion con administrador y usuario regular
Formularios: Validacion en tiempo real de campos obligatorios
Autenticacion: Sistema completo de login, registro y gestion de perfil
Navegacion: Flujo entre 7 activities diferentes
Arquitectura: Patron MVVM con separacion de responsabilidades
Animaciones: Transiciones visuales entre pantallas
Pruebas: Suite de tests unitarios con 80% de cobertura
APK: Configuracion de firma digital para distribucion

## Notas Tecnicas

Las imagenes capturadas se almacenan temporalmente como URI local. En un ambiente de produccion se implementaria carga a servidor remoto.

La sincronizacion con la API externa se realiza bajo demanda. Una implementacion mas avanzada incluiria actualizacion automatica en segundo plano.

## Autor

Orlando Rodriguez Lopez
RUT: 21.104.936-7
Institucion: DUOC UC
Asignatura: Desarrollo Movil
Fecha: Diciembre 2025

## Licencia

Proyecto desarrollado con fines academicos para DUOC UC.


