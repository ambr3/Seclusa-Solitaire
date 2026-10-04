package de.tobiasbielefeld.solitaire.helper;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeckShuffleTest {

    @Test
    public void breaksFourSameColourRunWhenLaterDifferentColourExists() {
        int[] colors = {0, 0, 0, 0, 1, 1};
        assertTrue(DeckShuffle.hasColorClumpOfFour(colors));

        DeckShuffle.breakColorClumps(colors);

        assertFalse(DeckShuffle.hasColorClumpOfFour(colors));
        assertArrayEquals(new int[]{0, 0, 0, 1, 0, 1}, colors);
    }

    @Test
    public void leavesDeckAloneWhenNoAlternateColourExists() {
        int[] colors = {1, 1, 1, 1, 1};
        DeckShuffle.breakColorClumps(colors);
        assertArrayEquals(new int[]{1, 1, 1, 1, 1}, colors);
        assertTrue(DeckShuffle.hasColorClumpOfFour(colors));
    }

    @Test
    public void ignoresRunsShorterThanFour() {
        int[] colors = {0, 0, 0, 1, 1, 1, 0};
        int[] original = colors.clone();
        DeckShuffle.breakColorClumps(colors);
        assertArrayEquals(original, colors);
        assertFalse(DeckShuffle.hasColorClumpOfFour(colors));
    }

    @Test
    public void breaksMultipleClumps() {
        int[] colors = {0, 0, 0, 0, 1, 1, 1, 1, 0};
        DeckShuffle.breakColorClumps(colors);
        assertFalse(DeckShuffle.hasColorClumpOfFour(colors));
    }
}
