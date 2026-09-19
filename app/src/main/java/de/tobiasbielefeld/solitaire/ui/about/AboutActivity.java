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

package de.tobiasbielefeld.solitaire.ui.about;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import de.tobiasbielefeld.solitaire.R;
import de.tobiasbielefeld.solitaire.classes.CustomAppCompatActivity;
import de.tobiasbielefeld.solitaire.helper.EdgeToEdge;

/**
 * The About activity simply shows the information fragment. The license fragment
 * can be opened from there, no tabs needed.
 */

public class AboutActivity extends CustomAppCompatActivity {

    @Override
    protected int getBaseThemeRes() {
        return R.style.AppThemeActionBar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_about);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.about_fragment_container, new InformationFragment())
                .commit();

        EdgeToEdge.applyContentInsets(this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //only menu item is the back button in the action bar so finish this activity
        finish();
        return true;
    }
}