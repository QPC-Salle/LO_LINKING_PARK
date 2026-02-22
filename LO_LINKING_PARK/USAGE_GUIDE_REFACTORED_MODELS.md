# Guía de Uso - Clases Refactorizadas

## 📋 Índice
1. Usuario
2. Vehicle
3. Session
4. Ejemplos de Integración

---

## 1️⃣ Clase Usuario

### Constructor Vacío (requerido por Firebase)
```java
Usuario usuario = new Usuario();
```

### Constructor Simplificado
```java
Usuario usuario = new Usuario(
    "Joan",
    "García López",
    "joan@example.com",
    "612345678"
);
// Los campos 'id', 'creatEl' y 'actualitzatEl' se asignan automáticamente
```

### Constructor Completo
```java
long ahora = System.currentTimeMillis();
Usuario usuario = new Usuario(
    "user-123",           // id
    "Anna",              // nom
    "Martínez Pérez",    // cognoms
    "anna@example.com",  // email
    "612987654",         // telefon
    "usuari",            // rol ("admin" o "usuari")
    true,                // actiu
    ahora,               // creatEl (timestamp)
    ahora                // actualitzatEl (timestamp)
);
```

### Getters y Setters
```java
// Getters
String id = usuario.getId();
String nom = usuario.getNom();
long creatEl = usuario.getCreatEl(); // long, no Date
boolean esAdmin = usuario.isAdmin();

// Setters
usuario.setNom("Nou Nom");
usuario.setActiu(false);
usuario.setCreatEl(System.currentTimeMillis());
```

---

## 2️⃣ Clase Vehicle

### Constructor Vacío (requerido por Firebase)
```java
Vehicle vehicle = new Vehicle();
```

### Constructor Simplificado
```java
String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

Vehicle vehicle = new Vehicle(
    userId,           // usuariId
    "ABC1234",        // matricula
    "Toyota",         // marca
    "Corolla",        // model
    "Gris Metalizado",// color
    2023              // anyFabricacio
);
// Los campos 'id', 'predeterminat', 'actiu', 'creatEl' y 'actualitzatEl' se asignan automáticamente
```

### Constructor Completo
```java
long ahora = System.currentTimeMillis();

Vehicle vehicle = new Vehicle(
    "vehicle-456",           // id
    "user123",              // usuariId
    "XYZ9876",             // matricula
    "BMW",                 // marca
    "320i",                // model
    "Negro",               // color
    2024,                  // anyFabricacio
    true,                  // predeterminat
    true,                  // actiu
    ahora,                 // creatEl (timestamp)
    ahora                  // actualitzatEl (timestamp)
);
```

### Getters y Setters
```java
// Getters
String matricula = vehicle.getMatricula();
String marca = vehicle.getMarca();
boolean actiu = vehicle.isActiu();
long creatEl = vehicle.getCreatEl(); // long, no Date

// Setters
vehicle.setColor("Blanco");
vehicle.setActiu(false);
vehicle.setActualitzatEl(System.currentTimeMillis());
```

---

## 3️⃣ Clase Session

### Constructor Vacío (requerido por Firebase)
```java
Session session = new Session();
```

### Constructor Simplificado
```java
String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

Session session = new Session(
    userId,           // usuariId
    "vehicle123",     // vehicleId
    "salle456",       // salleId
    120,              // tempsMaximMinuts (máximo de minutos para aparcar)
    15,               // avisoTempsMinuts (aviso a los X minutos)
    41.3874,          // latitudInici
    2.1686            // longitudInici
);
// Los campos 'id', 'dataInici', 'estat', 'creatEl' y 'actualitzatEl' se asignan automáticamente
```

### Constructor Completo
```java
long ahora = System.currentTimeMillis();

Session session = new Session(
    "session-789",               // id
    "user123",                  // usuariId
    "vehicle456",               // vehicleId
    "salle789",                 // salleId
    ahora,                      // dataInici (timestamp)
    ahora + 3600000,            // dataFi (1 hora después)
    120,                        // tempsMaximMinuts
    15,                         // avisoTempsMinuts
    "manual",                   // tipusFinalitzacio ("manual", "temps", "admin")
    41.3874,                    // latitudInici
    2.1686,                     // longitudInici
    "finalitzat",               // estat ("actiu", "finalitzat", "cancel·lat")
    true,                       // avisoEnviat
    true,                       // avisoFinalEnviat
    ahora,                      // creatEl (timestamp)
    ahora                       // actualitzatEl (timestamp)
);
```

