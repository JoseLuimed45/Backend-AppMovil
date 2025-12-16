# ============================================
# REGLAS DE PROGUARD/R8 PARA AJICOLOR APP
# ============================================

# Preservar información de debug en desarrollo
# Comentar en release si quieres ofuscar completamente
-keepattributes SourceFile,LineNumberTable

# ============================================
# GSON - JSON Serialization
# ============================================
-keepclassmembers class com.example.appajicolorgrupo4.data.remote.dto.** {
  <fields>;
}
-keep class com.example.appajicolorgrupo4.data.remote.dto.** { *; }

-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }

# Preservar nombres de campos anotados con @SerializedName
-keepclassmembers class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================
# RETROFIT
# ============================================
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# Preservar interfaces de servicio de Retrofit
-keep interface com.example.appajicolorgrupo4.data.remote.** { *; }

# ============================================
# OKHTTP
# ============================================
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# ============================================
# KOTLIN
# ============================================
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.Metadata {
  public <methods>;
}

# Metadata para data classes
-keepclassmembers class * {
  @kotlin.jvm.JvmField <fields>;
}

# ============================================
# ANDROIDX / JETPACK COMPOSE
# ============================================
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.material3.** { *; }

# ============================================
# ROOM DATABASE
# ============================================
-keep class androidx.room.** { *; }
-keep interface androidx.room.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
  public <init>();
}

# Database entities
-keep class com.example.appajicolorgrupo4.data.local.entity.** { *; }

# ============================================
# GENERAL RULES
# ============================================
# No ofuscar view models
-keep class com.example.appajicolorgrupo4.viewmodel.** { *; }

# No ofuscar repositories
-keep class com.example.appajicolorgrupo4.domain.repository.** { *; }

# No ofuscar navegación
-keep class com.example.appajicolorgrupo4.navigation.** { *; }

# Preservar enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preservar closures de funciones
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================
# ADVERTENCIAS IGNORADAS (SEGURAS)
# ============================================
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn com.google.gson.**
-dontwarn androidx.**
-dontwarn kotlin.**