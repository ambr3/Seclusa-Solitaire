package de.tobiasbielefeld.solitaire.dialogs;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.tobiasbielefeld.solitaire.R;

/**
 * Builds Material dialogs whose choices are preference-style outline pill buttons.
 */
final class OptionPillDialog {

    interface OptionClick {
        void onOption(int index);
    }

    private OptionPillDialog() {
    }

    static AlertDialog create(Context context,
                              @Nullable CharSequence title,
                              @Nullable View header,
                              @ArrayRes int optionLabels,
                              OptionClick onOption) {
        View root = LayoutInflater.from(context).inflate(R.layout.dialog_option_pills, null, false);
        TextView titleView = root.findViewById(R.id.dialog_option_title);
        ViewGroup headerSlot = root.findViewById(R.id.dialog_option_header);
        LinearLayout buttons = root.findViewById(R.id.dialog_option_buttons);
        MaterialButton cancel = root.findViewById(R.id.dialog_option_cancel);

        if (title != null && title.length() > 0) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        }

        if (header != null) {
            // Header view already includes its own title (e.g. won dialog).
            titleView.setVisibility(View.GONE);
            headerSlot.setVisibility(View.VISIBLE);
            if (header.getParent() instanceof ViewGroup) {
                ((ViewGroup) header.getParent()).removeView(header);
            }
            headerSlot.addView(header, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(root)
                .create();

        TypedArray labels = context.getResources().obtainTypedArray(optionLabels);
        try {
            float density = context.getResources().getDisplayMetrics().density;
            for (int i = 0; i < labels.length(); i++) {
                final int index = i;
                MaterialButton pill = new MaterialButton(context, null,
                        com.google.android.material.R.attr.materialButtonOutlinedStyle);
                pill.setText(labels.getText(i));
                pill.setAllCaps(false);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                if (i > 0) {
                    lp.topMargin = (int) (8 * density);
                }
                buttons.addView(pill, lp);
                pill.setOnClickListener(v -> {
                    dialog.dismiss();
                    onOption.onOption(index);
                });
            }
        } finally {
            labels.recycle();
        }

        cancel.setOnClickListener(v -> dialog.dismiss());
        return dialog;
    }

    static AlertDialog create(Context context,
                              @StringRes int titleRes,
                              @ArrayRes int optionLabels,
                              OptionClick onOption) {
        return create(context, context.getString(titleRes), null, optionLabels, onOption);
    }
}
