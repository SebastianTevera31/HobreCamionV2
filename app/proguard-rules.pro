# ==================================================================================================
# REGLAS DE OFUSCACIÓN (R8) - HombreCamión (com.rfz.appflotal)
# ==================================================================================================
# R8 corre en "modo completo" por defecto desde AGP 8: elimina todo lo que no ve referenciado
# directamente desde el código y renombra clases, métodos y campos. Cualquier cosa que se resuelva
# por NOMBRE en tiempo de ejecución (reflexión, JSON, nombres de enum guardados en BD/DataStore,
# clases referenciadas desde XML) necesita una regla aquí.
#
# La mayoría de las librerías (Hilt, Room, WorkManager, Firebase, Ads, CameraX, Coil, OkHttp,
# DataStore, Navigation, kotlinx.serialization) ya traen sus propias reglas de consumidor dentro
# del AAR; aquí solo va lo que esas reglas no cubren o lo que es propio del proyecto.

# ---------------------------------------------------------------------------
# Diagnóstico
# ---------------------------------------------------------------------------
# Vuelca TODAS las reglas combinadas (propias + de librerías) para revisar por qué R8 mantiene o
# descarta una clase. Si algo falla en release, agregar temporalmente:
#   -whyareyoukeeping class com.rfz.appflotal.MiClase
-printconfiguration build/outputs/logs/configuration.txt

# Stacktraces legibles: conserva número de línea aunque el código esté ofuscado. Play Console
# re-mapea los crashes con app/build/outputs/mapping/release/mapping.txt (subirlo con cada versión).
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod, Exceptions
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault


