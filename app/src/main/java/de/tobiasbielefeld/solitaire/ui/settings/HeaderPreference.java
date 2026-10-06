package de.tobiasbielefeld.solitaire.ui.settings;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import de.tobiasbielefeld.solitaire.R;

/**
 * Settings section header shown as a preference pill; selected state uses the activated drawable.
 */
public class HeaderPreference extends Preference {

    private boolean selected;

    public HeaderPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.settings_header_row);
    }

    public HeaderPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.settings_header_row);
    }

    public HeaderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.settings_header_row);
    }

    public HeaderPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.settings_header_row);
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            // Prefer deferring notify so header list isn't mid-layout (tablet multipane).
            notifyChanged();
        }
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View item = holder.itemView;
        item.setActivated(selected);
        item.setBackgroundResource(selected
                ? R.drawable.settings_header_activated
                : R.drawable.preference_card_single);

        int titleColor = selected
                ? resolveThemeColor(R.attr.colorOnPrimary, Color.WHITE)
                : resolveThemeColor(R.attr.colorPrimary, Color.BLACK);
        int summaryColor = selected
                ? Color.argb(220, Color.red(titleColor), Color.green(titleColor), Color.blue(titleColor))
                : resolveThemeColor(R.attr.colorOnSurfaceVariant, Color.GRAY);

        TextView title = (TextView) holder.findViewById(android.R.id.title);
        TextView summary = (TextView) holder.findViewById(android.R.id.summary);
        if (title != null) {
            title.setTextColor(titleColor);
        }
        if (summary != null) {
            summary.setTextColor(summaryColor);
        }
    }

    private int resolveThemeColor(int attr, int fallback) {
        TypedValue value = new TypedValue();
        if (getContext().getTheme().resolveAttribute(attr, value, true)) {
            return value.data;
        }
        return fallback;
    }
}
