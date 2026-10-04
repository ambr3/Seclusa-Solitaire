package de.tobiasbielefeld.solitaire.classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
        // Root-level toggles (not inside a PreferenceCategory) still need MaterialSwitch rows.
        java.util.ArrayList<Preference> rootPrefs = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            Preference preference = screen.getPreference(i);
            if (preference instanceof PreferenceGroup && !(preference instanceof PreferenceScreen)) {
                applySectionCard((PreferenceGroup) preference);
            } else if (!(preference instanceof PreferenceGroup)) {
                rootPrefs.add(preference);
            } else {
                preference.setLayoutResource(R.layout.settings_preference_row_middle);
            }
        }
        applyRootPrefs(rootPrefs);

        View view = getView();
        if (view != null) {
            ListView listView = view.findViewById(android.R.id.list);
            if (listView != null) {
                float density = getResources().getDisplayMetrics().density;
                int pad = (int) (6 * density);
                listView.setDivider(new ColorDrawable(Color.TRANSPARENT));
                listView.setDividerHeight((int) (8 * density));
                listView.setPadding(pad, pad, pad, pad);
                listView.setClipToPadding(false);
                listView.setSelector(android.R.color.transparent);
                listView.setCacheColorHint(Color.TRANSPARENT);
                // Avoid pressed/activated overlays washing out pill text on tablet multipane.
                listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
            }
        }
    }

    private void applySectionCard(PreferenceGroup category) {
        category.setLayoutResource(R.layout.settings_preference_category);
        int count = category.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            category.getPreference(i).setLayoutResource(rowLayout(category.getPreference(i), i, count));
        }
    }

    private void applyRootPrefs(java.util.ArrayList<Preference> rootPrefs) {
        int count = rootPrefs.size();
        for (int i = 0; i < count; i++) {
            rootPrefs.get(i).setLayoutResource(rowLayout(rootPrefs.get(i), i, count));
        }
    }

    private int rowLayout(Preference preference, int index, int count) {
        // Each option is its own rounded pill (not fused first/middle/last blocks).
        boolean checkbox = preference instanceof TwoStatePreference;
        return checkbox
                ? R.layout.settings_preference_checkbox_row_single
                : R.layout.settings_preference_row_single;
    }
}