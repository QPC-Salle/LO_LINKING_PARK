# 🚀 GUÍA RÁPIDA - CONFIGURACIÓN WAMP

## Checklist Paso a Paso

### ✅ 1. Verificar WAMP (2 minutos)

```
1. Abrir WAMP Server
2. Icono en bandeja del sistema debe estar VERDE
   ❌ Naranja/Amarillo = Apache o MySQL detenido
   ✅ Verde = Todo funcionando

3. Probar en navegador:
   http://localhost/
   
   Deberías ver: "Your WAMP server is working!"
```

### ✅ 2. Crear Base de Datos (3 minutos)

```
1. Abrir phpMyAdmin:
   http://localhost/phpmyadmin/
   
   Usuario: root
   Contraseña: (vacía, solo presiona Enter)

2. Click en pestaña "SQL"

3. Copiar y pegar este SQL:
```

```sql
CREATE DATABASE IF NOT EXISTS parking_lasalle
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE parking_lasalle;

CREATE TABLE IF NOT EXISTS usuaris (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    cognoms VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    telefon VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('admin', 'usuari') NOT NULL DEFAULT 'usuari',
    actiu TINYINT(1) NOT NULL DEFAULT 1,
    creat_el DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualitzat_el DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS salles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    ciutat VARCHAR(100) NOT NULL,
    adreca VARCHAR(255),
    latitud DECIMAL(10, 8) NOT NULL,
    longitud DECIMAL(11, 8) NOT NULL,
    places_totals INT NOT NULL DEFAULT 0,
    places_disponibles INT NOT NULL DEFAULT 0,
    actiu TINYINT(1) NOT NULL DEFAULT 1,
    creat_el DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuari_id INT NOT NULL,
    matricula VARCHAR(20) NOT NULL,
    marca VARCHAR(100),
    model VARCHAR(100),
    color VARCHAR(50),
    any_fabricacio INT,
    actiu TINYINT(1) NOT NULL DEFAULT 1,
    creat_el DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuari_id) REFERENCES usuaris(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuari_id INT NOT NULL,
    vehicle_id INT NOT NULL,
    salle_id INT,
    data_inici DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_fi DATETIME,
    temps_maxim_minuts INT NOT NULL DEFAULT 60,
    aviso_temps_minuts INT NOT NULL DEFAULT 10,
    tipus_finalitzacio ENUM('manual', 'temps', 'admin') DEFAULT NULL,
    latitud_inici DECIMAL(10, 8),
    longitud_inici DECIMAL(11, 8),
    estat ENUM('actiu', 'finalitzat', 'cancel·lat') NOT NULL DEFAULT 'actiu',
    creat_el DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuari_id) REFERENCES usuaris(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

INSERT INTO salles (nom, ciutat, adreca, latitud, longitud, places_totals, places_disponibles) VALUES
('La Salle Mollerussa', 'Mollerussa', 'Carrer de la Industria, 12, 25230 Mollerussa', 41.61320000, 0.62450000, 50, 50),
('La Salle Campus Barcelona', 'Barcelona', 'Carrer de Sant Joan de la Salle, 42, 08022', 41.41560000, 2.17450000, 100, 100),
('La Salle Bonanova', 'Barcelona', 'Passeig de la Bonanova, 8, 08022', 41.40890000, 2.13640000, 75, 75),
('La Salle Tarragona', 'Tarragona', 'Carrer de la Salle, 1, 43001', 41.11890000, 1.24450000, 60, 60),
('La Salle Girona', 'Girona', 'Avinguda de Montilivi, 16, 17003', 41.97940000, 2.82140000, 80, 80);

INSERT INTO usuaris (nom, cognoms, email, telefon, password_hash, rol) VALUES
('Admin', 'La Salle', 'admin@lasalle.cat', '000000000',
 '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin');
```

```
4. Click en botón "Ejecutar"

5. Verificar en panel izquierdo:
   ✅ Base de datos "parking_lasalle" creada
   ✅ 4 tablas (usuaris, salles, vehicles, sessions)
```

### ✅ 3. Copiar Archivos PHP (2 minutos)

**Ubicación de carpeta www de WAMP:**
- 64 bits: `C:\wamp64\www\`
- 32 bits: `C:\wamp\www\`

**PowerShell (Ejecuta en terminal):**

```powershell
# Crear carpeta parking_api en WAMP
New-Item -Path "C:\wamp64\www\parking_api" -ItemType Directory -Force

