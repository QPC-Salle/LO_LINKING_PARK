# GUÍA: IMPLEMENTACIÓN DE MARIADB

## Arquitectura Propuesta

```
Android App (Java)
    ↓ HTTP/HTTPS
API REST (PHP)
    ↓ MySQL/MariaDB
Base de Datos (MariaDB)
```

## Paso 1: Configurar Servidor API

### 1.1 Estructura de Archivos PHP

Ya tienes en `parking_api/`:
- `db.php` - Conexión a base de datos
- `login.php` - Login de usuarios
- `register.php` - Registro de usuarios
- `vehicles.php` - CRUD de vehículos
- `sessions.php` - CRUD de sesiones
- `schema.php` - Esquema de base de datos

### 1.2 Configurar `db.php`

```php
<?php
// db.php - Configuración de base de datos
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE');
header('Access-Control-Allow-Headers: Content-Type');

define('DB_HOST', 'localhost');
define('DB_USER', 'tu_usuario');
define('DB_PASS', 'tu_contraseña');
define('DB_NAME', 'lo_linking_park');

function getConnection() {
    try {
        $conn = new PDO(
            "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME,
            DB_USER,
            DB_PASS
        );
        $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        return $conn;
    } catch(PDOException $e) {
        http_response_code(500);
        echo json_encode(['error' => 'Database connection failed']);
        exit();
    }
}
?>
```

### 1.3 Crear Schema en MariaDB

Ejecuta el SQL del archivo `schema.php` que ya tienes en el proyecto.

## Paso 2: Implementar Cliente HTTP en Android

### 2.1 Crear ApiClient.java

```java
package com.example.lo_linking_park.api;

import okhttp3.*;
import android.util.Log;
import org.json.JSONObject;
import java.io.IOException;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = "http://TU_SERVIDOR/parking_api/";
    
    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build();
    
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }
    
    public static void post(String endpoint, JSONObject data, ApiCallback callback) {
        RequestBody body = RequestBody.create(
            data.toString(),
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
            .url(BASE_URL + endpoint)
            .post(body)
            .build();
            
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed", e);
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseBody);
                    if (response.isSuccessful()) {
                        callback.onSuccess(json);
                    } else {
                        callback.onError(json.optString("error", "Unknown error"));
                    }
                } catch (Exception e) {
                    callback.onError("Invalid JSON response");
                }
            }
        });
    }
    
    public static void get(String endpoint, ApiCallback callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + endpoint)
            .get()
            .build();
            
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseBody);
                    callback.onSuccess(json);
                } catch (Exception e) {
                    callback.onError("Invalid JSON response");
                }
            }
        });
    }
}
```

### 2.2 Modificar RealtimeDatabaseManager.java

Ejemplo de cómo implementar `createUser`:

```java
public void createUser(String uid, String email, String nombre, UserCallback callback) {
    try {
        JSONObject data = new JSONObject();
        data.put("uid", uid);
        data.put("email", email);
        data.put("nombre", nombre);
        
        ApiClient.post("register.php", data, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String userId = response.getString("user_id");
                    callback.onSuccess(userId);
                } catch (Exception e) {
                    callback.onError("Parse error: " + e.getMessage());
                }
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    } catch (Exception e) {
        callback.onError("JSON error: " + e.getMessage());
    }
}
```

### 2.3 Implementar Autenticación

Necesitarás gestionar sesiones de usuario. Opciones:

**Opción 1: SharedPreferences + API Token**
```java
// Guardar token
SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
prefs.edit().putString("token", token).apply();

// Leer token
String token = prefs.getString("token", null);
```

**Opción 2: SQLite Local**
```java
// Guardar usuario en SQLite local
// Sincronizar con servidor cuando haya conexión
```

## Paso 3: Endpoints de API Necesarios

### 3.1 Autenticación
- `POST /register.php` - Registrar usuario
- `POST /login.php` - Login de usuario
- `POST /logout.php` - Cerrar sesión

### 3.2 Usuarios
- `GET /users/{uid}` - Obtener datos de usuario
- `PUT /users/{uid}` - Actualizar usuario
- `DELETE /users/{uid}` - Eliminar usuario

### 3.3 Vehículos
- `GET /vehicles?uid={uid}` - Listar vehículos del usuario
- `POST /vehicles` - Crear vehículo
- `PUT /vehicles/{id}` - Actualizar vehículo
- `DELETE /vehicles/{id}` - Eliminar vehículo