### Getters y Setters
```java
// Getters
long dataInici = session.getDataInici(); // long, no Date
long dataFi = session.getDataFi();       // long, no Date
String estat = session.getEstat();
boolean avisoEnviat = session.isAvisoEnviat();

// Setters
session.setEstat("finalitzat");
session.setDataFi(System.currentTimeMillis());
session.setTipusFinalitzacio("manual");

// Utility para calcular duración
long duracioMillis = session.getDataFi() - session.getDataInici();
long duracioMinuts = duracioMillis / 60000;
```

---

## 4️⃣ Ejemplos de Integración

### Ejemplo 1: Crear Usuario y Guardar en Firebase
```java
Usuario usuario = new Usuario("Joan", "García", "joan@example.com", "612345678");

RealtimeDatabaseManager dbManager = RealtimeDatabaseManager.getInstance();
dbManager.createUser(usuario, new RealtimeDatabaseManager.UserCallback() {
    @Override
    public void onSuccess(String userId) {
        Toast.makeText(context, "Usuario creado: " + userId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
});
```

### Ejemplo 2: Añadir Vehículo
```java
String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
Vehicle vehicle = new Vehicle(userId, "ABC1234", "Toyota", "Corolla", "Gris", 2023);

dbManager.addVehicle(vehicle, new RealtimeDatabaseManager.VehicleCallback() {
    @Override
    public void onSuccess(Vehicle savedVehicle) {
        Toast.makeText(context, "Vehículo guardado: " + savedVehicle.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
});
```

### Ejemplo 3: Obtener Vehículos del Usuario
```java
dbManager.getVehicles(new RealtimeDatabaseManager.VehicleListCallback() {
    @Override
    public void onSuccess(List<Vehicle> vehicles) {
        for (Vehicle v : vehicles) {
            Log.d("Vehicle", v.getMatricula() + " - " + v.getMarca());
        }
    }

    @Override
    public void onError(String error) {
        Log.e("Error", error);
    }
});
```

### Ejemplo 4: Crear Sesión de Parking
```java
String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

Session session = new Session(
    userId,
    vehicleId,
    salleId,
    120,
    15,
    latitude,
    longitude
);

dbManager.createParkingSession(session, new RealtimeDatabaseManager.SessionCallback() {
    @Override
    public void onSuccess(Session savedSession) {
        Toast.makeText(context, "Sesión iniciada: " + savedSession.getId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
});
```

### Ejemplo 5: Finalizar Sesión de Parking
```java
dbManager.finishParkingSession(sessionId, "manual", 
    new RealtimeDatabaseManager.SessionCallback() {
    @Override
    public void onSuccess(Session updatedSession) {
        long duracion = updateSession.getDataFi() - updatedSession.getDataInici();
        long duracionMinutos = duracion / 60000;
        Toast.makeText(context, "Sesión finalizada. Duración: " + duracionMinutos + " minutos", 
            Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(context, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
});
```

---

## ⚠️ Notas Importantes

1. **Timestamps en Milisegundos:**
   - `System.currentTimeMillis()` devuelve milisegundos desde epoch (01/01/1970)
   - Esto es el formato estándar para Firebase RTDB

2. **Constructor Vacío Obligatorio:**
   - Requerido para que Firebase deserialice automáticamente
   - No eliminar nunca este constructor

3. **FirebaseAuth para User ID:**
   ```java
   String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
   ```

4. **Estados de Session:**
   - `"actiu"` - Sesión en curso
   - `"finalitzat"` - Sesión finalizada
   - `"cancel·lat"` - Sesión cancelada

5. **Conversión de Timestamps (si es necesario):**
   ```java
   // De long a Date
   Date date = new Date(timestamp);
   
   // De Date a long
   long timestamp = date.getTime();
   ```

---

## ✅ Checklist de Validación

- [ ] Constructor vacío presente
- [ ] Constructor completo presente
- [ ] Constructor simplificado presente
- [ ] Todos los campos tienen getter y setter
- [ ] Timestamps usan `long` en lugar de `Date`
- [ ] Campo `id` está presente y puede ser asignado
- [ ] No hay import de `java.util.Date`
- [ ] Compatible con Firebase RTDB

---

## 📚 Recursos Útiles

- [Firebase Realtime Database Documentation](https://firebase.google.com/docs/database)
- [Timestamp Conversion Tool](https://www.epochconverter.com/)
- [Android Firebase Auth Guide](https://firebase.google.com/docs/auth/android/start)
