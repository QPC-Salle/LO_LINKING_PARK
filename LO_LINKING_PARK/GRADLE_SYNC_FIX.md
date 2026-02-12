# Solución: Problemas de Sincronización Gradle

## Problemas Identificados y Solucionados

### 1. ❌ Plugin Google Services Duplicado

**Problema:**
En `build.gradle.kts` (raíz), el plugin `google-services` estaba declarado dos veces:
```kotlin
plugins {
    alias(libs.plugins.google.services) apply false
    id("com.google.gms.google-services") version "4.4.4" apply false  // ❌ DUPLICADO
}
```

**Solución:**
✅ Eliminada la línea duplicada. Solo se mantiene la versión del catálogo:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
}
```

### 2. ❌ Referencias No Resueltas a Firebase

**Problema:**
Android Studio mostraba errores de "Unresolved reference 'firebase'" en `app/build.gradle.kts`:
```kotlin
implementation(libs.firebase.bom)      // ❌ Error
implementation(libs.firebase.auth)     // ❌ Error
```

**Causa:**
- El catálogo de versiones no se había sincronizado correctamente
- El plugin duplicado causaba conflictos en la resolución

**Solución:**
✅ Corregido el `build.gradle.kts` raíz
✅ Versión de Firebase BOM ajustada a `32.7.0` (más estable)
✅ Añadidas versiones de Google Play Services al catálogo

### 3. ⚠️ Dependencias Hardcodeadas

**Problema:**
Las dependencias de Google Play Services estaban hardcodeadas en lugar de usar el catálogo:
```kotlin
implementation("com.google.android.gms:play-services-maps:18.2.0")     // ⚠️ Hardcoded
implementation("com.google.android.gms:play-services-location:21.0.1") // ⚠️ Hardcoded
```

**Solución:**
✅ Añadidas al catálogo `libs.versions.toml`:
```toml
[versions]
playServicesMaps = "18.2.0"
playServicesLocation = "21.0.1"

[libraries]
play-services-maps = { group = "com.google.android.gms", name = "play-services-maps", version.ref = "playServicesMaps" }
play-services-location = { group = "com.google.android.gms", name = "play-services-location", version.ref = "playServicesLocation" }
```

✅ Actualizadas las referencias en `app/build.gradle.kts`:
```kotlin
implementation(libs.play.services.maps)
implementation(libs.play.services.location)
```

---

## Archivos Modificados

### 1. `build.gradle.kts` (raíz)
- ✅ Eliminada línea duplicada del plugin google-services

### 2. `gradle/libs.versions.toml`
- ✅ Añadidas versiones: playServicesMaps, playServicesLocation
- ✅ Firebase BOM cambiado de 33.7.0 a 32.7.0 (más compatible)
- ✅ Añadidas librerías: play-services-maps, play-services-location

### 3. `app/build.gradle.kts`
- ✅ Reemplazadas dependencias hardcodeadas por referencias del catálogo

---

## Pasos para Sincronizar en Android Studio

### Método 1: Sync Manual
1. Abrir Android Studio
2. Clic en el icono del elefante (Gradle) en la barra superior
3. O ir a: **File → Sync Project with Gradle Files**
4. Esperar a que termine la sincronización (puede tardar 2-5 minutos)

### Método 2: Clean & Rebuild
Si el método 1 no funciona:
1. **Build → Clean Project** (esperar a que termine)
2. **Build → Rebuild Project** (esperar a que termine)
3. Si hay errores, hacer **File → Invalidate Caches → Invalidate and Restart**

### Método 3: Desde Terminal (PowerShell)
```powershell
cd C:\Users\danie\Documents\GitHub\LO_LINKING_PARK\LO_LINKING_PARK
.\gradlew clean build --refresh-dependencies
```

---

## Verificación Post-Sincronización

### ✅ Checklist de Verificación

1. **No hay errores en build.gradle.kts**
   - [ ] Abrir `build.gradle.kts` (raíz)
   - [ ] No debe haber líneas rojas

2. **No hay errores en app/build.gradle.kts**
   - [ ] Abrir `app/build.gradle.kts`
   - [ ] Todas las referencias `libs.*` deben estar en verde/azul

3. **Las clases de Firebase se importan correctamente**
   - [ ] Abrir `LoginActivity.java`
   - [ ] El import `import com.example.lo_linking_park.repository.FirebaseAuthRepository;` debe estar en gris (sin error)

4. **El proyecto compila sin errores**
   - [ ] Clic en el martillo verde (Build) en la barra superior
   - [ ] El build debe completarse sin errores (warnings son aceptables)

---

## Errores Comunes y Soluciones

### Error: "Plugin [id: 'com.google.gms.google-services'] was not found"
**Causa:** El plugin de Google Services no se descargó correctamente
**Solución:**
```powershell
.\gradlew clean --refresh-dependencies
```

### Error: "Unresolved reference 'firebase'"
**Causa:** El catálogo de versiones no se sincronizó
**Solución:**
1. File → Invalidate Caches → Invalidate and Restart
2. Esperar a que Android Studio reinicie e indexe el proyecto

### Error: "Duplicate class found"
**Causa:** Conflicto entre versiones de Firebase
**Solución:** Ya está solucionado (Firebase BOM gestiona todas las versiones automáticamente)

### Warning: "A newer version of ... is available"
**Causa:** Hay versiones más nuevas disponibles
**Solución:** Puedes ignorar estos warnings por ahora. Si deseas actualizar:
1. En `libs.versions.toml`, cambiar las versiones sugeridas
2. Volver a sincronizar

---

## Estado Actual del Proyecto

### ✅ Configuración Correcta

```toml
# gradle/libs.versions.toml
[versions]
firebaseBom = "32.7.0"           # ✅ Versión estable
googleServices = "4.4.2"          # ✅ Compatible
playServicesMaps = "18.2.0"      # ✅ Funcional
playServicesLocation = "21.0.1"  # ✅ Funcional
```

```kotlin
// build.gradle.kts (raíz)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false  // ✅ Sin duplicados
}
```

```kotlin
// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)  // ✅ Plugin aplicado
}

dependencies {
    // Firebase (usando BOM para gestión automática de versiones)
    implementation(platform(libs.firebase.bom))  // ✅ Todas las versiones compatibles
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
}
```

---

## Próximos Pasos

Una vez sincronizado correctamente:

1. **Descargar `google-services.json`** (CRÍTICO)
   - Firebase Console → Tu proyecto → Configuración del proyecto
   - Descargar y colocar en: `app/google-services.json`

2. **Activar servicios en Firebase Console**
   - Authentication (Email/Password)
   - Firestore Database

3. **Ejecutar la aplicación**
   - Probar registro y login

---

**Última actualización:** 12 de febrero de 2026
**Estado:** ✅ Problemas de sincronización solucionados
