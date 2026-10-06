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
import android.os.Bundle;

import androidx.annotation.NonNull;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogFragment;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Dialog for starting a new game. Asks whether the same cards should be dealt again (the default
 * is to deal a fresh, randomly shuffled game).
 */

public class DialogStartNewGame extends CustomDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return applyFlags(OptionPillDialog.create(
                requireActivity(),
                R.string.dialog_start_new_game_title,
                R.array.new_game_menu,
                which -> {
                    switch (which) {
                        case 0:
                            gameLogic.newGame();
                            break;
                        case 1:
                            gameLogic.redeal();
                            break;
                    }
                }));
    }
}
