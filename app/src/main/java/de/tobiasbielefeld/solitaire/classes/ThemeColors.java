/*
 * Copyright (C) 2016  Tobias Bielefeld
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
 */

package de.tobiasbielefeld.solitaire.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.tobiasbielefeld.solitaire.R;

/**
 * Resolves the theme style to use for a screen, based on the user selected theme colour.
 * Each screen gets its base theme (no action bar, action bar or settings) and the saved
 * colour is applied on top of it.
 */

public class ThemeColors {

    public static final String COLOR_GREEN = "green";
    public static final String COLOR_ORANGE = "orange";
    public static final String COLOR_BLUE = "blue";
    public static final String COLOR_PURPLE = "purple";

    private ThemeColors() {
    }

    public static int getThemeRes(Context context, int baseTheme) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String color = preferences.getString(context.getString(R.string.pref_key_theme_color),
                context.getString(R.string.default_theme_color));

        switch (color) {
            case COLOR_GREEN:
                return getVariant(baseTheme, R.style.AppThemeNoActionBar_Green,
                        R.style.AppThemeActionBar_Green, R.style.AppThemeSettings_Green,
                        R.style.AppThemeDialog_Green, R.style.AppThemeSettingsDialog_Green);
            case COLOR_BLUE:
                return getVariant(baseTheme, R.style.AppThemeNoActionBar_Blue,
                        R.style.AppThemeActionBar_Blue, R.style.AppThemeSettings_Blue,
                        R.style.AppThemeDialog_Blue, R.style.AppThemeSettingsDialog_Blue);
            case COLOR_PURPLE:
                return getVariant(baseTheme, R.style.AppThemeNoActionBar_Purple,
                        R.style.AppThemeActionBar_Purple, R.style.AppThemeSettings_Purple,
                        R.style.AppThemeDialog_Purple, R.style.AppThemeSettingsDialog_Purple);
            case COLOR_ORANGE:
            default:
                return baseTheme;
        }
    }

    private static int getVariant(int baseTheme, int noActionBarVariant, int actionBarVariant,
                                  int settingsVariant, int dialogVariant, int settingsDialogVariant) {
        if (baseTheme == R.style.AppThemeNoActionBar) {
            return noActionBarVariant;
        } else if (baseTheme == R.style.AppThemeActionBar) {
            return actionBarVariant;
        } else if (baseTheme == R.style.AppThemeSettings) {
            return settingsVariant;
        } else if (baseTheme == R.style.AppThemeDialog) {
            return dialogVariant;
        } else if (baseTheme == R.style.AppThemeSettingsDialog) {
            return settingsDialogVariant;
        }

        return baseTheme;
    }
}