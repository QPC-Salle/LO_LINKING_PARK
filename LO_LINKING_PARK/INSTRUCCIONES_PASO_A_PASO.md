# 📋 Instrucciones Paso a Paso - Refactorización

## 🎯 Objetivo
Verificar y validar que la refactorización de los modelos está completa y funcional.

---

## ✅ Paso 1: Sincronizar Gradle (1 min)

### En Android Studio:

**Opción A - Método más rápido:**
```
Presiona: Ctrl + Shift + S
```

**Opción B - Método manual:**
```
1. Clic en "File" en la barra superior
2. Selecciona "Sync Project with Gradle Files"
3. Espera a que termine (puede tardar 1-5 minutos)
```

**¿Qué debe ocurrir?**
- El IDE mostrará "Gradle sync finished"
- Los errores "Cannot resolve symbol" desaparecerán

---

## ✅ Paso 2: Limpiar Proyecto (1 min)

### En Android Studio:

```
1. Clic en "Build" en la barra superior
2. Selecciona "Clean Project"
3. Espera a que termine
```

**¿Qué debe ocurrir?**
- Se eliminará la carpeta "build"
- Aparecerá un mensaje de éxito

---

## ✅ Paso 3: Compilar Proyecto (2-5 min)

### En Android Studio:

```
1. Clic en "Build" en la barra superior
2. Selecciona "Rebuild Project"
3. Espera a que termine
```

**¿Qué debe ocurrir?**
- Mostrará "Build completed successfully"
- No debe haber ERRORES (los warnings son normales)

---

## ✅ Paso 4: Verificar Clases

### Verificar Usuario.java
```
1. Abre: app/src/main/java/com/example/lo_linking_park/model/Usuario.java
2. Verifica:
   - [ ] Constructor vacío presente (línea ~15)
   - [ ] Constructor completo presente (línea ~21)
   - [ ] Constructor simplificado presente (línea ~33)
   - [ ] getCreatEl() devuelve long (línea ~102)
   - [ ] NO hay "import java.util.Date"
3. Cierra el archivo
```

### Verificar Vehicle.java
```
1. Abre: app/src/main/java/com/example/lo_linking_park/model/Vehicle.java
2. Verifica:
   - [ ] Constructor vacío presente (línea ~17)
   - [ ] Constructor completo presente (línea ~23)
   - [ ] Constructor simplificado presente (línea ~38)
   - [ ] getCreatEl() devuelve long (línea ~124)
   - [ ] NO hay "import java.util.Date"
3. Cierra el archivo
```

### Verificar Session.java
```
1. Abre: app/src/main/java/com/example/lo_linking_park/model/Session.java
2. Verifica:
   - [ ] Constructor vacío presente (línea ~22)
   - [ ] Constructor completo presente (línea ~29)
   - [ ] Constructor simplificado presente (línea ~49)
   - [ ] getDataInici() devuelve long (línea ~98)
   - [ ] NO hay "import java.util.Date"
3. Cierra el archivo
```

---

## ✅ Paso 5: Revisar Documentación

### Lee estos documentos en orden:

```
1. RESUMEN_VISUAL_REFACTORING.md (5 minutos)
   → Entiende qué se hizo

2. USAGE_GUIDE_REFACTORED_MODELS.md (10 minutos)
   → Ve ejemplos de uso

3. CHECKLIST_REFACTORING_TECNICO.md (10 minutos)
   → Verifica detalles técnicos
```

---

## ✅ Paso 6: Crear Archivo de Prueba (Opcional)

Si quieres verificar que todo funciona, crea un simple test:

### Crear MainActivity.java (si no existe)

```java
package com.example.lo_linking_park;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lo_linking_park.model.Usuario;
import com.example.lo_linking_park.model.Vehicle;
import com.example.lo_linking_park.model.Session;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Probar Usuario
        Usuario usuario = new Usuario("Joan", "García", "joan@example.com", "612345678");
        Log.d("Test", "Usuario creado: " + usuario.getNom());
        
        // Probar Vehicle
        Vehicle vehicle = new Vehicle("uid123", "ABC1234", "Toyota", "Corolla", "Blanco", 2023);
        Log.d("Test", "Vehículo creado: " + vehicle.getMatricula());
        
        // Probar Session
        Session session = new Session("uid123", "veh123", "salle123", 120, 15, 41.3874, 2.1686);
        Log.d("Test", "Sesión creada con estado: " + session.getEstat());
    }
}
```

---

## ⚠️ Solución de Problemas

### Problema 1: Error "Cannot resolve symbol"
```
CAUSA: Gradle no se ha sincronizado
SOLUCIÓN:
1. Presiona Ctrl+Shift+S
2. Espera a que termine
3. Si sigue, invalida caché: File → Invalidate Caches → Invalidate and Restart
```

