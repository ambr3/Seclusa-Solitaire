package de.tobiasbielefeld.solitaire.classes;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.tabs.TabLayout;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.helper.NightMode;

/**
 * Appearance preference: System / Light / Dark pill tabs.
 */
public class PreferenceNightMode extends Preference {

    private static final String[] VALUES = {
            NightMode.SYSTEM,
            NightMode.LIGHT,
            NightMode.DARK
    };

    private String value = NightMode.SYSTEM;
    private boolean binding;

    public PreferenceNightMode(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.preference_night_mode);
        setSelectable(false);
    }

    public PreferenceNightMode(Context context) {
        this(context, null);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        value = getPersistedString(defaultValue != null ? (String) defaultValue : NightMode.SYSTEM);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        TabLayout tabs = (TabLayout) holder.findViewById(R.id.night_mode_tabs);
        if (tabs == null) {
            return;
        }

        binding = true;
        if (tabs.getTabCount() == 0) {
            tabs.addTab(tabs.newTab().setText(R.string.settings_night_mode_system));
            tabs.addTab(tabs.newTab().setText(R.string.settings_night_mode_light));
            tabs.addTab(tabs.newTab().setText(R.string.settings_night_mode_dark));
        }

        int index = indexOf(value);
        TabLayout.Tab selected = tabs.getTabAt(index);
        if (selected != null && tabs.getSelectedTabPosition() != index) {
            selected.select();
        }

        tabs.clearOnTabSelectedListeners();
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (binding) {
                    return;
                }
                setNightMode(VALUES[tab.getPosition()]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        binding = false;
    }

    private void setNightMode(String mode) {
        if (mode.equals(value)) {
            return;
        }
        if (!callChangeListener(mode)) {
            return;
        }
        value = mode;
        persistString(mode);
        NightMode.apply(mode);
    }

    private static int indexOf(String mode) {
        for (int i = 0; i < VALUES.length; i++) {
            if (VALUES[i].equals(mode)) {
                return i;
            }
        }
        return 0;
    }
}
