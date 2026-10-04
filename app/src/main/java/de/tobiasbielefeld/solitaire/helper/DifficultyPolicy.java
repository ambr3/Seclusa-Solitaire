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
 * Pure difficulty → deal-filter rules (no Android deps; unit-tested).
 */
public final class DifficultyPolicy {

    public static final String EASY = "easy";
    public static final String MEDIUM = "medium";
    public static final String HARD = "hard";
    public static final int WINNABLE_MOVES = 500;
    public static final int MEDIUM_MIN_MOVES = 20;

    private DifficultyPolicy() {
    }

    /**
     * Easy/Medium filter deals; Hard is fully random; other values use the expert toggle.
     */
    public static boolean isEnsureMovabilityEnabled(String difficulty, boolean expertEnsureMovability) {
        if (HARD.equals(difficulty)) {
            return false;
        }
        if (EASY.equals(difficulty) || MEDIUM.equals(difficulty)) {
            return true;
        }
        return expertEnsureMovability;
    }

    /**
     * Easy aims for a near-winnable start; Medium uses at least {@link #MEDIUM_MIN_MOVES};
     * otherwise the expert min-moves setting.
     */
    public static int minMovesForDifficulty(String difficulty, int expertMinMoves) {
        if (EASY.equals(difficulty)) {
            return WINNABLE_MOVES;
        }
        if (MEDIUM.equals(difficulty)) {
            return Math.max(MEDIUM_MIN_MOVES, expertMinMoves);
        }
        return expertMinMoves;
    }
}
