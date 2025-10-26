# Proguard
# ==================================================================================================
# REGLAS ESPECÍFICAS DEL PROYECTO
# ==================================================================================================
# Agrega aquí reglas personalizadas para tu proyecto.


# ==================================================================================================
# REGLAS PARA ROOM
# ==================================================================================================
# Mantiene las clases que implementan androidx.room.Dao, ya que Room las utiliza mediante reflexión.
-keep interface * implements androidx.room.Dao


# ==================================================================================================
# REGLAS PARA GSON
# ==================================================================================================
# Mantiene las firmas genéricas, necesarias para la correcta resolución de tipos (Ej: List<MiClase>).
-keepattributes Signature

# Mantiene las anotaciones de Gson y sus valores por defecto.
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault

# Mantiene los campos anotados con @Expose, @JsonAdapter, @Since, y @Until.
# Permite la ofuscación de nombres, asumiendo que se usa @SerializedName para la serialización.
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.Expose <fields>;
  @com.google.gson.annotations.JsonAdapter <fields>;
  @com.google.gson.annotations.Since <fields>;
  @com.google.gson.annotations.Until <fields>;
}

# Mantiene las clases y sus miembros anotados con @SerializedName.
# Esto es crucial para que Gson pueda mapear el JSON a los objetos correctos.
-if class *
-keepclasseswithmembers,allowobfuscation class <1> {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Si una clase tiene campos con @SerializedName, mantiene también su constructor sin argumentos.
# Gson necesita este constructor para poder instanciar la clase.
-if class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keepclassmembers,allowobfuscation,allowoptimization class <1> {
  <init>();
}


# ==================================================================================================
# REGLAS PARA RETROFIT
# =================================================_================================================
# Retrofit usa reflexión sobre parámetros genéricos, métodos y anotaciones.
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

# Mantiene los métodos de las interfaces de servicios de Retrofit.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Con R8 en modo completo, es necesario mantener explícitamente las interfaces de Retrofit
# para evitar que sean eliminadas.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Mantiene las interfaces que heredan de otras interfaces de servicio.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# Mantiene las clases usadas como tipo de retorno genérico en las funciones suspend.
-keep,allowoptimization,allowshrinking,allowobfuscation class kotlin.coroutines.Continuation

# Mantiene las clases de tipo de retorno para que no se eliminen sus firmas genéricas.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# Mantiene la clase Response y su firma genérica.
-keep,allowoptimization,allowshrinking,allowobfuscation class retrofit2.Response


# ==================================================================================================
# REGLAS PARA OKHTTP3 Y DEPENDENCIAS COMUNES
# ==================================================================================================
# Ignora warnings de OkHttp3, Okio y otras dependencias comunes que Retrofit puede usar.
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp carga un recurso con una ruta relativa, por lo que el paquete de esta clase debe preservarse.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

