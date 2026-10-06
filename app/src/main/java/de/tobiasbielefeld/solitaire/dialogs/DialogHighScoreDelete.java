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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomDialogFragment;
import de.tobiasbielefeld.solitaire.ui.statistics.StatisticsActivity;

/**
 * Dialog for deleting all high scores
 */

public class DialogHighScoreDelete extends CustomDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setMessage(R.string.statistics_button_delete_text)
                .setPositiveButton(R.string.game_confirm, (dialog, id) -> {
                    StatisticsActivity statistics = (StatisticsActivity) getActivity();
                    if (statistics != null) {
                        statistics.deleteHighScores();
                    }
                })
                .setNegativeButton(R.string.game_cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });

        return applyFlags(builder.create());
    }
}
