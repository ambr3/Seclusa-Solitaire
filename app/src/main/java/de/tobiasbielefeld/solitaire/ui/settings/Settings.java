/*
 * Copyright (C) 2016  Tobias Bielefeld
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 */

package de.tobiasbielefeld.solitaire.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.Locale;

import de.tobiasbielefeld.solitaire.LoadGame;
import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceFourColorMode;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceHideAutoCompleteButton;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceHideMenuButton;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceHideScore;
import de.tobiasbielefeld.solitaire.checkboxpreferences.CheckBoxPreferenceHideTime;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CustomPreferenceFragment;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceBackgroundColor;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceCardBackground;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceCards;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceOnlyForThisGame;
import de.tobiasbielefeld.solitaire.dialogs.DialogPreferenceTextColor;
import de.tobiasbielefeld.solitaire.games.FortyEight;
import de.tobiasbielefeld.solitaire.games.Klondike;
import de.tobiasbielefeld.solitaire.games.NapoleonsTomb;
import de.tobiasbielefeld.solitaire.games.Pyramid;

import static android.content.Context.MODE_PRIVATE;
import static de.tobiasbielefeld.solitaire.SharedData.*;
import static de.tobiasbielefeld.solitaire.helper.Preferences.*;

import de.tobiasbielefeld.solitaire.helper.NightMode;

/**
 * Settings activity — AndroidX PreferenceFragmentCompat host (no PreferenceActivity).
 */
