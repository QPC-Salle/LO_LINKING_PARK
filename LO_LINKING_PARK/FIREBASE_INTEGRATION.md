# Integración de Firebase en LO_LINKING_PARK

## Configuración Completada ✓

### 1. Dependencias de Firebase
- ✓ Firebase BOM v33.7.0
- ✓ Firebase Authentication
- ✓ Firebase Firestore
- ✓ Firebase Analytics
- ✓ Google Services Plugin v4.4.2

### 2. Modelos de Datos Creados
- ✓ `Usuario.java` - Mapeo de tabla `usuaris`
- ✓ `Vehicle.java` - Mapeo de tabla `vehicles`
- ✓ `Salle.java` - Mapeo de tabla `salles`
- ✓ `Session.java` - Mapeo de tabla `sessions_parkimetre`
- ✓ `Payment.java` - Mapeo de tabla `pagaments`
- ✓ `Config.java` - Mapeo de tabla `configuracio`

### 3. Repositorios Implementados
- ✓ `FirebaseAuthRepository.java` - Autenticación y gestión de usuarios
- ✓ `VehicleRepository.java` - CRUD de vehículos con validación de máximo 5 por usuario
- ✓ `SalleRepository.java` - Gestión de salles y plazas disponibles

### 4. Pantallas de Autenticación
- ✓ `LoginActivity.java` - Login con email y contraseña
- ✓ `RegisterActivity.java` - Registro completo de usuarios
- ✓ Layouts XML actualizados con Material Design

### 5. Utilidades
- ✓ `DataMigrationHelper.java` - Helper para migrar datos iniciales a Firestore

---

## PASOS SIGUIENTES PARA COMPLETAR LA INTEGRACIÓN

### PASO 1: Configurar Proyecto en Firebase Console

1. **Ir a Firebase Console**: https://console.firebase.google.com/
2. **Crear nuevo proyecto o seleccionar existente**
3. **Añadir aplicación Android**:
   - Nombre del paquete: `com.example.lo_linking_park`
   - Nickname de la app: LO_LINKING_PARK
   - Certificado SHA-1: (opcional por ahora, necesario para Google Sign-In)

4. **Descargar `google-services.json`**:
   - Descargar el archivo desde Firebase Console
   - **COLOCAR EN**: `C:\Users\danie\Documents\GitHub\LO_LINKING_PARK\LO_LINKING_PARK\app\google-services.json`
   - ⚠️ **MUY IMPORTANTE**: Este archivo debe estar en la carpeta `app`, no en la raíz del proyecto

### PASO 2: Activar Firebase Authentication

1. En Firebase Console, ir a **Authentication** > **Sign-in method**
2. Activar **Email/Password**
3. (Opcional) Activar **Google Sign-In** para LoginGActivity

### PASO 3: Crear Base de Datos Firestore

1. En Firebase Console, ir a **Firestore Database**
2. Crear base de datos en modo **Producción** o **Prueba**
3. Seleccionar ubicación: **europe-west1** (o más cercana)

### PASO 4: Configurar Reglas de Seguridad Firestore

