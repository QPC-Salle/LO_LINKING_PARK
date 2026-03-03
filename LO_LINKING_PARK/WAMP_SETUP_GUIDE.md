# 🚀 GUÍA COMPLETA: CONECTAR ANDROID CON WAMP + MariaDB

## PASO 1: Configurar WAMP Server

### 1.1 Verificar que WAMP esté funcionando

1. **Iniciar WAMP**
   - Abre WAMP Server
   - Verifica que el icono en la bandeja del sistema esté **VERDE**
   - Si está naranja/amarillo, hay servicios detenidos

2. **Verificar servicios**
   - Click derecho en icono WAMP
   - Apache: debe estar en estado "Started"
   - MySQL: debe estar en estado "Started"

3. **Probar en navegador**
   ```
   http://localhost/
   ```
   Deberías ver la página de inicio de WAMP

### 1.2 Configurar MySQL/MariaDB

1. **Acceder a phpMyAdmin**
   ```
   http://localhost/phpmyadmin/
   ```
   - Usuario: `root`
   - Contraseña: *(vacía por defecto)*

2. **Cambiar contraseña root (RECOMENDADO)**
   - Ir a "Cuentas de usuario"
   - Editar usuario "root"
   - Cambiar contraseña (ejemplo: "root123")
   - **IMPORTANTE:** Actualiza `parking_api/db.php` con la nueva contraseña

## PASO 2: Crear Base de Datos

### 2.1 Ejecutar Schema SQL

**Opción A: Desde phpMyAdmin**

1. Abre phpMyAdmin: `http://localhost/phpmyadmin/`
2. Click en pestaña "SQL"
3. Copia el contenido de `parking_api/schema.php` (líneas SQL, no PHP)
4. Click en "Ejecutar"

**Opción B: Crear manualmente**

1. Click en "Nueva" base de datos
2. Nombre: `parking_lasalle`
3. Cotejamiento: `utf8mb4_unicode_ci`
4. Click "Crear"
5. Luego ejecuta el SQL de las tablas

### 2.2 Verificar Tablas Creadas

Deberías ver estas tablas:
- ✅ `usuaris` (con usuario admin)
- ✅ `salles` (con 5 salles de La Salle)
- ✅ `vehicles`
- ✅ `sessions`

### 2.3 Verificar Datos Iniciales

```sql
-- Ejecuta en SQL de phpMyAdmin
SELECT * FROM usuaris;
SELECT * FROM salles;
```

Deberías ver:
- 1 usuario admin (email: admin@lasalle.cat, password: admin123)
- 5 salles de La Salle Catalunya

## PASO 3: Configurar API PHP

### 3.1 Copiar archivos a carpeta WAMP

