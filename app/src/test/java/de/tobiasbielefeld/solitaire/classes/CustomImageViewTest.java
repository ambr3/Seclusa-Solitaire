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

package de.tobiasbielefeld.solitaire.classes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Pure JVM tests for the rounded-corner radius math of {@link CustomImageView}. The radius is
 * the only part of the card-outline logic that does not need the Android framework (no Views, no
 * Outline, no hardware acceleration tree), so it is tested directly with plain JUnit instead of
 * Robolectric.
 *
 * Run with: ./gradlew testDebugUnitTest
 */
public class CustomImageViewTest {

    private static final float DELTA = 0.001f;
    private static final float SCALE = 0.045f;

    @Test
    public void radius_is_4_5_percent_of_card_width() {
        // 246px wide card -> 246 * 0.045 = 11.07px corner radius
        assertEquals(11.07f, CustomImageView.calcRoundedCornerRadius(246), DELTA);
    }

    @Test
    public void radius_scale_holds_for_full_width() {
        // 1280px wide card -> exactly 57.6px
        assertEquals(1280 * SCALE, CustomImageView.calcRoundedCornerRadius(1280), DELTA);
    }

    @Test
    public void radius_never_smaller_than_one_pixel() {
        // Sub-1px widths are clamped up to 1f so the rounding is always visible, even on tiny views
        assertEquals(1f, CustomImageView.calcRoundedCornerRadius(0), DELTA);
        assertEquals(1f, CustomImageView.calcRoundedCornerRadius(10), DELTA);   // 10*0.045 = 0.45
        assertEquals(1f, CustomImageView.calcRoundedCornerRadius(22), DELTA);   // 22*0.045 = 0.99
    }

    @Test
    public void radius_is_proportional_to_width() {
        // Doubling the width doubles the radius (0.045 is a pure multiplier, no constant term)
        float small = CustomImageView.calcRoundedCornerRadius(100);
        float large = CustomImageView.calcRoundedCornerRadius(200);
        assertEquals(small * 2f, large, DELTA);
    }
}
