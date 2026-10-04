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
    public void mediumUsesAtLeastTwentyMoves() {
        assertEquals(20, DifficultyPolicy.minMovesForDifficulty(DifficultyPolicy.MEDIUM, 10));
        assertEquals(25, DifficultyPolicy.minMovesForDifficulty(DifficultyPolicy.MEDIUM, 25));
    }

    @Test
    public void hardAndCustomUseExpertMinMoves() {
        assertEquals(10, DifficultyPolicy.minMovesForDifficulty(DifficultyPolicy.HARD, 10));
        assertEquals(12, DifficultyPolicy.minMovesForDifficulty("custom", 12));
    }
}
