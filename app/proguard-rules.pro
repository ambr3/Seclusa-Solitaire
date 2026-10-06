# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:/Users/$USER/AppData/Local/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Settings preference classes are inflated from XML by class name.
-keep class de.tobiasbielefeld.solitaire.ui.settings.Settings$* { *; }
-keep class de.tobiasbielefeld.solitaire.ui.settings.HeaderPreference { *; }
-keep class de.tobiasbielefeld.solitaire.classes.CustomDialogPreference { *; }
-keep class de.tobiasbielefeld.solitaire.classes.CustomPreferenceDialogFragment { *; }
-keep class de.tobiasbielefeld.solitaire.classes.ListPreferenceWithSummary { *; }
-keep class de.tobiasbielefeld.solitaire.classes.CustomCheckBoxPreference { *; }
-keep class de.tobiasbielefeld.solitaire.dialogs.DialogPreference* { *; }
-keep class de.tobiasbielefeld.solitaire.checkboxpreferences.** { *; }

# Preference summary copy uses ClipboardManager — never enabled in this app.
-assumevalues class androidx.preference.Preference {
    boolean isCopyingEnabled() return false;
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}
