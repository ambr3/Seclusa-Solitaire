package de.tobiasbielefeld.solitaire.classes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceDialogFragmentCompat;

/**
 * Hosts dialogs for {@link CustomDialogPreference} subclasses.
 */
public class CustomPreferenceDialogFragment extends PreferenceDialogFragmentCompat {

    public static CustomPreferenceDialogFragment newInstance(String key) {
        CustomPreferenceDialogFragment fragment = new CustomPreferenceDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        CustomDialogPreference preference = preference();
        if (preference != null && dialog instanceof AlertDialog) {
            preference.attachActiveDialog((AlertDialog) dialog);
        }
        return dialog;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        CustomDialogPreference preference = preference();
        if (preference != null) {
            preference.onBindDialogView(view);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        CustomDialogPreference preference = preference();
        Dialog dialog = getDialog();
        if (preference != null && dialog instanceof AlertDialog) {
            preference.attachActiveDialog((AlertDialog) dialog);
            preference.onDialogShown((AlertDialog) dialog);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        CustomDialogPreference preference = preference();
        if (preference != null) {
            preference.onDialogClosed(positiveResult);
            preference.attachActiveDialog(null);
        }
    }

    private CustomDialogPreference preference() {
        return (CustomDialogPreference) getPreference();
    }
}
