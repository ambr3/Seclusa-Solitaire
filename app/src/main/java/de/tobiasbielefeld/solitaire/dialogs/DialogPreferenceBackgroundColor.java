/*
 * Copyright (C) 2016  Tobias Bielefeld
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you want to contact me, send me an e-mail at tobias.bielefeld@gmail.com
 */

package de.tobiasbielefeld.solitaire.dialogs;

import androidx.appcompat.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;
import yuku.ambilwarna.AmbilWarnaDialog;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Dialog for changing the background color. It uses a custom layout, so I can dynamically update
 * the widget icon of the preference. The user can choose between 6 pre defined colors or set a custom
 * color. The custom color chooser uses this library: https://github.com/yukuku/ambilwarna
 * <p>
 * To distinguish between the pre defined and custom colors, I use another entry in the sharedPref.
 * I also planned to add a "Add background from gallery" option, but it would require the
 * permission to the external storage, and i wanted my app to use no permissions.
 */

public class DialogPreferenceBackgroundColor extends CustomDialogPreference implements View.OnClickListener {

    int backgroundType;
    int backgroundValue;
    int savedCustomColor;
    private ArrayList<LinearLayout> linearLayouts;
    private Context context;
    private ImageView image;

    public DialogPreferenceBackgroundColor(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_background_color);
        setDialogIcon(null);
        this.context = context;
    }

    @Override
    protected void onBindDialogView(View view) {

        backgroundType = prefs.getSavedBackgroundColorType();
        backgroundValue = prefs.getSavedBackgroundColor();
        savedCustomColor = prefs.getSavedBackgroundCustomColor();

        linearLayouts = new ArrayList<>();
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorBlue));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorGreen));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorRed));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorYellow));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorOrange));
        linearLayouts.add(view.findViewById(R.id.dialogBackgroundColorPurple));

        for (LinearLayout linearLayout : linearLayouts) {
            linearLayout.setOnClickListener(this);
        }
    }


    @SuppressWarnings("SuspiciousMethodCalls")
    public void onClick(View view) {
        if (view == ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)) {
            AmbilWarnaDialog dialog = new AmbilWarnaDialog(context, savedCustomColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    backgroundType = 2;
                    backgroundValue = savedCustomColor = color;

                    prefs.saveBackgroundColorType(backgroundType);
                    prefs.saveBackgroundCustomColor(backgroundValue);
                    updateSummary();
                    getDialog().dismiss();
                }

                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    // cancel was selected by the user
                }
            });
            dialog.show();
        } else if (view == ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE)) {
            getDialog().dismiss();
        } else {
            backgroundValue = linearLayouts.indexOf(view) + 1;
            backgroundType = 1;

            prefs.saveBackgroundColorType(backgroundType);
            prefs.saveBackgroundColor(backgroundValue);

            updateSummary();
            getDialog().dismiss();
        }
    }

    @Override
    protected void onDialogShown(AlertDialog dialog) {
        super.onDialogShown(dialog);

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(this);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(androidx.preference.PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        image = holder.itemView.findViewById(R.id.widget_layout_color_imageView);
        applyWidget();
    }

    /**
     * Gets the saved data and updates the summary according to it
     */
    public void updateSummary() {
        if (prefs.getSavedBackgroundColorType() == 1) {
            int stringID;
            switch (prefs.getSavedBackgroundColor()) {
                case 1:
                default:
                    stringID = R.string.blue;
                    break;
                case 2:
                    stringID = R.string.green;
                    break;
                case 3:
                    stringID = R.string.red;
                    break;
                case 4:
                    stringID = R.string.yellow;
                    break;
                case 5:
                    stringID = R.string.orange;
                    break;
                case 6:
                    stringID = R.string.purple;
                    break;
            }
            setSummary(context.getString(stringID));
        } else {
            int customColor = prefs.getSavedBackgroundCustomColor();
            setSummary(String.format("#%06X", (0xFFFFFF & customColor)));
        }
        applyWidget();
    }

    private void applyWidget() {
        if (image == null) {
            return;
        }
        if (prefs.getSavedBackgroundColorType() == 1) {
            int drawableID;
            switch (prefs.getSavedBackgroundColor()) {
                case 1:
                default:
                    drawableID = R.drawable.background_color_blue;
                    break;
                case 2:
                    drawableID = R.drawable.background_color_green;
                    break;
                case 3:
                    drawableID = R.drawable.background_color_red;
                    break;
                case 4:
                    drawableID = R.drawable.background_color_yellow;
                    break;
                case 5:
                    drawableID = R.drawable.background_color_orange;
                    break;
                case 6:
                    drawableID = R.drawable.background_color_purple;
                    break;
            }
            image.setBackgroundColor(0);
            image.setImageResource(drawableID);
        } else {
            image.setImageResource(0);
            image.setBackgroundColor(prefs.getSavedBackgroundCustomColor());
        }
    }
}
