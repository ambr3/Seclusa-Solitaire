package de.tobiasbielefeld.solitaire.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.DynamicColorsOptions;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.ThemeColors;
import de.tobiasbielefeld.solitaire.helper.EdgeToEdge;
import de.tobiasbielefeld.solitaire.helper.LocaleChanger;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * AppCompat host for Settings (replaces deprecated PreferenceActivity).
 */
public abstract class AppCompatPreferenceActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected int getBaseThemeRes() {
        return R.style.AppThemeSettingsDialog;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(ThemeColors.getThemeRes(this, getBaseThemeRes()));
        super.onCreate(savedInstanceState);
        if (prefs != null && prefs.getSavedDynamicColors() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            DynamicColors.applyToActivityIfAvailable(this,
                    new DynamicColorsOptions.Builder().build());
        }
        applyDialogWindowSize();
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applyDialogWindowSize();
    }

    /** Wider in landscape — Material dialog defaults are too narrow for Settings multipane. */
    protected void applyDialogWindowSize() {
        float width = getResources().getDisplayMetrics().widthPixels;
        float height = getResources().getDisplayMetrics().heightPixels;
        boolean landscape = width > height;
        float widthFrac = landscape ? 0.96f : 0.92f;
        float heightFrac = landscape ? 0.92f : 0.85f;
        getWindow().setLayout((int) (width * widthFrac), (int) (height * heightFrac));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleChanger.onAttach(base));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        EdgeToEdge.applyContentInsets(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prefs.registerListener(this);
        showOrHideStatusBar();
        setOrientation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        prefs.unregisterListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showOrHideStatusBar() {
        if (prefs.getSavedHideStatusBar()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    protected void restartApplication() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());

        if (i != null) {
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(i);
        }
    }

    protected void setOrientation() {
        switch (prefs.getSavedOrientation()) {
            case 1:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
                break;
            case 2:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case 3:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 4:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
        }
    }
}
