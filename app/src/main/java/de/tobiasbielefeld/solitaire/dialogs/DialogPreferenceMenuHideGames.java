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
 */

package de.tobiasbielefeld.solitaire.dialogs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogPreference;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Dialog for hiding games in the main menu.
 * It is NOT a multiSelection list, because it was buggy on tested Android 6 phones. So I
 * just use a linearLayout with a button and a textView for each game
 */

public class DialogPreferenceMenuHideGames extends CustomDialogPreference implements View.OnClickListener {

    private ArrayList<LinearLayout> linearLayouts = new ArrayList<>();
    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private ArrayList<Integer> gameOrder;

    public DialogPreferenceMenuHideGames(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialog_menu_show_games);
        setDialogIcon(null);
    }

    @Override
    protected void onBindDialogView(View view) {
        LinearLayout container = view.findViewById(R.id.layoutContainer);

        linearLayouts.clear();
        checkBoxes.clear();

        ArrayList<Integer> results = lg.getMenuShownList();
        gameOrder = lg.getOrderedGameList();

        ArrayList<String> sortedGameList = lg.getOrderedGameNameList(getContext().getResources());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0; i < lg.getGameCount(); i++) {
            LinearLayout entry = (LinearLayout) inflater.inflate(R.layout.dialog_menu_hide_game_row, container, false);
            entry.setOnClickListener(this);

            CheckBox checkBox = entry.findViewById(R.id.dialog_hide_game_checkbox);
            int index = gameOrder.indexOf(i);
            checkBox.setChecked(results.get(index) == 1);

            TextView textView = entry.findViewById(R.id.dialog_hide_game_name);
            textView.setText(sortedGameList.get(i));

            checkBoxes.add(checkBox);
            linearLayouts.add(entry);

            container.addView(entry);
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void onClick(View view) {
        int index = linearLayouts.indexOf(view);
        boolean checked = checkBoxes.get(index).isChecked();
        checkBoxes.get(index).setChecked(!checked);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {

        if (positiveResult) {
            ArrayList<Integer> list = new ArrayList<>();

            for (int i = 0; i < lg.getGameCount(); i++) {
                int index = gameOrder.get(i);
                list.add(checkBoxes.get(index).isChecked() ? 1 : 0);
            }

            prefs.saveMenuGamesList(list);
        }
    }
}
