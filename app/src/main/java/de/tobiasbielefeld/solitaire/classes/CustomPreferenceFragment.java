package de.tobiasbielefeld.solitaire.classes;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import androidx.recyclerview.widget.RecyclerView;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.reinitializeData;

/**
 * PreferenceFragmentCompat with section pill layouts and custom dialog dispatch.
 */
public class CustomPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onAttach(@NonNull Context context) {
        reinitializeData(context);
        super.onAttach(context);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Subclasses call setPreferencesFromResource, then applySectionCards().
    }

    protected void finishPreferenceSetup() {
        applySectionCards();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = getListView();
        if (list != null) {
            float density = getResources().getDisplayMetrics().density;
            int pad = (int) (6 * density);
            list.setPadding(pad, pad, pad, pad);
            list.setClipToPadding(false);
            list.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof CustomDialogPreference) {
            if (getParentFragmentManager().findFragmentByTag("pref_dialog") != null) {
                return;
            }
            CustomPreferenceDialogFragment fragment =
                    CustomPreferenceDialogFragment.newInstance(preference.getKey());
            fragment.setTargetFragment(this, 0);
            fragment.show(getParentFragmentManager(), "pref_dialog");
            return;
        }
        super.onDisplayPreferenceDialog(preference);
    }

    private void applySectionCards() {
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            return;
        }

        int count = screen.getPreferenceCount();
        java.util.ArrayList<Preference> rootPrefs = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            Preference preference = screen.getPreference(i);
            if (preference instanceof de.tobiasbielefeld.solitaire.ui.settings.HeaderPreference) {
                continue;
            }
            if (preference instanceof PreferenceGroup && !(preference instanceof PreferenceScreen)) {
                applySectionCard((PreferenceGroup) preference);
            } else if (!(preference instanceof PreferenceGroup)) {
                rootPrefs.add(preference);
            } else {
                preference.setLayoutResource(R.layout.settings_preference_row_middle);
            }
        }
        applyRootPrefs(rootPrefs);
    }

    private void applySectionCard(PreferenceGroup category) {
        category.setLayoutResource(R.layout.settings_preference_category);
        int count = category.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference preference = category.getPreference(i);
            if (preference instanceof PreferenceNightMode) {
                continue; // keeps System / Light / Dark tab layout
            }
            preference.setLayoutResource(rowLayout(preference));
        }
    }

    private void applyRootPrefs(java.util.ArrayList<Preference> rootPrefs) {
        for (Preference preference : rootPrefs) {
            preference.setLayoutResource(rowLayout(preference));
        }
    }

    private int rowLayout(Preference preference) {
        boolean checkbox = preference instanceof TwoStatePreference;
        return checkbox
                ? R.layout.settings_preference_checkbox_row_single
                : R.layout.settings_preference_row_single;
    }
}