### 3.4 Sesiones de Parking
- `GET /sessions?uid={uid}` - Listar sesiones del usuario
- `GET /sessions/active?uid={uid}` - Obtener sesión activa
- `POST /sessions` - Crear sesión
- `PUT /sessions/{id}` - Actualizar sesión
- `DELETE /sessions/{id}` - Eliminar sesión

### 3.5 Salles
- `GET /salles` - Listar todas las salles
- `GET /salles/{id}` - Obtener salle por ID
- `POST /salles` - Crear salle (admin)
- `PUT /salles/{id}` - Actualizar salle (admin)

## Paso 4: Permisos en AndroidManifest.xml

Asegúrate de tener:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## Paso 5: Configuración de Seguridad

### 5.1 Network Security Config

Crea `res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">TU_SERVIDOR.com</domain>
    </domain-config>
</network-security-config>
```

En AndroidManifest.xml:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

### 5.2 HTTPS Recomendado

Para producción, usa HTTPS:
- Obtén certificado SSL (Let's Encrypt gratis)
- Configura Apache/Nginx con SSL
- Cambia BASE_URL a `https://`

## Paso 6: Testing

### 6.1 Probar API con Postman

1. Crea colección "LO_LINKING_PARK"
2. Añade requests para cada endpoint
3. Verifica respuestas JSON

### 6.2 Probar desde Android

```java
// En MainActivity.onCreate()
ApiClient.get("salles", new ApiClient.ApiCallback() {
    @Override
    public void onSuccess(JSONObject response) {
        Log.d("TEST", "API funciona: " + response.toString());
    }
    
    @Override
    public void onError(String error) {
        Log.e("TEST", "API error: " + error);
    }
});
```

## Paso 7: Migración de Datos Iniciales

### 7.1 Insertar Salles

```java
// En una Activity de administración
List<Salle> salles = DataMigrationHelper.getSallesData();
for (Salle salle : salles) {
    JSONObject data = new JSONObject();
    data.put("nom", salle.getNom());
    data.put("ciutat", salle.getCiutat());
    data.put("adreca", salle.getAdreca());
    data.put("latitud", salle.getLatitud());
    data.put("longitud", salle.getLongitud());
    data.put("placesDisponibles", salle.getPlacesDisponibles());
    
    ApiClient.post("salles/create.php", data, callback);
}
```

### 7.2 Insertar Configuraciones

Similar para configuraciones usando `DataMigrationHelper.getConfigurationData()`.

## Ejemplo Completo: Login

### Android (LoginActivity.java)
```java
private void doLogin(String email, String password) {
    try {
        JSONObject data = new JSONObject();
        data.put("email", email);
        data.put("password", password);
        
        ApiClient.post("login.php", data, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    String token = response.getString("token");
                    String uid = response.getString("uid");
                    
                    // Guardar sesión
                    SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                    prefs.edit()
                        .putString("token", token)
                        .putString("uid", uid)
                        .apply();
                    
                    // Ir a MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                    
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, "Login failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    } catch (Exception e) {
        Toast.makeText(this, "JSON error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
```

### PHP (login.php)
```php
<?php
require_once 'db.php';

$data = json_decode(file_get_contents('php://input'), true);
$email = $data['email'] ?? '';
$password = $data['password'] ?? '';

if (empty($email) || empty($password)) {
    http_response_code(400);
    echo json_encode(['error' => 'Email and password required']);
    exit();
}

$conn = getConnection();
$stmt = $conn->prepare("SELECT * FROM Usuaris WHERE email = ?");
$stmt->execute([$email]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

if ($user && password_verify($password, $user['contrasenya'])) {
    // Generar token (puedes usar JWT)
    $token = bin2hex(random_bytes(32));
    
    // Guardar token en base de datos
    $stmt = $conn->prepare("UPDATE Usuaris SET token = ? WHERE id = ?");
    $stmt->execute([$token, $user['id']]);
    
    echo json_encode([
        'success' => true,
        'token' => $token,
        'uid' => $user['id'],
        'email' => $user['email'],
        'nom' => $user['nom']
    ]);
} else {
    http_response_code(401);
    echo json_encode(['error' => 'Invalid credentials']);
}
?>
```

## Recursos Adicionales

- **OkHttp Documentation:** https://square.github.io/okhttp/
- **MariaDB Documentation:** https://mariadb.com/kb/en/
- **PHP PDO Tutorial:** https://www.php.net/manual/en/book.pdo.php
- **REST API Best Practices:** https://restfulapi.net/

---

**Siguiente Paso:** Implementa `ApiClient.java` y comienza con el endpoint de login.

