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
 * <p>
 * Tuned from a 20k-deal Klondike replay of the greedy hint bot:
 * median depth ~41 (draw-1) / ~25 (draw-3); old Medium (≥20) passed ~96% of deals.
 */
public final class DifficultyPolicy {

    public static final String EASY = "easy";
    public static final String MEDIUM = "medium";
    public static final String HARD = "hard";

    /** Sentinel: Easy keeps searching until the hint bot wins (never reaches this count). */
    public static final int WINNABLE_MOVES = 500;

    /**
     * Medium needs a long greedy playthrough (around draw-1 median), not a trivial opener.
     * Wins also count.
     */
    public static final int MEDIUM_MIN_MOVES = 40;

    /** Easy also wants a few distinct plays available before anything is moved. */
    public static final int EASY_MIN_OPENING_MOVES = 3;

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
     * Easy aims for a hint-bot win; Medium uses at least {@link #MEDIUM_MIN_MOVES};
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

    public static boolean requiresWin(String difficulty) {
        return EASY.equals(difficulty);
    }

    public static int minOpeningMoves(String difficulty) {
        if (EASY.equals(difficulty)) {
            return EASY_MIN_OPENING_MOVES;
        }
        return 0;
    }

    /**
     * Whether a finished greedy playthrough is good enough to deal to the player.
     *
     * @param movesMade     hint-bot moves before getting stuck (or winning)
     * @param won           {@code winTest()} succeeded
     * @param openingMoves  distinct hint moves available on the fresh deal
     */
    public static boolean isDealAcceptable(String difficulty, int movesMade, boolean won,
                                           int openingMoves, int expertMinMoves) {
        if (openingMoves < minOpeningMoves(difficulty)) {
            return false;
        }
        if (requiresWin(difficulty)) {
            return won;
        }
        int minMoves = minMovesForDifficulty(difficulty, expertMinMoves);
        return won || movesMade >= minMoves;
    }

    /**
     * Score for best-deal fallback when the deal budget is exhausted.
     * Higher is better; wins dominate depth.
     */
    public static int dealScore(boolean won, int movesMade, int foundationCards) {
        if (won) {
            return 1_000_000 + movesMade;
        }
        return movesMade * 10 + foundationCards;
    }
}