# ==================================================================================================
# ENUMS
# ==================================================================================================
# Varios enums se reconstruyen desde un String que viene de fuera del código, por ejemplo:
#   - PaymentPlanType.valueOf(...) con el plan que regresa la API / FCM (AppStatusManagerRepository).
#   - AccessoryVehicleType.fromApi(...) compara contra entries[].name ("TRUCK" / "RV").
#   - Room 2.7 guarda los enums de las entidades por su nombre (name()) en la base de datos.
# Si R8 renombra las constantes o aplica "enum unboxing", el String guardado ya no coincide y
# revienta con "IllegalArgumentException: No enum constant ..." o cae silenciosamente al valor
# por defecto. Se conservan todos los enums de la app con sus nombres.
-keep,allowoptimization enum com.rfz.appflotal.** { *; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# ==================================================================================================
# MODELOS DE RED - RETROFIT + GSON
# ==================================================================================================
# Gson lee y escribe los campos por reflexión. Muchos DTO de este proyecto NO usan @SerializedName
# (ej. CreateAirPressureRatingDto, BaseResponse, LanguageResponse, TireSizeResponse...) y dependen
# del nombre literal del campo (fld_description, id_base...). Si R8 renombra esos campos el JSON
# enviado/recibido deja de coincidir: los valores llegan en null/0 sin ningún error visible.
# Se conservan completos todos los modelos (constructores, campos y nombres).
-keep class com.rfz.appflotal.data.model.** { *; }

# También hay DTOs junto a las interfaces de Retrofit (ej. vialstatus/RoadMapDto). Sin esta regla
# R8 marca la clase como "nunca instanciada" (solo Gson la crea, por reflexión), borra el tipo de
# List<RoadMapDto> y Gson devuelve LinkedTreeMap: ClassCastException al abrir Estado vial.
-keep class com.rfz.appflotal.data.network.client.** { *; }

# Protección para cualquier clase futura fuera de esos paquetes que use @SerializedName: conserva
# la clase (así R8 no la considera "nunca instanciada") y sus campos, aunque permite renombrarla.
-keepclasseswithmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken

# Interfaces de servicio de Retrofit: se instancian por reflexión con retrofit.create().
# Si R8 elimina los métodos de un endpoint que todavía no se invoca desde ningún sitio, la interfaz
# queda vacía, deja de preservarse y el cast de retrofit.create() revienta con ClassCastException
# al arrancar (Hilt construye todos los clientes en NetworkModule).
-keep,allowobfuscation interface com.rfz.appflotal.data.network.client.** { *; }

# Reglas generales de Retrofit para interfaces anotadas con @GET/@POST/... fuera de ese paquete.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# Bug conocido de Retrofit 2.9.0 + R8 full mode (square/retrofit#3751): R8 borra la firma genérica
# del parámetro Continuation<T> que Kotlin agrega a cada "suspend fun" y Retrofit revienta con
# "ClassCastException: java.lang.Class cannot be cast to java.lang.reflect.ParameterizedType".
# Si se actualiza Retrofit a 2.11+ estas tres reglas ya vienen incluidas en la librería.
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*


# ==================================================================================================
# OKHTTP
# ==================================================================================================
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# OkHttp carga un recurso con ruta relativa a esta clase.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# El Network Inspector de Android Studio inyecta un interceptor que llama métodos públicos de
# okhttp3 que la app nunca usa (ej. Headers.toMultimap()). R8 los elimina y, al depurar el build
# minificado desde Studio, las peticiones fallan con NoSuchMethodError. No afecta a usuarios reales.
-keep class okhttp3.Headers { *; }


# ==================================================================================================
# KOTLINX SERIALIZATION
# ==================================================================================================
# Se usa en: caches de DataStore (Alerts, Report, Performance, AppUpdateMessage) y en las rutas
# type-safe de Navigation Compose (CouponRoutes, ForumRoutes, ReportRoute, ServiceRoutes).
# Navigation localiza el serializer de cada ruta (composable<T>, toRoute<T>) y para los "object"
# usa el campo INSTANCE; si R8 los quita revienta con "Serializer for class 'X' is not found".
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.rfz.appflotal.**$$serializer { *; }
-keepclassmembers class com.rfz.appflotal.** {
    *** Companion;
}
-keepclasseswithmembers class com.rfz.appflotal.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep @kotlinx.serialization.Serializable class com.rfz.appflotal.** { *; }


# ==================================================================================================
# ROOM
# ==================================================================================================
# Room ya mantiene HombreCamionDatabase_Impl y los DAO generados. Se protegen además las
# entidades (viven en data.model.**, cubiertas arriba) y cualquier TypeConverter futuro.
-keep class com.rfz.appflotal.data.database.** { *; }
-keepclassmembers class * {
    @androidx.room.TypeConverter <methods>;
}


# ==================================================================================================
# HILT / WORKMANAGER
# ==================================================================================================
# HiltWorkerFactory crea los Workers (SyncCreateTaskWorker, FollowedPromotionsWorker,
# MaintenanceNotificationsWorker, MessagingWorker). WorkManager guarda el nombre de clase del
# Worker en su base de datos interna: si cambia entre versiones, los trabajos encolados antes de
# actualizar la app ya no se pueden instanciar. Por eso se conserva el nombre de la clase.
-keep class * extends androidx.work.ListenableWorker {
    <init>(...);
}
-keep,allowobfuscation,allowshrinking class dagger.hilt.internal.aggregatedroot.codegen.**
-keep,allowobfuscation,allowshrinking class hilt_aggregated_deps.**


# ==================================================================================================
# FIREBASE CLOUD MESSAGING
# ==================================================================================================
# MyFirebaseMessagingService ya se conserva por estar en el Manifest; se conserva el paquete
# completo porque los payloads de FCM se mapean a modelos/enums por nombre.
-keep class com.rfz.appflotal.data.network.messaging.** { *; }


# ==================================================================================================
# LIBRERÍAS DE TERCEROS
# ==================================================================================================
# SweetAlertDialog (paquete real: cn.pedant.SweetAlert). Sus animaciones XML referencian clases por
# nombre (atributo className) y AnimationUtils las resuelve con Class.forName.
-keep class cn.pedant.SweetAlert.** { *; }
-dontwarn cn.pedant.SweetAlert.**

# android-image-cropper (vanniktech, paquete com.canhub.cropper).
-keep class com.canhub.cropper.** { *; }

# Play Services / Engage / Coroutines: avisos por clases opcionales que no existen en runtime.
-dontwarn com.google.android.play.core.**
-dontwarn kotlinx.coroutines.debug.**


# ==================================================================================================
# PENDIENTE / SI SE AGREGA EN EL FUTURO
# ==================================================================================================
# WebView con JavaScript (hoy VialStatusScreen no usa addJavascriptInterface):
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   @android.webkit.JavascriptInterface <methods>;
#}