Reemplazar las reglas por defecto con estas:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Función helper para verificar autenticación
    function isSignedIn() {
      return request.auth != null;
    }
    
    // Función helper para verificar si es el propietario
    function isOwner(userId) {
      return request.auth.uid == userId;
    }
    
    // Función helper para verificar si es admin
    function isAdmin() {
      return isSignedIn() && 
             get(/databases/$(database)/documents/users/$(request.auth.uid)).data.rol == 'admin';
    }
    
    // Colección de usuarios
    match /users/{userId} {
      allow read: if isSignedIn();
      allow create: if isSignedIn() && isOwner(userId);
      allow update, delete: if isOwner(userId) || isAdmin();
    }
    
    // Colección de vehículos
    match /vehicles/{vehicleId} {
      allow read: if isSignedIn();
      allow create: if isSignedIn() && isOwner(request.resource.data.usuariId);
      allow update, delete: if isSignedIn() && isOwner(resource.data.usuariId);
    }
    
    // Colección de salles (parkings)
    match /salles/{salleId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();
    }
    
    // Colección de sesiones
    match /sessions/{sessionId} {
      allow read: if isSignedIn() && 
                     (isOwner(resource.data.usuariId) || isAdmin());
      allow create: if isSignedIn() && isOwner(request.resource.data.usuariId);
      allow update: if isSignedIn() && 
                       (isOwner(resource.data.usuariId) || isAdmin());
      allow delete: if isAdmin();
    }
    
    // Colección de pagos
    match /payments/{paymentId} {
      allow read: if isSignedIn() && 
                     (isOwner(resource.data.usuariId) || isAdmin());
      allow create: if isSignedIn() && isOwner(request.resource.data.usuariId);
      allow update: if isAdmin();
      allow delete: if isAdmin();
    }
    
    // Colección de configuración
    match /configuration/{configId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();
    }
  }
}
```

### PASO 5: Poblar Datos Iniciales

En tu `MainActivity` o en una Activity de administración, añade este código para migrar los datos iniciales:

```java
// Verificar si ya existen datos
DataMigrationHelper migrationHelper = new DataMigrationHelper();
migrationHelper.checkIfDataExists(new DataMigrationHelper.CheckDataCallback() {
    @Override
    public void onResult(boolean exists) {
        if (!exists) {
            // Migrar datos iniciales
            migrationHelper.migrateAll(new DataMigrationHelper.MigrationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, 
                        "Datos iniciales cargados", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Log.e("MainActivity", "Error al cargar datos: " + error);
                }
            });
        }
    }
});
```

### PASO 6: Sincronizar Proyecto

1. Abrir el proyecto en Android Studio
2. Clic en **File** > **Sync Project with Gradle Files**
3. Esperar a que se descarguen todas las dependencias
4. Si hay errores, hacer **Build** > **Clean Project** y luego **Build** > **Rebuild Project**

### PASO 7: Probar la Aplicación

1. **Registro de Usuario**:
   - Ejecutar la app
   - Ir a Registro
   - Completar todos los campos
   - Verificar en Firebase Console > Authentication que se creó el usuario
   - Verificar en Firestore > users que se guardaron los datos

2. **Login**:
   - Usar las credenciales del usuario registrado
   - Verificar que el login funciona correctamente

---

## Estructura de Colecciones en Firestore

```
firestore/
├── users/{userId}
│   ├── id: string
│   ├── nom: string
│   ├── cognoms: string
│   ├── email: string
│   ├── telefon: string
│   ├── rol: string ("admin" | "usuari")
│   ├── actiu: boolean
│   ├── creatEl: timestamp
│   └── actualitzatEl: timestamp
│
├── vehicles/{vehicleId}
│   ├── id: string
│   ├── usuariId: string (referencia a users)
│   ├── matricula: string
│   ├── marca: string
│   ├── model: string
│   ├── color: string
│   ├── anyFabricacio: number
│   ├── predeterminat: boolean
│   ├── actiu: boolean
│   ├── creatEl: timestamp
│   └── actualitzatEl: timestamp
│
├── salles/{salleId}
│   ├── id: string
│   ├── nom: string
│   ├── ciutat: string
│   ├── adreca: string
│   ├── latitud: number
│   ├── longitud: number
│   ├── placesTotals: number
│   ├── placesDisponibles: number
│   ├── actiu: boolean
│   ├── creatEl: timestamp
│   └── actualitzatEl: timestamp
│
├── sessions/{sessionId}
│   ├── id: string
│   ├── usuariId: string
│   ├── vehicleId: string
│   ├── salleId: string
│   ├── dataInici: timestamp
│   ├── dataFi: timestamp
│   ├── tempsMaximMinuts: number
│   ├── avisoTempsMinuts: number
│   ├── tipusFinalitzacio: string
│   ├── latitudInici: number
│   ├── longitudInici: number
│   ├── estat: string ("actiu" | "finalitzat" | "cancel·lat")
│   ├── avisoEnviat: boolean
│   ├── avisoFinalEnviat: boolean
│   ├── creatEl: timestamp
│   └── actualitzatEl: timestamp
│
├── payments/{paymentId}
│   ├── id: string
│   ├── sessionId: string
│   ├── usuariId: string
│   ├── dadesBancariesId: string
│   ├── tempsTotalMinuts: number
│   ├── tarifaPerHora: number
│   ├── importTotal: number
│   ├── estatPagament: string
│   ├── dataPagament: timestamp
│   ├── referenciaTransaccio: string
│   ├── notes: string
│   ├── creatEl: timestamp
│   └── actualitzatEl: timestamp
│
└── configuration/{configKey}
    ├── id: string
    ├── clau: string
    ├── valor: string
    ├── descripcio: string
    ├── tipusDada: string
    ├── modificablePer: string
    └── actualitzatEl: timestamp
