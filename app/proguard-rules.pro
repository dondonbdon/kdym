# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see9
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 1. Protect your data models (You already had this)
-keep class dev.bti.kdym.data.models.** { *; }

# 2. Protect your viewmodels where EventRSVP lives (THIS FIXES YOUR CRASH)
-keep class dev.bti.kdym.viewmodels.** { *; }

# 3. Keep generic signatures
# Essential for Firebase to understand lists and maps (e.g., List<String> or Map<String, Object>)
-keepattributes Signature

# 4. Keep Annotations
# Ensures that annotations like @Keep, @PropertyName, or @Serializable aren't stripped
-keepattributes *Annotation*

# 5. Keep Inner Classes and Enclosing Methods
# Highly recommended for Kotlin Coroutines and Flows (which you are using heavily in EventRepository)
-keepattributes EnclosingMethod, InnerClasses

# 6. Kotlinx Serialization
-keepclassmembers class ** {
    @kotlinx.serialization.SerialName <fields>;
}
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keep class kotlinx.serialization.json.** { *; }
-keep class * extends kotlinx.serialization.internal.GeneratedSerializer { *; }
-keepclassmembers class * {
    *** Companion;
    *** serializer(...);
}

# 7. Serializers
-keep class * implements kotlinx.serialization.KSerializer { *; }
-keep class dev.bti.kdym.data.local.serializers.** { *; }