public class Settings extends AppCompatPreferenceActivity
        implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String STATE_SELECTED_HEADER = "state_selected_header";
    private static final String TAG_HEADERS = "settings_headers";
    private static final String TAG_DETAIL = "settings_detail";

    private Preference preferenceMenuBarPosition;
    private Preference preferenceMenuColumns;
    private Preference preferenceMaxNumberUndos;
    private Preference preferenceGameLayoutMargins;

    private CheckBoxPreference preferenceSingleTapAllGames;
    private CheckBoxPreference preferenceTapToSelect;
    private CheckBoxPreference preferenceImmersiveMode;

    private DialogPreferenceCards preferenceCards;
    private DialogPreferenceCardBackground preferenceCardBackground;
    private DialogPreferenceBackgroundColor preferenceBackgroundColor;
    private DialogPreferenceTextColor preferenceTextColor;
    private DialogPreferenceOnlyForThisGame dialogPreferenceOnlyForThisGame;

    private CheckBoxPreferenceFourColorMode preferenceFourColorMode;
    private CheckBoxPreferenceHideAutoCompleteButton preferenceHideAutoCompleteButton;
    private CheckBoxPreferenceHideMenuButton preferenceHideMenuButton;
    private CheckBoxPreferenceHideScore preferenceHideScore;
    private CheckBoxPreferenceHideTime preferenceHideTime;

    private PreferenceCategory categoryOnlyForThisGame;
    CustomizationPreferenceFragment customizationPreferenceFragment;

    static Intent returnIntent;

    private View headersContainer;
    private View detailContainer;
    private String selectedHeaderKey;
    private boolean multiPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reinitializeData(this);
        prefs.setCriticalSettings();

        if (returnIntent == null) {
            returnIntent = new Intent();
        }

        setContentView(R.layout.activity_settings);
        headersContainer = findViewById(R.id.settings_headers);
        detailContainer = findViewById(R.id.settings_detail);
        multiPane = isLargeTablet(this);

        if (savedInstanceState != null) {
            selectedHeaderKey = savedInstanceState.getString(STATE_SELECTED_HEADER);
        }

        if (getSupportFragmentManager().findFragmentByTag(TAG_HEADERS) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_headers, new SettingsHeadersFragment(), TAG_HEADERS)
                    .commit();
        }

        applyPaneVisibility(getSupportFragmentManager().findFragmentByTag(TAG_DETAIL) != null);

        if (multiPane && selectedHeaderKey == null) {
            headersContainer.post(() -> openHeader("hdr_customize", false));
        } else if (multiPane && selectedHeaderKey != null
                && getSupportFragmentManager().findFragmentByTag(TAG_DETAIL) == null) {
            String key = selectedHeaderKey;
            headersContainer.post(() -> openHeader(key, false));
        }

        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!multiPane && getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    selectedHeaderKey = null;
                    applyPaneVisibility(false);
                    refreshHeaderSelection();
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public void setContentView(@LayoutRes int layoutResId) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        TypedValue colorPrimary = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimary, true);

        Toolbar toolbar = new Toolbar(this);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setTitle(R.string.title_activity_settings);
        toolbar.setTitleTextColor(colorPrimary.data);
        android.graphics.drawable.Drawable closeIcon = getDrawable(R.drawable.ic_close);
        if (closeIcon != null) {
            closeIcon.setTint(colorPrimary.data);
            toolbar.setNavigationIcon(closeIcon);
        }
        toolbar.setNavigationContentDescription(R.string.game_close);
        toolbar.setNavigationOnClickListener(v -> finish());

        root.addView(toolbar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        View content = LayoutInflater.from(this).inflate(layoutResId, root, false);
        root.addView(content, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        super.setContentView(root);
        headersContainer = findViewById(R.id.settings_headers);
        detailContainer = findViewById(R.id.settings_detail);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedHeaderKey != null) {
            outState.putString(STATE_SELECTED_HEADER, selectedHeaderKey);
        }
    }

    @Override
    public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, @NonNull Preference pref) {
        String key = pref.getKey();
        if (key != null && key.startsWith("hdr_")) {
            openHeader(key, true);
            return true;
        }
        String fragmentName = pref.getFragment();
        if (fragmentName == null) {
            return false;
        }
        openFragment(fragmentName, true);
        return true;
    }

    void openHeader(String headerKey, boolean userClick) {
        SettingsHeadersFragment headers = (SettingsHeadersFragment)
                getSupportFragmentManager().findFragmentByTag(TAG_HEADERS);
        if (headers == null) {
            return;
        }
        Preference pref = headers.findPreference(headerKey);
        if (pref == null || pref.getFragment() == null) {
            return;
        }
        selectedHeaderKey = headerKey;
        headers.setSelectedHeader(headerKey);
        openFragment(pref.getFragment(), userClick && !multiPane);
    }

    private void openFragment(String fragmentName, boolean addToBackStack) {
        Fragment fragment = getSupportFragmentManager().getFragmentFactory()
                .instantiate(getClassLoader(), fragmentName);
        androidx.fragment.app.FragmentTransaction tx = getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_detail, fragment, TAG_DETAIL);
        if (addToBackStack) {
            tx.addToBackStack(null);
        }
        tx.commit();
        applyPaneVisibility(true);
    }

    private void applyPaneVisibility(boolean showingDetail) {
        if (headersContainer == null || detailContainer == null) {
            return;
        }
        if (multiPane) {
            headersContainer.setVisibility(View.VISIBLE);
            detailContainer.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams lp = headersContainer.getLayoutParams();
            if (lp instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) lp).weight = 1f;
                headersContainer.setLayoutParams(lp);
            }
            ViewGroup.LayoutParams dp = detailContainer.getLayoutParams();
            if (dp instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) dp).weight = 2f;
                detailContainer.setLayoutParams(dp);
            }
        } else if (showingDetail) {
            headersContainer.setVisibility(View.GONE);
            detailContainer.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams dp = detailContainer.getLayoutParams();
            if (dp instanceof LinearLayout.LayoutParams) {
                ((LinearLayout.LayoutParams) dp).weight = 1f;
                detailContainer.setLayoutParams(dp);
            }
        } else {
            headersContainer.setVisibility(View.VISIBLE);
            detailContainer.setVisibility(View.GONE);
        }
    }

    private void refreshHeaderSelection() {
        SettingsHeadersFragment headers = (SettingsHeadersFragment)
                getSupportFragmentManager().findFragmentByTag(TAG_HEADERS);
        if (headers != null) {
            headers.setSelectedHeader(selectedHeaderKey);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_KEY_SETTINGS_ONLY_FOR_THIS_GAME)) {

            if (preferenceFourColorMode != null) {
                preferenceFourColorMode.update();
            }

            if (preferenceHideAutoCompleteButton != null) {
                preferenceHideAutoCompleteButton.update();
            }

            if (preferenceHideMenuButton != null) {
                preferenceHideMenuButton.update();
            }

            if (preferenceHideScore != null) {
                preferenceHideScore.update();
            }

            if (preferenceHideTime != null) {
                preferenceHideTime.update();
            }

            if (preferenceCards != null) {
                preferenceCards.updateSummary();
            }

            if (preferenceCardBackground != null) {
                preferenceCardBackground.updateSummary();
            }

            if (preferenceBackgroundColor != null) {
                preferenceBackgroundColor.updateSummary();
            }

            if (preferenceTextColor != null) {
                preferenceTextColor.updateSummary();
            }

            Card.updateCardDrawableChoice();
            Card.updateCardBackgroundChoice();

            updatePreferenceGameLayoutMarginsSummary();
            updatePreferenceMenuBarPositionSummary();

            returnIntent.putExtra(getString(R.string.intent_update_game_layout), true);
            returnIntent.putExtra(getString(R.string.intent_update_menu_bar), true);
            returnIntent.putExtra(getString(R.string.intent_background_color), true);
            returnIntent.putExtra(getString(R.string.intent_text_color), true);
            returnIntent.putExtra(getString(R.string.intent_update_score_visibility), true);
            returnIntent.putExtra(getString(R.string.intent_update_time_visibility), true);
        }
        if (key.equals(PREF_KEY_CARD_DRAWABLES)) {
            Card.updateCardDrawableChoice();
        } else if (key.equals(PREF_KEY_CARD_BACKGROUND) || key.equals(PREF_KEY_CARD_BACKGROUND_COLOR)) {
            Card.updateCardBackgroundChoice();
        } else if (key.equals(PREF_KEY_HIDE_STATUS_BAR)) {
            showOrHideStatusBar();
        } else if (key.equals(PREF_KEY_ORIENTATION)) {
            setOrientation();
        } else if (key.equals(PREF_KEY_LEFT_HANDED_MODE)) {
            if (gameLogic != null) {
                gameLogic.mirrorStacks();
            }
        } else if (key.equals(PREF_KEY_MENU_COLUMNS_PORTRAIT) || key.equals(PREF_KEY_MENU_COLUMNS_LANDSCAPE)) {
            updatePreferenceMenuColumnsSummary();
        } else if (key.equals(PREF_KEY_LANGUAGE)) {
            bitmaps.resetMenuPreviews();
            restartApplication();
        } else if (key.equals(PREF_KEY_THEME_COLOR) || key.equals(PREF_KEY_DYNAMIC_COLORS)
                || key.equals(PREF_KEY_NIGHT_MODE)) {
            if (key.equals(PREF_KEY_NIGHT_MODE)) {
                NightMode.apply(sharedPreferences.getString(key, DEFAULT_NIGHT_MODE));
            }
            bitmaps.setResources(this);
            bitmaps.resetMenuPreviews();
            restartApplication();
        } else if (key.equals(PREF_KEY_MENU_BAR_POS_LANDSCAPE) || key.equals(PREF_KEY_MENU_BAR_POS_PORTRAIT)) {
            updatePreferenceMenuBarPositionSummary();
            returnIntent.putExtra(getString(R.string.intent_update_menu_bar), true);
        } else if (key.equals(PREF_KEY_4_COLOR_MODE)) {
            Card.updateCardDrawableChoice();

            if (preferenceCards != null) {
                preferenceCards.updateSummary();
            }
        } else if (key.equals(PREF_KEY_MOVEMENT_SPEED)) {
            if (animate != null) {
                animate.updateMovementSpeed();
            }
        } else if (key.equals(PREF_KEY_FORCE_TABLET_LAYOUT)) {
            restartApplication();
        } else if (key.equals(PREF_KEY_SINGLE_TAP_ALL_GAMES)) {
            if (sharedPreferences.getBoolean(key, false) && preferenceTapToSelect != null) {
                preferenceTapToSelect.setChecked(false);
            }
        } else if (key.equals(PREF_KEY_TAP_TO_SELECT_ENABLED)) {
            if (sharedPreferences.getBoolean(key, false) && preferenceSingleTapAllGames != null) {
                preferenceSingleTapAllGames.setChecked(false);
            }
        } else if (key.equals(PREF_KEY_MAX_NUMBER_UNDOS)) {
            if (recordList != null) {
                recordList.setMaxRecords();
            }

            updatePreferenceMaxNumberUndos();
        } else if (key.equals(PREF_KEY_GAME_LAYOUT_MARGINS_PORTRAIT) || key.equals(PREF_KEY_GAME_LAYOUT_MARGINS_LANDSCAPE)) {
            updatePreferenceGameLayoutMarginsSummary();
            returnIntent.putExtra(getString(R.string.intent_update_game_layout), true);
        } else if (key.equals(PREF_KEY_HIDE_MENU_BUTTON)) {
            returnIntent.putExtra(getString(R.string.intent_update_menu_bar), true);
        } else if (key.equals(PREF_KEY_IMMERSIVE_MODE)) {
            returnIntent.putExtra(getString(R.string.intent_update_game_layout), true);
        } else if (key.equals(PREF_KEY_BACKGROUND_COLOR) || key.equals(PREF_KEY_BACKGROUND_COLOR_CUSTOM) || key.equals(PREF_KEY_BACKGROUND_COLOR_TYPE)) {
            returnIntent.putExtra(getString(R.string.intent_background_color), true);
        } else if (key.equals(PREF_KEY_TEXT_COLOR)) {
            returnIntent.putExtra(getString(R.string.intent_text_color), true);
        } else if (key.equals(PREF_KEY_HIDE_SCORE)) {
            returnIntent.putExtra(getString(R.string.intent_update_score_visibility), true);
        } else if (key.equals(PREF_KEY_HIDE_TIME)) {
            returnIntent.putExtra(getString(R.string.intent_update_time_visibility), true);
        } else if (key.equals(PREF_KEY_ENSURE_MOVABILITY)) {
            ArrayList<LoadGame.AllGameInformation> gameInfoList = lg.getOrderedGameInfoList();

            for (int i = 0; i < lg.getGameCount(); i++) {
                SharedPreferences sharedPref = getSharedPreferences(gameInfoList.get(i).getSharedPrefName(), MODE_PRIVATE);
                sharedPref.edit().putInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES, sharedPref.getInt(PREF_KEY_ENSURE_MOVABILITY_MIN_MOVES, gameInfoList.get(i).getEnsureMovabilityMoves())).apply();
            }
        } else if (key.equals(PREF_KEY_KLONDIKE_DRAW)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Klondike)), this);
        } else if (key.equals(PREF_KEY_CANFIELD_DRAW)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Canfield)), this);
        } else if (key.equals(PREF_KEY_SPIDER_DIFFICULTY)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Spider)), this);
        } else if (key.equals(PREF_KEY_SPIDERETTE_DIFFICULTY)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Spiderette)), this);
        } else if (key.equals(PREF_KEY_YUKON_RULES)) {
            showToast(String.format(getString(R.string.settings_restart_game), getString(R.string.games_Yukon)), this);
        } else if (key.equals(PREF_KEY_FORTYEIGHT_LIMITED_RECYCLES)) {
            if (currentGame instanceof FortyEight) {
                gameLogic.toggleRecycles(prefs.getSavedFortyEightLimitedRecycles());
            }
        } else if (key.equals(PREF_KEY_PYRAMID_LIMITED_RECYCLES)) {
            if (currentGame instanceof Pyramid) {
                gameLogic.toggleRecycles(prefs.getSavedPyramidLimitedRecycles());
            }
        } else if (key.equals(PREF_KEY_PYRAMID_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof Pyramid) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_PYRAMID_NUMBER_OF_RECYCLES);
            }
        } else if (key.equals(PREF_KEY_NAPOLEONSTOMB_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof NapoleonsTomb) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_NAPOLEONSTOMB_NUMBER_OF_RECYCLES);
            }
        } else if (key.equals(PREF_KEY_FORTYEIGHT_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof FortyEight) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_FORTYEIGHT_NUMBER_OF_RECYCLES);
            }
        } else if (key.equals(PREF_KEY_KLONDIKE_LIMITED_RECYCLES)) {
            if (currentGame instanceof Klondike) {
                gameLogic.toggleRecycles(prefs.getSavedKlondikeLimitedRecycles());
            }
        } else if (key.equals(PREF_KEY_KLONDIKE_NUMBER_OF_RECYCLES)) {
            if (currentGame instanceof Klondike) {
                gameLogic.setNumberOfRecycles(key, DEFAULT_KLONDIKE_NUMBER_OF_RECYCLES);
            }
        } else if (key.startsWith("difficulty_")) {
            String sharedPrefName = key.substring("difficulty_".length());

            for (LoadGame.AllGameInformation gameInfo : lg.getOrderedGameInfoList()) {
                if (gameInfo.getSharedPrefName().equals(sharedPrefName)) {
                    showToast(String.format(getString(R.string.settings_restart_game), gameInfo.getName(getResources())), this);
                    break;
                }
            }
        }
    }

    @Override
    public void finish() {
        setResult(Activity.RESULT_OK, returnIntent);
        super.finish();
    }

    private void updatePreferenceMenuColumnsSummary() {
        if (preferenceMenuColumns == null) {
            return;
        }
        int portraitValue = prefs.getSavedMenuColumnsPortrait();
        int landscapeValue = prefs.getSavedMenuColumnsLandscape();
        String text = String.format(Locale.getDefault(), "%s: %d\n%s: %d",
                getString(R.string.settings_portrait), portraitValue,
                getString(R.string.settings_landscape), landscapeValue);
        preferenceMenuColumns.setSummary(text);
    }

    private void updatePreferenceGameLayoutMarginsSummary() {
        if (preferenceGameLayoutMargins == null) {
            return;
        }
        String textPortrait = "", textLandscape = "";
        switch (prefs.getSavedGameLayoutMarginsPortrait()) {
            case 0: textPortrait = getString(R.string.settings_game_layout_margins_none); break;
            case 1: textPortrait = getString(R.string.settings_game_layout_margins_small); break;
            case 2: textPortrait = getString(R.string.settings_game_layout_margins_medium); break;
            case 3: textPortrait = getString(R.string.settings_game_layout_margins_large); break;
        }
        switch (prefs.getSavedGameLayoutMarginsLandscape()) {
            case 0: textLandscape = getString(R.string.settings_game_layout_margins_none); break;
            case 1: textLandscape = getString(R.string.settings_game_layout_margins_small); break;
            case 2: textLandscape = getString(R.string.settings_game_layout_margins_medium); break;
            case 3: textLandscape = getString(R.string.settings_game_layout_margins_large); break;
        }
        String text = String.format(Locale.getDefault(), "%s: %s\n%s: %s",
                getString(R.string.settings_portrait), textPortrait,
                getString(R.string.settings_landscape), textLandscape);
        preferenceGameLayoutMargins.setSummary(text);
    }

    private void updatePreferenceMaxNumberUndos() {
        if (preferenceMaxNumberUndos == null) {
            return;
        }
        preferenceMaxNumberUndos.setSummary(Integer.toString(prefs.getSavedMaxNumberUndos()));
    }

    private void updatePreferenceMenuBarPositionSummary() {
        if (preferenceMenuBarPosition == null) {
            return;
        }
        String portrait = prefs.getSavedMenuBarPosPortrait().equals(DEFAULT_MENU_BAR_POSITION_PORTRAIT)
                ? getString(R.string.settings_menu_bar_position_bottom)
                : getString(R.string.settings_menu_bar_position_top);
        String landscape = prefs.getSavedMenuBarPosLandscape().equals(DEFAULT_MENU_BAR_POSITION_LANDSCAPE)
                ? getString(R.string.settings_menu_bar_position_right)
                : getString(R.string.settings_menu_bar_position_left);
        String text = String.format(Locale.getDefault(), "%s: %s\n%s: %s",
                getString(R.string.settings_portrait), portrait,
                getString(R.string.settings_landscape), landscape);
        preferenceMenuBarPosition.setSummary(text);
    }

    public void hidePreferenceOnlyForThisGame() {
        if (dialogPreferenceOnlyForThisGame != null && dialogPreferenceOnlyForThisGame.canBeHidden()
                && customizationPreferenceFragment != null && categoryOnlyForThisGame != null) {
            customizationPreferenceFragment.getPreferenceScreen().removePreference(categoryOnlyForThisGame);
        }
    }

    public static class SettingsHeadersFragment extends CustomPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_headers, rootKey);
            finishPreferenceSetup();
        }

        void setSelectedHeader(@Nullable String key) {
            PreferenceScreen screen = getPreferenceScreen();
            if (screen == null) {
                return;
            }
            for (int i = 0; i < screen.getPreferenceCount(); i++) {
                Preference pref = screen.getPreference(i);
                if (pref instanceof HeaderPreference) {
                    ((HeaderPreference) pref).setSelected(key != null && key.equals(pref.getKey()));
                }
            }
        }
    }

    public static class CustomizationPreferenceFragment extends CustomPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_customize, rootKey);
            finishPreferenceSetup();

            Settings settings = (Settings) getActivity();
            if (settings == null) {
                return;
            }
            settings.customizationPreferenceFragment = this;
            settings.preferenceMenuBarPosition = findPreference(getString(R.string.pref_key_menu_bar_position));
            settings.preferenceCards = findPreference(getString(R.string.pref_key_cards));
            settings.preferenceGameLayoutMargins = findPreference(getString(R.string.pref_key_game_layout_margins));
            settings.preferenceCardBackground = findPreference(getString(R.string.pref_key_cards_background));
            settings.preferenceBackgroundColor = findPreference(getString(R.string.pref_key_background_color));
            settings.preferenceTextColor = findPreference(getString(R.string.pref_key_text_color));
            settings.preferenceFourColorMode = findPreference(getString(R.string.dummy_pref_key_4_color_mode));
            settings.preferenceHideAutoCompleteButton = findPreference(getString(R.string.dummy_pref_key_hide_auto_complete_button));
            settings.preferenceHideMenuButton = findPreference(getString(R.string.dummy_pref_key_hide_menu_button));
            settings.preferenceHideScore = findPreference(getString(R.string.dummy_pref_key_hide_score));
            settings.preferenceHideTime = findPreference(getString(R.string.dummy_pref_key_hide_time));
            settings.dialogPreferenceOnlyForThisGame = findPreference(getString(R.string.pref_key_settings_only_for_this_game));
            settings.categoryOnlyForThisGame = findPreference(getString(R.string.pref_cat_key_only_for_this_game));
            if (settings.categoryOnlyForThisGame != null) {
                settings.categoryOnlyForThisGame.setLayoutResource(R.layout.empty);
            }
            if (settings.preferenceFourColorMode != null) settings.preferenceFourColorMode.update();
            if (settings.preferenceHideAutoCompleteButton != null) settings.preferenceHideAutoCompleteButton.update();
            if (settings.preferenceHideMenuButton != null) settings.preferenceHideMenuButton.update();
            if (settings.preferenceHideScore != null) settings.preferenceHideScore.update();
            if (settings.preferenceHideTime != null) settings.preferenceHideTime.update();
            if (settings.preferenceCards != null) settings.preferenceCards.updateSummary();
            if (settings.preferenceCardBackground != null) settings.preferenceCardBackground.updateSummary();
            if (settings.preferenceBackgroundColor != null) settings.preferenceBackgroundColor.updateSummary();
            if (settings.preferenceTextColor != null) settings.preferenceTextColor.updateSummary();
            if (settings.dialogPreferenceOnlyForThisGame != null) {
                settings.dialogPreferenceOnlyForThisGame.updateAppearance();
            }
            settings.updatePreferenceGameLayoutMarginsSummary();
            settings.updatePreferenceMenuBarPositionSummary();
            settings.hidePreferenceOnlyForThisGame();
        }

        @Override
        public void onDestroyView() {
            Settings settings = (Settings) getActivity();
            if (settings != null && settings.customizationPreferenceFragment == this) {
                settings.customizationPreferenceFragment = null;
            }
            super.onDestroyView();
        }
    }

    public static class GamesPreferenceFragment extends CustomPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            prefs.setCriticalGameSettings();
            setPreferencesFromResource(R.xml.pref_games, rootKey);
            finishPreferenceSetup();
        }
    }

    public static class OtherPreferenceFragment extends CustomPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_other, rootKey);
            finishPreferenceSetup();
            Settings settings = (Settings) getActivity();
            if (settings == null) {
                return;
            }
            settings.preferenceImmersiveMode = findPreference(getString(R.string.pref_key_immersive_mode));
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT && settings.preferenceImmersiveMode != null) {
                settings.preferenceImmersiveMode.setEnabled(false);
            }
        }
    }

    public static class SoundPreferenceFragment extends CustomPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_sounds, rootKey);
            finishPreferenceSetup();
        }
    }

    public static class MenuPreferenceFragment extends CustomPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_menu, rootKey);
            finishPreferenceSetup();
            Settings settings = (Settings) getActivity();
            if (settings == null) {
                return;
            }
            settings.preferenceMenuColumns = findPreference(getString(R.string.pref_key_menu_columns));
            settings.updatePreferenceMenuColumnsSummary();
        }
    }

    public static class AdditionalMovementsPreferenceFragment extends CustomPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_movement_methods, rootKey);
            finishPreferenceSetup();
            Settings settings = (Settings) getActivity();
            if (settings == null) {
                return;
            }
            settings.preferenceSingleTapAllGames = findPreference(getString(R.string.pref_key_single_tap_all_games));
            settings.preferenceTapToSelect = findPreference(getString(R.string.pref_key_tap_to_select_enable));
        }
    }

    public static class ExpertSettingsPreferenceFragment extends CustomPreferenceFragment {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.pref_expert_settings, rootKey);
            finishPreferenceSetup();
            Settings settings = (Settings) getActivity();
            if (settings == null) {
                return;
            }
            settings.preferenceMaxNumberUndos = findPreference(getString(R.string.pref_key_max_number_undos));
            settings.updatePreferenceMaxNumberUndos();
        }
    }
}
