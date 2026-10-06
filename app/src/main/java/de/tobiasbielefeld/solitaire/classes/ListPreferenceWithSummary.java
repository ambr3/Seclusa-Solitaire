package de.tobiasbielefeld.solitaire.classes;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

/**
 * ListPreference that keeps the summary in sync with the selected entry.
 * AndroidX already expands %s; this still forces a refresh after setValue.
 */
public class ListPreferenceWithSummary extends ListPreference {

    public ListPreferenceWithSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListPreferenceWithSummary(Context context) {
        super(context);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        setSummary(getEntry());
    }

    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(getEntry());
    }
}
