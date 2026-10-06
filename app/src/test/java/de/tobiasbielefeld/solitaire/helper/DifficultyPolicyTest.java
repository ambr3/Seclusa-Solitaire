package de.tobiasbielefeld.solitaire.helper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DifficultyPolicyTest {

    @Test
    public void easyAndMediumEnableEnsureMovability() {
        assertTrue(DifficultyPolicy.isEnsureMovabilityEnabled(DifficultyPolicy.EASY, false));
        assertTrue(DifficultyPolicy.isEnsureMovabilityEnabled(DifficultyPolicy.MEDIUM, false));
    }

    @Test
    public void hardDisablesEnsureMovability() {
        assertFalse(DifficultyPolicy.isEnsureMovabilityEnabled(DifficultyPolicy.HARD, true));
        assertFalse(DifficultyPolicy.isEnsureMovabilityEnabled(DifficultyPolicy.HARD, false));
    }

    @Test
    public void customDifficultyUsesExpertToggle() {
        assertTrue(DifficultyPolicy.isEnsureMovabilityEnabled("custom", true));
        assertFalse(DifficultyPolicy.isEnsureMovabilityEnabled("custom", false));
    }

    @Test
    public void easyUsesWinnableMoveTarget() {
        assertEquals(DifficultyPolicy.WINNABLE_MOVES,
                DifficultyPolicy.minMovesForDifficulty(DifficultyPolicy.EASY, 10));
    }

    @Test
    public void mediumUsesAtLeastFortyMoves() {
        assertEquals(40, DifficultyPolicy.minMovesForDifficulty(DifficultyPolicy.MEDIUM, 10));
        assertEquals(45, DifficultyPolicy.minMovesForDifficulty(DifficultyPolicy.MEDIUM, 45));
    }

    @Test
    public void hardAndCustomUseExpertMinMoves() {
        assertEquals(10, DifficultyPolicy.minMovesForDifficulty(DifficultyPolicy.HARD, 10));
        assertEquals(12, DifficultyPolicy.minMovesForDifficulty("custom", 12));
    }

    @Test
    public void easyRequiresWinAndOpeningMoves() {
        assertTrue(DifficultyPolicy.requiresWin(DifficultyPolicy.EASY));
        assertEquals(3, DifficultyPolicy.minOpeningMoves(DifficultyPolicy.EASY));
        assertFalse(DifficultyPolicy.isDealAcceptable(DifficultyPolicy.EASY, 80, true, 2, 10));
        assertTrue(DifficultyPolicy.isDealAcceptable(DifficultyPolicy.EASY, 80, true, 3, 10));
        assertFalse(DifficultyPolicy.isDealAcceptable(DifficultyPolicy.EASY, 80, false, 5, 10));
    }

    @Test
    public void mediumAcceptsLongPlayOrWin() {
        assertFalse(DifficultyPolicy.isDealAcceptable(DifficultyPolicy.MEDIUM, 20, false, 0, 10));
        assertTrue(DifficultyPolicy.isDealAcceptable(DifficultyPolicy.MEDIUM, 40, false, 0, 10));
        assertTrue(DifficultyPolicy.isDealAcceptable(DifficultyPolicy.MEDIUM, 10, true, 0, 10));
    }

    @Test
    public void dealScorePrefersWins() {
        assertTrue(DifficultyPolicy.dealScore(true, 10, 0)
                > DifficultyPolicy.dealScore(false, 200, 52));
        assertTrue(DifficultyPolicy.dealScore(false, 30, 5)
                > DifficultyPolicy.dealScore(false, 30, 0));
    }
}
