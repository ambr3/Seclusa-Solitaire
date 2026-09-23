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

package de.tobiasbielefeld.solitaire.ui.statistics;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.material.button.MaterialButton;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;
import de.tobiasbielefeld.solitaire.dialogs.DialogHighScoreDelete;

import static de.tobiasbielefeld.solitaire.SharedData.*;

public class StatisticsActivity extends CustomAppCompatActivity {

    private HideWinPercentage callback;

    @Override
    protected int getBaseThemeRes() {
        return R.style.AppThemeDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_statistics);

        //floating rounded card over the game, like the Settings screen
        float width = getResources().getDisplayMetrics().widthPixels;
        float height = getResources().getDisplayMetrics().heightPixels;
        getWindow().setLayout((int) (width * 0.9f), (int) (height * 0.8f));

        Toolbar toolbar = findViewById(R.id.statistics_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        PagerSlidingTabStrip tabs = findViewById(R.id.tabs);
        tabs.setAllCaps(false);
        tabs.setShouldExpand(true);
        tabs.setIndicatorColor(resolveThemeColor(R.attr.colorPrimary));
        tabs.setUnderlineColor(resolveThemeColor(R.attr.colorSurfaceVariant));
        tabs.setDividerColor(0x00000000);
        tabs.setTextColor(resolveThemeColor(R.attr.colorOnSurface));
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18,
                getResources().getDisplayMetrics()));

        MaterialButton deleteAllButton = findViewById(R.id.item_delete_all);
        deleteAllButton.setOnClickListener(v -> {
            DialogFragment deleteDialog = new DialogHighScoreDelete();
            deleteDialog.show(getSupportFragmentManager(), "high_score_delete");
        });

        MaterialButton hideWinButton = findViewById(R.id.item_hide_win);
        hideWinButton.setOnClickListener(v -> {
            boolean checked = !prefs.getSavedStatisticsHideWinPercentage();

            prefs.saveStatisticsHideWinPercentage(checked);
            updateHideWinButtonState(hideWinButton);
            if (callback != null) {
                callback.sendNewState(checked);
            }
        });
        updateHideWinButtonState(hideWinButton);

        ViewPager pager = findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager(), this);

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }

    /**
     * Resolves a theme attribute (such as an M3 color role) to its actual color value.
     */
    private int resolveThemeColor(int attributeId) {
        TypedValue typedValue = new TypedValue();

        getTheme().resolveAttribute(attributeId, typedValue, true);
        return typedValue.data;
    }

    /**
     * Syncs the visible "show/hide win percentage" button with the current preference.
     */
    private void updateHideWinButtonState(MaterialButton button) {
        if (prefs.getSavedStatisticsHideWinPercentage()) {
            button.setText(R.string.statistics_show_win_percentage);
        } else {
            button.setText(R.string.statistics_hide_win_percentage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return true;
    }

    public void setCallback(HideWinPercentage callback) {
        this.callback = callback;
    }

    public interface HideWinPercentage {
        void sendNewState(boolean state);
    }

    /**
     * deletes the data, reloads the activity
     */
    public void deleteHighScores() {
        scores.deleteScores();
        gameLogic.deleteStatistics();
        currentGame.deleteAdditionalStatisticsData();
        showToast(getString(R.string.statistics_button_deleted_all_entries), this);

        finish();
        startActivity(getIntent());
    }
}
