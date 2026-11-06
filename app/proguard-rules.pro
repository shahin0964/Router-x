# Proguard rules for ROUTER X
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class com.router.x.data.model.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
