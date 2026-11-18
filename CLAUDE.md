# Arquitectura y Reglas de Desarrollo Android

App Android en Kotlin: Jetpack Compose, Hilt, Room, Retrofit y WorkManager.
Este proyecto sigue estrictamente **MVVM**, **Patrón Repositorio** y **Flujo de Datos Unidireccional (UDF)** con Jetpack Compose.

## 1. Capa de UI (Jetpack Compose)
- **Stateless UI:** separa cada pantalla en dos Composables:
  - uno con estado (`XRoute`), que recibe el `ViewModel` y colecta el estado con `collectAsStateWithLifecycle()`;
  - uno sin estado (`XContent`), que solo recibe `UiState` y lambdas.
- **Navegación:** nunca pases el `NavController` a los Composables de pantalla; usa callbacks (`onBackClick`, `onItemClick`).
- **Previsualizaciones:** incluye siempre un `@Preview` para los Composables sin estado.

## 2. ViewModel y manejo de estado
- **Estado único:** cada pantalla tiene un `UiState` inmutable (`data class` o `sealed interface`).
- **Exposición:** usa un `MutableStateFlow` privado y expón un `StateFlow` público de solo lectura.
- **Efectos de un solo uso:** usa un `Channel` convertido a `Flow` para la navegación o los mensajes temporales (*Toasts/Snackbars*).
- **Sin referencias Android:** prohibido importar `Context`, `View`, `Uri` u otras clases `android.*`, y clases de Compose, dentro del `ViewModel`.
  - La UI convierte lo que llega del sistema (por ejemplo, el `Uri` de la cámara) antes de pasárselo.

## 3. Capa de Dominio y Datos
- **Repositorios:** el `ViewModel` nunca accede directo a Retrofit, Room o DataStore. Toda operación pasa por una interfaz `Repository`:
  - interfaz `XRepository` e implementación `XRepositoryImpl`;
  - se enlazan con `@Binds` en `di/`.
- **Casos de uso:** una operación que coordina varios repositorios va en `domain/<módulo>/` con `operator fun invoke`. El `ViewModel` no orquesta repositorios entre sí.
- **Mapeo de datos:** los modelos de red (`DTO`) y de base de datos (`Entity`) se mapean a modelos limpios dentro del repositorio, antes de subir al `ViewModel`.
- **Concurrencia:** usa `suspend` y `Flow`. La red y el disco corren en `Dispatchers.IO` dentro de la capa de datos; en clases nuevas, inyecta el dispatcher para poder probarlo.
- **Transacciones:** una escritura que toca varias tablas va en una sola transacción de Room (`withTransaction`).
- **Archivos:** las imágenes viven como archivos; la base de datos guarda solo su ruta.
- **Inyección de dependencias:** usa Hilt para inyectar los repositorios en los `ViewModels` y las fuentes de datos en los repositorios.

## 4. Trabajo en segundo plano
- Un `Worker` es un punto de entrada, como un `ViewModel`: no tiene reglas de negocio y delega a un caso de uso o a un repositorio.
- La notificación y la programación de WorkManager viven en clases propias, fuera del `Worker`.

## 5. Tamaño y responsabilidad
- Máximo **500 líneas por archivo**. Si un cambio lo rebasa, divide el archivo por responsabilidad antes de seguir. Por ejemplo:
  - una pantalla, en pantalla, componentes y estilos;
  - un repositorio, en almacenamiento local y subida.
- Una clase tiene una sola razón para cambiar.
- Código heredado:
  - un archivo de más de 500 líneas que se modifica no debe crecer; la parte que se toca se extrae a su propio archivo;
  - no se refactoriza el código que la tarea no toca.

## 6. Pruebas
- Solo las críticas: reglas de negocio, contratos del servidor y migraciones.
- Los nombres siguen el formato `metodo_condicion_resultado`.
- Los dobles de prueba son fakes de la interfaz del repositorio.
- Las respuestas del servidor en las pruebas son solo mensajes reales y confirmados.
- Instrumentadas: no uses `connectedAndroidTest` en un teléfono con datos reales, porque desinstala la app y borra su información. Instala el APK de prueba y córrelo con `am instrument`.

## 7. Estilo
- El código nuevo va en español, como el módulo SuperAsesor.
- Los comentarios ocupan 1 o 2 líneas y explican el porqué, no el qué.
