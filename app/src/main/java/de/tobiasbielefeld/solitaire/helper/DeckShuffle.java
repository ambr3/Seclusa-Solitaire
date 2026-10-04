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

package de.tobiasbielefeld.solitaire.helper;

/**
 * Pure deal-shuffle helpers (no Android deps; unit-tested).
 */
public final class DeckShuffle {

    private DeckShuffle() {
    }

    public interface ColorAt {
        int get(int index);
    }

    public interface Swap {
        void swap(int i, int j);
    }

    /**
     * Breaks runs of 4+ same colour by swapping the 4th card with a later different colour.
     */
    public static void breakColorClumps(int length, ColorAt colorAt, Swap swap) {
        for (int i = 0; i < length - 3; i++) {
            int color = colorAt.get(i);
            if (colorAt.get(i + 1) != color
                    || colorAt.get(i + 2) != color
                    || colorAt.get(i + 3) != color) {
                continue;
            }
            for (int j = i + 4; j < length; j++) {
                if (colorAt.get(j) != color) {
                    swap.swap(i + 3, j);
                    break;
                }
            }
        }
    }

    /** Convenience for tests / int colour arrays. */
    public static void breakColorClumps(int[] colors) {
        breakColorClumps(colors.length, i -> colors[i], (a, b) -> {
            int tmp = colors[a];
            colors[a] = colors[b];
            colors[b] = tmp;
        });
    }

    /** True if any run of 4 identical colours remains. */
    public static boolean hasColorClumpOfFour(int[] colors) {
        for (int i = 0; i < colors.length - 3; i++) {
            int color = colors[i];
            if (colors[i + 1] == color && colors[i + 2] == color && colors[i + 3] == color) {
                return true;
            }
        }
        return false;
    }
}
