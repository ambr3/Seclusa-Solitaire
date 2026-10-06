/* Copyright (C) 2016  Tobias Bielefeld
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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogFragment;
import de.tobiasbielefeld.solitaire.ui.GameManager;
import de.tobiasbielefeld.solitaire.ui.manual.Manual;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * dialog to handle new games or returning to main menu( in that case, cancel the current activity)
 */

public class DialogInGameHelpMenu extends CustomDialogFragment {

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final GameManager gameManager = (GameManager) getActivity();
        return applyFlags(OptionPillDialog.create(
                requireActivity(),
                R.string.settings_support,
                R.array.help_menu,
                which -> {
                    switch (which) {
                        case 0:
                            if (!gameLogic.hasWon()) {
                                hint.start();
                            }
                            break;
                        case 1:
                            if (gameManager != null) {
                                Intent intent = new Intent(gameManager, Manual.class);
                                intent.putExtra(GAME, lg.getSharedPrefName());
                                startActivity(intent);
                            }
                            break;
                    }
                }));
    }
}
