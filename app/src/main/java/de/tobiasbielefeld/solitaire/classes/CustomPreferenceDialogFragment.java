package de.tobiasbielefeld.solitaire.classes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * Dialog host for {@link CustomDialogPreference} — local copy of AndroidX
 * PreferenceDialogFragmentCompat that avoids the ARG_KEY = "key" string MobSF flags.
 */
public class CustomPreferenceDialogFragment extends DialogFragment
        implements DialogInterface.OnClickListener {

    private static final String ARG_PREF = "pref";
    private static final String STATE_TITLE = "CustomPrefDialog.title";
    private static final String STATE_POSITIVE = "CustomPrefDialog.positive";
    private static final String STATE_NEGATIVE = "CustomPrefDialog.negative";
    private static final String STATE_MESSAGE = "CustomPrefDialog.message";
    private static final String STATE_LAYOUT = "CustomPrefDialog.layout";
    private static final String STATE_ICON = "CustomPrefDialog.icon";

    private DialogPreference preference;
    private CharSequence dialogTitle;
    private CharSequence positiveButtonText;
    private CharSequence negativeButtonText;
    private CharSequence dialogMessage;
    private int dialogLayoutRes;
    private BitmapDrawable dialogIcon;
    private int whichButtonClicked = DialogInterface.BUTTON_NEGATIVE;

    public static CustomPreferenceDialogFragment newInstance(String preferenceKey) {
        CustomPreferenceDialogFragment fragment = new CustomPreferenceDialogFragment();
        Bundle args = new Bundle(1);
        args.putString(ARG_PREF, preferenceKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceFragmentCompat host = (PreferenceFragmentCompat) getTargetFragment();
        if (host == null) {
            throw new IllegalStateException("Target fragment missing");
        }
        String preferenceKey = requireArguments().getString(ARG_PREF);
        if (savedInstanceState == null) {
            preference = host.findPreference(preferenceKey);
            if (preference == null) {
                throw new IllegalStateException("Preference not found: " + preferenceKey);
            }
            dialogTitle = preference.getDialogTitle();
            positiveButtonText = preference.getPositiveButtonText();
            negativeButtonText = preference.getNegativeButtonText();
            dialogMessage = preference.getDialogMessage();
            dialogLayoutRes = preference.getDialogLayoutResource();
            Drawable icon = preference.getDialogIcon();
            if (icon == null || icon instanceof BitmapDrawable) {
                dialogIcon = (BitmapDrawable) icon;
            } else {
                Bitmap bitmap = Bitmap.createBitmap(
                        icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                icon.draw(canvas);
                dialogIcon = new BitmapDrawable(getResources(), bitmap);
            }
        } else {
            dialogTitle = savedInstanceState.getCharSequence(STATE_TITLE);
            positiveButtonText = savedInstanceState.getCharSequence(STATE_POSITIVE);
            negativeButtonText = savedInstanceState.getCharSequence(STATE_NEGATIVE);
            dialogMessage = savedInstanceState.getCharSequence(STATE_MESSAGE);
            dialogLayoutRes = savedInstanceState.getInt(STATE_LAYOUT, 0);
            Bitmap bitmap = savedInstanceState.getParcelable(STATE_ICON);
            if (bitmap != null) {
                dialogIcon = new BitmapDrawable(getResources(), bitmap);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(STATE_TITLE, dialogTitle);
        outState.putCharSequence(STATE_POSITIVE, positiveButtonText);
        outState.putCharSequence(STATE_NEGATIVE, negativeButtonText);
        outState.putCharSequence(STATE_MESSAGE, dialogMessage);
        outState.putInt(STATE_LAYOUT, dialogLayoutRes);
        if (dialogIcon != null) {
            outState.putParcelable(STATE_ICON, dialogIcon.getBitmap());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        whichButtonClicked = DialogInterface.BUTTON_NEGATIVE;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(dialogTitle)
                .setIcon(dialogIcon)
                .setPositiveButton(positiveButtonText, this)
                .setNegativeButton(negativeButtonText, this);
        View contentView = onCreateDialogView(requireContext());
        if (contentView != null) {
            onBindDialogView(contentView);
            builder.setView(contentView);
        } else {
            builder.setMessage(dialogMessage);
        }
        AlertDialog dialog = builder.create();
        CustomDialogPreference custom = preference();
        if (custom != null) {
            custom.attachActiveDialog(dialog);
        }
        return dialog;
    }

    @Nullable
    protected View onCreateDialogView(@NonNull Context context) {
        if (dialogLayoutRes == 0) {
            return null;
        }
        return getLayoutInflater().inflate(dialogLayoutRes, (ViewGroup) null);
    }

    protected void onBindDialogView(@NonNull View view) {
        View messageView = view.findViewById(android.R.id.message);
        if (messageView != null) {
            int visibility = View.GONE;
            if (!TextUtils.isEmpty(dialogMessage)) {
                if (messageView instanceof TextView) {
                    ((TextView) messageView).setText(dialogMessage);
                }
                visibility = View.VISIBLE;
            }
            if (messageView.getVisibility() != visibility) {
                messageView.setVisibility(visibility);
            }
        }
        CustomDialogPreference custom = preference();
        if (custom != null) {
            custom.onBindDialogView(view);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        CustomDialogPreference custom = preference();
        Dialog dialog = getDialog();
        if (custom != null && dialog instanceof AlertDialog) {
            custom.attachActiveDialog((AlertDialog) dialog);
            custom.onDialogShown((AlertDialog) dialog);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        whichButtonClicked = which;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        boolean positive = whichButtonClicked == DialogInterface.BUTTON_POSITIVE;
        CustomDialogPreference custom = preference();
        if (custom != null) {
            custom.onDialogClosed(positive);
            custom.attachActiveDialog(null);
        }
    }

    @Nullable
    private CustomDialogPreference preference() {
        if (preference instanceof CustomDialogPreference) {
            return (CustomDialogPreference) preference;
        }
        PreferenceFragmentCompat host = (PreferenceFragmentCompat) getTargetFragment();
        if (host == null) {
            return null;
        }
        String preferenceKey = requireArguments().getString(ARG_PREF);
        preference = host.findPreference(preferenceKey);
        return preference instanceof CustomDialogPreference
                ? (CustomDialogPreference) preference
                : null;
    }
}
