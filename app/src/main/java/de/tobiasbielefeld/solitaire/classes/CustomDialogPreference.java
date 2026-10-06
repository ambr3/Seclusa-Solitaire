package de.tobiasbielefeld.solitaire.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceViewHolder;

/**
 * AndroidX DialogPreference with the old onBindDialogView / onDialogClosed hooks.
 */
public class CustomDialogPreference extends DialogPreference {

    private AlertDialog activeDialog;

    public CustomDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDialogPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        View title = holder.findViewById(android.R.id.title);
        if (title instanceof android.widget.TextView) {
            ((android.widget.TextView) title).setSingleLine(false);
            ((android.widget.TextView) title).setMaxLines(3);
        }
    }

    protected void onBindDialogView(View view) {
    }

    protected void onDialogClosed(boolean positiveResult) {
    }

    protected void onDialogShown(@NonNull AlertDialog dialog) {
    }

    void attachActiveDialog(@Nullable AlertDialog dialog) {
        activeDialog = dialog;
    }

    @Nullable
    public AlertDialog getDialog() {
        return activeDialog;
    }

    /** Unwraps ContextThemeWrapper so callers can reach the Settings activity. */
    @Nullable
    protected Context getActivityContext() {
        Context context = getContext();
        while (context instanceof android.content.ContextWrapper) {
            if (context instanceof android.app.Activity) {
                return context;
            }
            context = ((android.content.ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
