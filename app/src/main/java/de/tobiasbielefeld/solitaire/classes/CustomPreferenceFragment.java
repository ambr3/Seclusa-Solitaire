package de.tobiasbielefeld.solitaire.classes;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.widget.ListView;

import android.view.View;

import de.tobiasbielefeld.solitaire.R;

import static de.tobiasbielefeld.solitaire.SharedData.reinitializeData;

/**
 * Custom PreferenceFragment, to override onAttach. If the app got killed within a
 * PreferenceFragment and restarted, the data has to be reinitialized
 */

public class CustomPreferenceFragment extends PreferenceFragment {

    @Override
    public void onAttach(Context context) {
        reinitializeData(context);
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            reinitializeData(activity);
        }
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        applySectionCards();
    }

    /**
     * Renders every PreferenceCategory as one rounded card: each category's first, middle and
     * last row is assigned the rounded-top / flat / rounded-bottom layout.
     */
    private void applySectionCards() {
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            return;
        }

        int count = screen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference preference = screen.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                applySectionCard((PreferenceGroup) preference);
            } else {
                preference.setLayoutResource(R.layout.settings_preference_row_middle);
            }
        }

        View view = getView();
        if (view != null) {
            ListView listView = view.findViewById(android.R.id.list);
            if (listView != null) {
                listView.setDivider(null);
                listView.setDividerHeight(0);
            }
        }
    }

    private void applySectionCard(PreferenceGroup category) {
        category.setLayoutResource(R.layout.settings_preference_category);
        int count = category.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference preference = category.getPreference(i);
            boolean checkbox = preference instanceof TwoStatePreference;
            int layout;
            if (count == 1) {
                layout = checkbox ? R.layout.settings_preference_checkbox_row_single : R.layout.settings_preference_row_single;
            } else if (i == 0) {
                layout = checkbox ? R.layout.settings_preference_checkbox_row_first : R.layout.settings_preference_row_first;
            } else if (i == count - 1) {
                layout = checkbox ? R.layout.settings_preference_checkbox_row_last : R.layout.settings_preference_row_last;
            } else {
                layout = checkbox ? R.layout.settings_preference_checkbox_row_middle : R.layout.settings_preference_row_middle;
            }
            preference.setLayoutResource(layout);
        }
    }
}