**Ubicación de carpeta www de WAMP:**
```
C:\wamp64\www\
```
(o `C:\wamp\www\` si usas versión 32 bits)

**Copiar archivos:**

1. Crea carpeta `parking_api` en `C:\wamp64\www\`
2. Copia todos los archivos de tu proyecto:
   ```
   C:\Users\danie\Documents\GitHub\LO_LINKING_PARK\LO_LINKING_PARK\parking_api\
   ```
   A:
   ```
   C:\wamp64\www\parking_api\
   ```

### 3.2 Verificar estructura

```
C:\wamp64\www\parking_api\
├── db.php
├── login.php
├── register.php
├── vehicles.php
├── sessions.php
└── schema.php
```

### 3.3 Probar API en navegador

**Test 1: Verificar db.php**
```
http://localhost/parking_api/db.php
```
No debería mostrar errores. Si ves JSON vacío o nada, está bien.

**Test 2: Verificar login.php con datos incorrectos**
```
http://localhost/parking_api/login.php
```
Debería mostrar: `{"success":false,"message":"Mètode no permès"}`

## PASO 4: Probar API con Postman/Thunder Client

### 4.1 Test Login (POST)

**URL:**
```
http://localhost/parking_api/login.php
```

**Method:** POST

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "email": "admin@lasalle.cat",
  "password": "admin123"
}
```

**Respuesta esperada:**
```json
{
  "success": true,
  "user": {
    "id": "1",
    "nom": "Admin",
    "cognoms": "La Salle",
    "email": "admin@lasalle.cat"
  }
}
```

### 4.2 Test Registro (POST)

**URL:**
```
http://localhost/parking_api/register.php
```

**Body:**
```json
{
  "nom": "Test",
  "cognoms": "Usuario",
  "email": "test@test.com",
  "telefon": "123456789",
  "password": "test123"
}
```

## PASO 5: Configurar Android para conectar con WAMP

### 5.1 Crear ApiClient.java

Crea el archivo en: `app/src/main/java/com/example/lo_linking_park/api/ApiClient.java`

```java
package com.example.lo_linking_park.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient {
    private static final String TAG = "ApiClient";
    
    // ⚠️ IMPORTANTE: Cambiar según tu configuración
    // Para emulador Android: usa 10.0.2.2 en lugar de localhost
    // Para dispositivo real: usa tu IP local (ej: 192.168.1.100)
    private static final String BASE_URL = "http://10.0.2.2/parking_api/";
    
    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build();
    
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }
    
    /**
     * POST request
     */
    public static void post(String endpoint, JSONObject data, ApiCallback callback) {
        RequestBody body = RequestBody.create(
            data.toString(),
            MediaType.get("application/json; charset=utf-8")
        );
        
        Request request = new Request.Builder()
            .url(BASE_URL + endpoint)
            .post(body)
            .build();
            
        executeRequest(request, callback);
    }
    
    /**
     * GET request
     */
    public static void get(String endpoint, ApiCallback callback) {
        Request request = new Request.Builder()
            .url(BASE_URL + endpoint)
            .get()
            .build();
            
        executeRequest(request, callback);
    }
    
    /**
     * Ejecuta request y maneja respuesta
     */
    private static void executeRequest(Request request, ApiCallback callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Request failed: " + request.url(), e);
                runOnMainThread(() -> callback.onError("Error de conexión: " + e.getMessage()));
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Response from " + request.url() + ": " + responseBody);
                
                try {
                    JSONObject json = new JSONObject(responseBody);
                    runOnMainThread(() -> {
                        if (response.isSuccessful()) {
                            callback.onSuccess(json);
                        } else {
                            String error = json.optString("message", "Error desconocido");
                            callback.onError(error);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "JSON parse error", e);
                    runOnMainThread(() -> callback.onError("Error al procesar respuesta"));
                }
            }
        });
    }
    
    /**
     * Ejecutar en hilo principal (UI thread)
     */
    private static void runOnMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }
}
```

### 5.2 Obtener tu IP Local (para dispositivo real)

**Windows PowerShell:**
```powershell
ipconfig
```

Busca "Adaptador de red inalámbrica" o "Ethernet adapter":
```
IPv4 Address. . . . . . . . . . . : 192.168.1.100
```

Usa esta IP en ApiClient.java:
```java
private static final String BASE_URL = "http://192.168.1.100/parking_api/";
```

### 5.3 Configuración según dispositivo

| Dispositivo | URL a usar |
|-------------|------------|
| Emulador Android Studio | `http://10.0.2.2/parking_api/` |
| Emulador Genymotion | `http://10.0.3.2/parking_api/` |
| Dispositivo Real (WiFi) | `http://TU_IP_LOCAL/parking_api/` |
| Dispositivo Real (USB) | Configurar port forwarding |

## PASO 6: Modificar AndroidManifest.xml

### 6.1 Agregar permisos

Abre `app/src/main/AndroidManifest.xml` y agrega:

```xml
<!-- Antes de <application> -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 6.2 Permitir tráfico HTTP (cleartext)

Dentro de `<application>`:

```xml
<application
    android:usesCleartextTraffic="true"
    ...>
```

O crea `res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

Y en AndroidManifest.xml:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

## PASO 7: Implementar Login en Android

### 7.1 Modificar tu LoginActivity

Encuentra donde haces el login y reemplaza con:

```java
private void doLogin(String email, String password) {
    // Mostrar loading
    // progressBar.setVisibility(View.VISIBLE);
    
    try {
        JSONObject data = new JSONObject();
        data.put("email", email);
        data.put("password", password);
        
        ApiClient.post("login.php", data, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        JSONObject user = response.getJSONObject("user");
                        String userId = user.getString("id");
                        String userName = user.getString("nom");
                        
                        // Guardar sesión
                        saveUserSession(userId, userName, email);
                        
                        // Ir a MainActivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        String message = response.getString("message");
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    } catch (Exception e) {
        Toast.makeText(this, "Error al crear request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}

private void saveUserSession(String userId, String userName, String email) {
    SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
    prefs.edit()
        .putString("userId", userId)
        .putString("userName", userName)
        .putString("userEmail", email)
        .putBoolean("isLoggedIn", true)
        .apply();
}
```

## PASO 8: Solución de Problemas Comunes

### ❌ Error: "Unable to resolve host"

**Causa:** WAMP no está accesible desde Android

**Soluciones:**
1. Verifica que WAMP esté en verde
2. En emulador, usa `10.0.2.2` en lugar de `localhost`
3. En dispositivo real, verifica que esté en la misma WiFi
4. Desactiva firewall de Windows temporalmente

### ❌ Error: "Connection refused"

**Causa:** Apache no está iniciado o puerto bloqueado

**Soluciones:**
1. Click en WAMP > Apache > Service > Start
2. Verifica puerto 80: `netstat -ano | findstr :80`
3. Si Skype u otro programa usa puerto 80, cámbialo en Apache

### ❌ Error: "Cleartext HTTP traffic not permitted"

**Causa:** Android bloquea HTTP sin cifrar (Android 9+)

**Solución:**
```xml
<application android:usesCleartextTraffic="true">
```

### ❌ Error de CORS en navegador

**Causa:** Headers CORS ya están en db.php

**Verificar:** Los headers ya están configurados correctamente

### ❌ Error: "Database connection failed"

**Causa:** Credenciales incorrectas en db.php

**Solución:** Verifica en `db.php`:
```php
define('DB_HOST', 'localhost');
define('DB_USER', 'root');
define('DB_PASS', '');  // Cambia si pusiste contraseña
define('DB_NAME', 'parking_lasalle');
```

### ❌ Firewall bloquea conexión

**Solución Windows:**
1. Panel de Control > Firewall
2. "Permitir una aplicación a través del Firewall"
3. Buscar "Apache HTTP Server" o "wampapache64"
4. Marcar checkboxes de Privada y Pública

## PASO 9: Testing Completo

### Test 1: Verificar WAMP
```
http://localhost/
```
✅ Debe mostrar página WAMP

### Test 2: Verificar phpMyAdmin
```
http://localhost/phpmyadmin/
```
✅ Debe mostrar login

### Test 3: Verificar API desde navegador
```
http://localhost/parking_api/db.php
```
✅ No debe mostrar errores

### Test 4: Login desde Postman
```
POST http://localhost/parking_api/login.php
Body: {"email":"admin@lasalle.cat","password":"admin123"}
```
✅ Debe retornar success: true

### Test 5: Login desde Android
- Ejecuta app en emulador
- Ingresa credenciales: admin@lasalle.cat / admin123
- ✅ Debe iniciar sesión correctamente

## PASO 10: Próximos Endpoints a Implementar

Una vez que login funcione, necesitarás crear:

1. ✅ **login.php** - Ya existe
2. ✅ **register.php** - Ya existe  
3. ⚠️ **vehicles.php** - Completar CRUD
4. ⚠️ **sessions.php** - Completar CRUD
5. ⚠️ **salles.php** - Crear para listar salles
6. ⚠️ **user_profile.php** - Datos de usuario

## Checklist Final

- [ ] WAMP icon en verde
- [ ] Base de datos `parking_lasalle` creada
- [ ] Tablas creadas (usuaris, salles, vehicles, sessions)
- [ ] Datos iniciales insertados (admin + 5 salles)
- [ ] Archivos PHP copiados a `C:\wamp64\www\parking_api\`
- [ ] Login probado en Postman (success)
- [ ] ApiClient.java creado en Android
- [ ] AndroidManifest.xml actualizado (permisos)
- [ ] Cleartext traffic permitido
- [ ] IP correcta en BASE_URL (10.0.2.2 o tu IP local)
- [ ] Login funciona desde app Android

---

**¿Necesitas ayuda con algún paso específico?**