# Copiar archivos
Copy-Item "C:\Users\danie\Documents\GitHub\LO_LINKING_PARK\LO_LINKING_PARK\parking_api\*" -Destination "C:\wamp64\www\parking_api\" -Recurse
```

**O copiar manualmente:**
1. Abre Explorador de Windows
2. Copia carpeta: `C:\Users\danie\Documents\GitHub\LO_LINKING_PARK\LO_LINKING_PARK\parking_api\`
3. Pégala en: `C:\wamp64\www\`

**Estructura final:**
```
C:\wamp64\www\parking_api\
├── db.php
├── login.php
├── register.php
├── vehicles.php
├── sessions.php
└── schema.php
```

### ✅ 4. Probar API (3 minutos)

**Test 1: API accesible**
```
http://localhost/parking_api/db.php
```
✅ No debe mostrar errores

**Test 2: Login con Postman/Thunder Client**

```
POST http://localhost/parking_api/login.php
Content-Type: application/json

Body:
{
  "email": "admin@lasalle.cat",
  "password": "admin123"
}

Respuesta esperada:
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

**Test 3: Login con credenciales incorrectas**
```
Body:
{
  "email": "admin@lasalle.cat",
  "password": "wrongpassword"
}

Respuesta esperada:
{
  "success": false,
  "message": "Credencials incorrectes"
}
```

### ✅ 5. Obtener tu IP Local (para dispositivo real)

**PowerShell:**
```powershell
ipconfig | findstr IPv4
```

**Resultado ejemplo:**
```
IPv4 Address. . . . . . . . . . . : 192.168.1.100
```

**Actualizar ApiClient.java:**
```java
// Línea 28 en ApiClient.java
private static final String BASE_URL = "http://192.168.1.100/parking_api/";
```

### ✅ 6. Configurar Android (HECHO)

Ya está configurado:
- ✅ `ApiClient.java` creado
- ✅ `network_security_config.xml` creado
- ✅ `AndroidManifest.xml` actualizado con permisos
- ✅ `LoginActivityExample.java` como referencia

### ✅ 7. Compilar y Probar

**Desde Android Studio:**
```
1. Build > Clean Project
2. Build > Rebuild Project
3. Run > Run 'app'
```

**Probar login:**
```
Email: admin@lasalle.cat
Password: admin123
```

---

## 🔧 Solución de Problemas

### ❌ WAMP no inicia (icono rojo/naranja)

**Problema:** Otro programa usa puerto 80 o 3306

**Solución:**
```powershell
# Verificar qué programa usa puerto 80
netstat -ano | findstr :80

# Si es Skype, IIS, u otro:
# 1. Cerrar ese programa
# 2. O cambiar puerto de Apache en WAMP:
#    - Click derecho en WAMP > Tools > Use a port other than 80
```

### ❌ "Unable to resolve host" en Android

**Problema:** Android no puede conectar con WAMP

**Solución:**
```java
// Para EMULADOR, usa:
private static final String BASE_URL = "http://10.0.2.2/parking_api/";

// Para DISPOSITIVO REAL, usa tu IP:
private static final String BASE_URL = "http://192.168.1.XXX/parking_api/";
```

### ❌ "Database connection failed"

**Problema:** Credenciales incorrectas en db.php

**Solución:**
```php
// Verificar en C:\wamp64\www\parking_api\db.php
define('DB_HOST', 'localhost');
define('DB_USER', 'root');
define('DB_PASS', '');  // Vacía por defecto
define('DB_NAME', 'parking_lasalle');
```

### ❌ Firewall bloquea conexión

**Solución:**
```
1. Panel de Control > Windows Defender Firewall
2. "Permitir una aplicación..."
3. Buscar "wampapache64" o "Apache HTTP Server"
4. Marcar checkboxes de Privada y Pública
5. Aceptar
```

### ❌ "Cleartext HTTP traffic not permitted"

**Solución:** Ya está solucionado con:
- `android:usesCleartextTraffic="true"`
- `network_security_config.xml`

---

## ⏱️ Tiempo Total Estimado

- ✅ Paso 1: 2 min
- ✅ Paso 2: 3 min
- ✅ Paso 3: 2 min
- ✅ Paso 4: 3 min
- ✅ Paso 5: 1 min
- ✅ Paso 6: Ya hecho
- ✅ Paso 7: 5 min

**Total: ~15 minutos**

---

## 📋 Verificación Final

- [ ] WAMP icono VERDE
- [ ] Base de datos `parking_lasalle` existe
- [ ] Tablas creadas (4 tablas)
- [ ] Usuario admin insertado
- [ ] 5 salles insertadas
- [ ] Archivos PHP en `C:\wamp64\www\parking_api\`
- [ ] Login funciona en Postman
- [ ] ApiClient.java con URL correcta
- [ ] App Android compila sin errores
- [ ] Login funciona desde app Android

---

## 🎯 Próximos Pasos

1. **Implementar Register** - Crear cuenta nueva
2. **Completar vehicles.php** - CRUD de vehículos
3. **Completar sessions.php** - CRUD de sesiones
4. **Crear salles.php** - Listar salles
5. **Implementar en RealtimeDatabaseManager** - Conectar métodos stub con API

---

**¿Algún problema?** Revisa `WAMP_SETUP_GUIDE.md` para guía completa.

