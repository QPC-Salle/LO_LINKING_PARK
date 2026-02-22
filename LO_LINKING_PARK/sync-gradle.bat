@echo off
REM Script para sincronizar Gradle y resolver problemas de referencias no resueltas

echo Limpiando caché de Gradle...
rmdir /s /q .gradle
rmdir /s /q build
cd app
rmdir /s /q build
cd ..

echo.
echo Sincronizando Gradle...
call gradlew.bat --refresh-dependencies clean build

echo.
echo Sincronización completada. Abre Android Studio y presiona Ctrl+Shift+S para sincronizar el IDE.
pause