```

---

## Uso de los Repositorios

### FirebaseAuthRepository

```java
FirebaseAuthRepository authRepo = FirebaseAuthRepository.getInstance();

// Registro
Usuario usuario = new Usuario("Juan", "Pérez", "juan@email.com", "612345678");
authRepo.registerUser("juan@email.com", "password123", usuario, new FirebaseAuthRepository.AuthCallback() {
    @Override
    public void onSuccess(String userId) {
        // Usuario registrado
    }
    
    @Override
    public void onError(String error) {
        // Manejo de error
    }
});

// Login
authRepo.loginUser("juan@email.com", "password123", callback);

// Obtener usuario actual
String userId = authRepo.getCurrentUserId();

// Cerrar sesión
authRepo.logout();
```

### VehicleRepository

```java
VehicleRepository vehicleRepo = VehicleRepository.getInstance();

// Añadir vehículo
Vehicle vehicle = new Vehicle(userId, "1234ABC", "Toyota", "Corolla", "Blanco", 2020);
vehicleRepo.addVehicle(vehicle, callback);

// Obtener vehículos del usuario
vehicleRepo.getUserVehicles(userId, new VehicleRepository.VehicleListCallback() {
    @Override
    public void onSuccess(List<Vehicle> vehicles) {
        // Lista de vehículos
    }
    
    @Override
    public void onError(String error) {
        // Manejo de error
    }
});

// Establecer vehículo predeterminado
vehicleRepo.setDefaultVehicle(userId, vehicleId, callback);

// Desactivar vehículo
vehicleRepo.deactivateVehicle(vehicleId, callback);
```

### SalleRepository

```java
SalleRepository salleRepo = SalleRepository.getInstance();

// Obtener todas las salles activas
salleRepo.getAllActiveSalles(new SalleRepository.SalleListCallback() {
    @Override
    public void onSuccess(List<Salle> salles) {
        // Lista de parkings
    }
    
    @Override
    public void onError(String error) {
        // Manejo de error
    }
});

// Obtener salle por ID
salleRepo.getSalleById(salleId, callback);

// Verificar plazas disponibles
salleRepo.checkPlacesDisponibles(salleId, new SalleRepository.CheckPlacesCallback() {
    @Override
    public void onResult(boolean hasPlaces) {
        if (hasPlaces) {
            // Hay plazas disponibles
        }
    }
});
```

---

## Próximos Pasos Recomendados

1. **Implementar SessionRepository** - Gestión de sesiones de parquímetro (ya está el código base)
2. **Crear MenuActivity** - Pantalla principal después del login
3. **Implementar VehicleActivity** - CRUD de vehículos para el usuario
4. **Integrar MapActivity con SalleRepository** - Mostrar parkings en el mapa
5. **Implementar sistema de notificaciones** - Avisos de tiempo
6. **Configurar Google Sign-In** - Para LoginGActivity
7. **Implementar PaymentRepository** - Sistema de pagos

---

## Solución de Problemas Comunes

### Error: "Default FirebaseApp is not initialized"
- Verificar que `google-services.json` esté en `app/` y no en la raíz
- Sincronizar proyecto con Gradle

### Error: "FirebaseAuth has not been initialized"
- Verificar que el plugin `com.google.gms.google-services` esté aplicado
- Limpiar y reconstruir el proyecto

### Error al registrar usuario
- Verificar que Firebase Authentication esté activado en Firebase Console
- Verificar que el método Email/Password esté habilitado
- Revisar las reglas de Firestore

### No se guardan datos en Firestore
- Verificar reglas de seguridad en Firebase Console
- Verificar que el usuario esté autenticado
- Revisar logs en Android Studio

---

## Contacto y Soporte

Para cualquier duda sobre la implementación de Firebase, consultar:
- Documentación oficial: https://firebase.google.com/docs
- Firebase Authentication: https://firebase.google.com/docs/auth
- Cloud Firestore: https://firebase.google.com/docs/firestore

---

**Última actualización**: 12 de febrero de 2026
**Versión**: 1.0
