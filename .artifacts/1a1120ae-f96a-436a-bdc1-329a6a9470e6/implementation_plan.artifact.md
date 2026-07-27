# Plan de Cumplimiento para Android 16 (API 36) y Play Store

Este plan detalla las verificaciones y cambios necesarios para asegurar que la aplicación cumpla con las políticas de Play Store al targetear Android 16 (API 36), minimizando riesgos de comportamiento inesperado.

## Análisis de Situación Actual
La aplicación ya tiene configurado `targetSdk = 36` y `compileSdk = 36`. Sin embargo, Play Store ha notificado que la versión actual (posiblemente la que está en producción) no cumple con las políticas por targetear una versión inferior a la requerida (Android 16).

## User Review Required

> [!IMPORTANT]
> **Bloqueo de Orientación:** Android 16 ignora las restricciones de orientación (`portrait`) en pantallas grandes (>600dp). Se recomienda probar la interfaz en modo horizontal o tablets para evitar elementos estirados.
> **Almacenamiento:** Se está solicitando `WRITE_EXTERNAL_STORAGE`, el cual está obsoleto para API 35+. Se debe revisar si es realmente necesario o si se puede migrar a Scoped Storage.

## Cambios Propuestos

### 1. Interfaz de Usuario (Edge-to-Edge)
Android 15 y 16 fuerzan el modo edge-to-edge. Aunque `InicioActivity` ya llama a `enableEdgeToEdge()`, debemos asegurar que todos los Composables manejan correctamente los insets.

#### [MODIFY] [InicioActivity.kt](file:///C:/Users/Montajes/StudioProjects/HobreCamionV2/app/src/main/java/com/rfz/appflotal/presentation/ui/inicio/ui/InicioActivity.kt)
- Verificar el uso de `Scaffold` y asegurar que `innerPadding` se aplique a todos los contenidos.
- Asegurar que elementos fijos (banners de anuncios, barras de estado personalizadas) no se solapen con las barras del sistema.

### 2. Navegación (Predictive Back)
Se utiliza `BackHandler` en varios lugares. Esto es correcto para Android 16.
- Verificar que no existan overrides manuales de `onBackPressed()` en la actividad que puedan interferir.

### 3. Servicios en Primer Plano (Foreground Services)
`HombreCamionService` usa el tipo `connectedDevice`.
- Asegurar que se maneje correctamente el ciclo de vida, especialmente si en el futuro se migra a `dataSync` (que tiene límite de 6 horas en API 35+).

### 4. Bluetooth (Android 16 Bond Loss)
Android 16 introduce nuevos Intents para el manejo de pérdida de vínculo (`bond loss`).
#### [MODIFY] [BluetoothRepository.kt](file:///C:/Users/Montajes/StudioProjects/HobreCamionV2/app/src/main/java/com/rfz/appflotal/data/repository/bluetooth/BluetoothRepository.kt)
- Agregar soporte para `ACTION_KEY_MISSING` y `ACTION_ENCRYPTION_CHANGE` para una reconexión más robusta en Android 16.

### 5. Permisos y Manifiesto
#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Montajes/StudioProjects/HobreCamionV2/app/src/main/java/com/rfz/appflotal/src/main/AndroidManifest.xml)
- Revisar `WRITE_EXTERNAL_STORAGE`.
- Verificar si se requiere el nuevo permiso de Red Local si la app realiza descubrimiento de dispositivos en la red LAN (aparte de Bluetooth).

## Plan de Verificación

### Manual Verification
- Ejecutar la aplicación en un emulador con Android 15/16.
- Verificar que la barra de estado y la barra de navegación no oculten botones o texto.
- Probar la rotación en un dispositivo de pantalla grande (tablet o emulador resizable) para ver cómo se adapta la UI de "Portrait" forzado.
- Validar el flujo de reconexión Bluetooth apagando y encendiendo el Bluetooth del sistema.
