# ------------------------------- 自定义区 -------------------------------


-keepattributes Signature
-keepattributes *Annotation*
-keep class com.android.puy.puymvvm.** { *; }
-keep interface com.android.puy.puymvvm.** { *; }
-dontwarn com.android.puy.puymvvm.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# ------------------------------- 自定义区 end -------------------------------

-keep class com.orhanobut.hawk.** { *; }