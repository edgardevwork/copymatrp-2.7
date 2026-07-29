#-dontshrink
#-keepclasseswithmembernames class com.nvidia.devtech.*, com.wardrumstudios.utils.*

-keep class com.wardrumstudios.utils.* { *; }

-keep class ru.edgar.nlremake.network.** { *; }

-dontwarn javax.servlet.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

-keep class ru.edgar.nlremake.model.** { *; }
-dontwarn androidx.datastore.**
-keep class androidx.datastore.** { *; }
-keep class com.google.firebase.** { *; }
-keep class android.support.** { *; }
-keep class org.json.** { *; }

# made by EDGAR 3.0
-keep class com.nvidia.** {*;}
-keep class androidx.fragment.** {*;}
-keep class ru.edgar.space.GameRender {*;}
-keep class ru.edgar.space.SAMP {*;}
-keep class ru.edgar.space.core.ui.** {*;}
-keep class ru.edgar.launcher.activity.MainActivity {*;}
-keep class ru.edgar.nlremake.model.** {*;}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn com.liulishuo.**
-keep class com.liulishuo.** {*;}
-dontwarn okhttp3.**
-keep class okhttp3.** {*;}
-dontwarn org.ini4j.**
-keep class org.ini4j.** {*;}
-keepclassmembers  class com.nvidia.** {*;}
-keepclassmembers  class ru.edgar.nlremake.network.** { *; }
-keepclassmembers  class com.google.firebase.** { *; }
-keepclassmembers  class androidx.fragment.** {*;}
-keepclassmembers  class android.support.** { *; }
-keepclassmembers  class org.json.** { *; }
-keepclassmembers  class ru.edgar.space.GameRender {*;}
-keepclassmembers  class ru.edgar.space.SAMP {*;}
-keepclassmembers  class ru.edgar.space.core.ui.** {*;}
-keepclassmembers  class ru.edgar.launcher.activity.MainActivity {*;}
-keepclassmembers  class ru.edgar.nlremake.model.** {*;}
#-keepclassmembers  class ru.edgar.space.core.ui.edit.EditCamera {*;}
-keepclassmembers  class retrofit2.** { *; }
-keepclassmembers  class com.liulishuo.** {*;}
-keepclassmembers  class okhttp3.** {*;}
-keepclassmembers  class org.ini4j.** {*;}
-keepclasseswithmembernames class * {
     native <methods>;
}
-dontwarn okio.**
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepclassmembers class * extends retrofit2.CallAdapter {
   public <methods>;
}
-keepclassmembers class * implements retrofit2.Converter {
   public <methods>;
}
-keepclasseswithmembers class * {
   @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
   @retrofit2.http.* <methods>;
}
-keepattributes RuntimeVisibleAnnotations
-keepattributes AnnotationDefault
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# EDGAR 3.0

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile, LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
#-dontobfuscate
