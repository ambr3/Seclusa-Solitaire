package de.tobiasbielefeld.solitaire.helper;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Maps saved appearance preference values to {@link AppCompatDelegate} night modes.
 */
public final class NightMode {

    public static final String SYSTEM = "system";
    public static final String LIGHT = "light";
    public static final String DARK = "dark";

    private NightMode() {
    }

    public static void apply(String mode) {
        int nightMode;
        if (LIGHT.equals(mode)) {
            nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        } else if (DARK.equals(mode)) {
            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}
