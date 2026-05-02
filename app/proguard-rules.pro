# ============================================================
# FloatingBallApp ProGuard Rules
# ============================================================

# --- Android 组件（Manifest 声明，系统通过反射实例化，必须 keep）---

-keep class com.example.floatingball.MainActivity { *; }
-keep class com.example.floatingball.AppListActivity { *; }
-keep class com.example.floatingball.service.FloatingBallService { *; }
-keep class com.example.floatingball.service.KeepAliveService { *; }
-keep class com.example.floatingball.receiver.BootCompletedReceiver { *; }
-keep class com.example.floatingball.application.FloatingBallApplication { *; }

# --- View 子类（XML layout 中通过反射构造，但本项目 View 全部代码创建，可混淆）---
# FloatingBallView 和 MenuPopupView 都是代码 new 出来，不需要 keep
# 如果未来在 XML 中引用，需取消注释：
# -keep class com.example.floatingball.view.FloatingBallView { <init>(android.content.Context, android.util.AttributeSet); }
# -keep class com.example.floatingball.view.MenuPopupView { <init>(android.content.Context, android.util.AttributeSet); }

# --- 回调接口（Service 中通过 setter 传入的 lambda/匿名内部类，ProGuard 自动处理）---

# --- SharedPreferences key（字符串常量，ProGuard 不混淆字符串，无需额外规则）---

# --- 泛型擦除相关 ---
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# --- 枚举 ---
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- Parcelable 子类 ---
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# --- Serializable ---
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# --- R 类 ---
-keep class **.R$* { *; }

# --- BuildConfig ---
-keep class com.example.floatingball.BuildConfig { *; }

# --- Native 方法（当前无 JNI，预留）---
# -keepclasseswithmembernames class * {
#     native <methods>;
# }