### Problema 2: Error de compilación
```
CAUSA: Dependencias no descaregadas
SOLUCIÓN:
1. Build → Clean Project
2. Build → Rebuild Project
3. Si sigue: File → Invalidate Caches → Invalidate and Restart
```

### Problema 3: Warnings "is never used"
```
CAUSA: Normal - métodos aún no usados
SOLUCIÓN: No hagas nada. Desaparecerán cuando uses las clases
```

### Problema 4: No puedo sincronizar Gradle
```
CAUSA: Gradle wrapper corrupto
SOLUCIÓN:
1. Elimina carpeta: .gradle
2. Elimina carpeta: build
3. Presiona Ctrl+Shift+S
```

---

## ✅ Verificación Final

Antes de considerar completado, verifica:

### Compilación
- [ ] Build completado sin ERRORES
- [ ] Warnings normales (se ignoran)

### Código
- [ ] Usuario.java: 3 constructores, long timestamps
- [ ] Vehicle.java: 3 constructores, long timestamps
- [ ] Session.java: 3 constructores, long timestamps

### Firebase
- [ ] Cada clase tiene constructor vacío
- [ ] No hay imports de java.util.Date
- [ ] RealtimeDatabaseManager usa System.currentTimeMillis()

### Documentación
- [ ] Leído RESUMEN_VISUAL_REFACTORING.md
- [ ] Leído USAGE_GUIDE_REFACTORED_MODELS.md
- [ ] Entiendes cómo usar las clases

---

## 🎯 Próximos Pasos (Después de Verificación)

### 1. Crear Activities
```java
public class MyActivity extends AppCompatActivity {
    private RealtimeDatabaseManager dbManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = RealtimeDatabaseManager.getInstance();
        // Usar dbManager...
    }
}
```

### 2. Usar Métodos
```java
// Crear usuario
Usuario usuario = new Usuario("Nom", "Cognoms", "email@email.com", "123456789");
dbManager.createUser(usuario, callback);

// Crear vehículo
Vehicle vehicle = new Vehicle(uid, "ABC1234", "Toyota", "Corolla", "Blanco", 2023);
dbManager.addVehicle(vehicle, callback);
```

### 3. Manejar Callbacks
```java
dbManager.addVehicle(vehicle, new RealtimeDatabaseManager.VehicleCallback() {
    @Override
    public void onSuccess(Vehicle savedVehicle) {
        Log.d("Success", "Vehículo guardado: " + savedVehicle.getId());
    }
    
    @Override
    public void onError(String error) {
        Log.e("Error", error);
    }
});
```

---

## 📚 Recursos de Referencia

| Documento | Cuando Usar |
|-----------|------------|
| RESUMEN_VISUAL_REFACTORING.md | Para entender rápido |
| USAGE_GUIDE_REFACTORED_MODELS.md | Para ver ejemplos |
| CHECKLIST_REFACTORING_TECNICO.md | Para detalles técnicos |
| ESTADO_COMPILACION_REFACTORING.md | Para validar compilación |
| INDICE_DOCUMENTACION.md | Índice de todo |

---

## ✨ Tiempo Total Estimado

```
Paso 1 (Gradle): 1-5 minutos
Paso 2 (Clean): 1 minuto
Paso 3 (Rebuild): 2-5 minutos
Paso 4 (Verificar): 5 minutos
Paso 5 (Leer docs): 15-25 minutos
────────────────────────────
TOTAL: 25-40 minutos
```

---

## 🎉 Resultado

Si completaste todos los pasos:
- ✅ Gradle sincronizado
- ✅ Proyecto compilado
- ✅ Clases validadas
- ✅ Documentación revisada
- ✅ **LISTO PARA USAR** 🚀

---

## 📞 Si Algo No Funciona

1. **Lee**: ESTADO_COMPILACION_REFACTORING.md
2. **Revisa**: Sección "Solución de Problemas" arriba
3. **Verifica**: Que Gradle está sincronizado
4. **Intenta**: File → Invalidate Caches → Invalidate and Restart

---

## 🎯 Última Verificación

Abre Android Studio y:
```
1. Abre Usuario.java
2. Verifica: Sin errores rojos, constructor vacío presente
3. Abre Vehicle.java
4. Verifica: Sin errores rojos, constructor vacío presente
5. Abre Session.java
6. Verifica: Sin errores rojos, constructor vacío presente
```

Si todo está verde (sin errores rojos) → ✅ COMPLETADO

---

**Fecha**: 22 de Febrero de 2026  
**Versión**: 1.0  
**Status**: Instrucciones Verificadas ✅
