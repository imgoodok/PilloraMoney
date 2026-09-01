# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Natan\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# --- Hilt/Dagger ---
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends dagger.hilt.components.SingletonComponent { *; }

# --- Room ---
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# --- Firebase ---
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keepattributes Signature
-keepattributes *Annotation*

# --- Kotlin Serialization ---
-keepattributes *Annotation*, EnclosingMethod, InnerClasses
-keep class kotlinx.serialization.json.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# --- Coil ---
-keep class coil.** { *; }
-dontwarn coil.**

# --- Credentials API ---
-keep class androidx.credentials.** { *; }

# --- PilloraMoney Models ---
# Keep your data models to prevent R8 from obfuscating field names used in Firebase/Serialization
-keep class com.example.pilloramoney.data.model.** { *; }
