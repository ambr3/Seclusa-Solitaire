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

package de.tobiasbielefeld.solitaire.ui.manual;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.Toolbar;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Manual/How-to-play screen: shows the rules for the single games and an explanation of the in
 * game menu bar. A close (X) button on the toolbar returns to the calling screen.
 */

public class Manual extends CustomAppCompatActivity implements ManualGames.GamePageShown {

    private boolean gamePageShown = false;

    @Override
    protected int getBaseThemeRes() {
        return R.style.AppThemeDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        //floating rounded card over the game, like the Settings screen
        float width = getResources().getDisplayMetrics().widthPixels;
        float height = getResources().getDisplayMetrics().heightPixels;
        getWindow().setLayout((int) (width * 0.9f), (int) (height * 0.8f));

        Toolbar toolbar = findViewById(R.id.manual_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        if (getIntent() != null && getIntent().hasExtra(GAME)) {
            loadManualGames(getIntent().getStringExtra(GAME));
        } else {
            loadManualGames(null);
        }
    }

    @Override
    public void onBackPressed() {
        //if a game rule page is shown, return to the game selection grid instead of closing
        if (gamePageShown) {
            loadManualGames(null);
        } else {
            super.onBackPressed();
        }
    }

    private void loadManualGames(String gamePrefName) {
        Bundle args = new Bundle();
        if (gamePrefName != null) {
            args.putString(GAME, gamePrefName);
        }

        Fragment fragment = new ManualGames();
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void setGamePageShown(boolean value) {
        gamePageShown = value;
    }
}