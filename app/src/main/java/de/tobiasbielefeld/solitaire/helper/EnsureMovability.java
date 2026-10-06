package de.tobiasbielefeld.solitaire.helper;

import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;

import de.tobiasbielefeld.solitaire.SharedData;
import de.tobiasbielefeld.solitaire.classes.Card;
import de.tobiasbielefeld.solitaire.classes.CardAndStack;
import de.tobiasbielefeld.solitaire.classes.Stack;
import de.tobiasbielefeld.solitaire.dialogs.DialogEnsureMovability;
import de.tobiasbielefeld.solitaire.games.Pyramid;

import static de.tobiasbielefeld.solitaire.SharedData.*;

/**
 * Filters new deals according to {@link DifficultyPolicy}.
 * Uses {@link de.tobiasbielefeld.solitaire.games.Game#hintTest()} to play greedily.
 * If not enough progress is found, a new game is dealt and the test restarts.
 * <p>
 * Everything happens inside the async task, the user only sees a spinning wait wheel. While the tests
 * run, the stopUiUpdates variable in SharedData is set to true, so cards won't move visibly, but
 * in the background they are assigned to other stacks and so on.
 * <p>
 * IMPORTANT: The Game.hintTest() does NOT return every possible movement! For example in SimpleSimon:
 * If a Hearts 9 lies on a Clubs 10 and could be moved to a Diamonds 10, it won't be shown. If it
 * could be moved to a Hearts 10, this would be shown. This decision was made to not show redundant
 * movements.
 */

public class EnsureMovability {

    FindMoves findMoves;
    DialogEnsureMovability dialog;

    private boolean paused = false;

    private int minPossibleMovements;

    private ShowDialog showDialog;

    public void setShowDialog(ShowDialog callback) {
        showDialog = callback;
    }

    public void start(int minPossibleMovements) {
        this.minPossibleMovements = minPossibleMovements;

        dialog = new DialogEnsureMovability();
        showDialog.show(dialog);

        findMoves = new FindMoves();
        findMoves.execute();
    }

    public void stop() {
        dialog.dismiss();
        findMoves.cancel(true);
    }

    public boolean isRunning() {
        return SharedData.stopUiUpdates;
    }

    public void pause() {
        if (isRunning()) {
            paused = true;
            dialog.dismiss();
            findMoves.interrupt();
        }
    }

    public void saveInstanceState(Bundle bundle) {
        if (isRunning() || paused) {
            bundle.putBoolean("BUNDLE_ENSURE_MOVABILITY", true);
        }
    }

    public void loadInstanceState(Bundle bundle) {
        if (bundle.containsKey("BUNDLE_ENSURE_MOVABILITY")) {
            gameLogic.newGame();
        }
    }

    public void resume() {
        if (paused) {
            paused = false;
            gameLogic.load(true);
            gameLogic.newGame();
        }
    }

    private void dismissDialog() {
        dialog.dismiss();
    }

    private static class FindMoves extends AsyncTask<Object, Void, Boolean> {
        private int counter = 0;
        private int dealCounter = 0;
        private int openingMoves = 0;
        private static final int MAX_DEALS = 2000;
        private boolean mainStackAlreadyFlipped = false;
        private boolean isInterrupted = false;

        private int[] currentDealOrder;
        private int[] bestDealOrder;
        private int bestScore = -1;

        private String difficulty;
        private int expertMinMoves;

        @Override
        protected Boolean doInBackground(Object... objects) {
            difficulty = prefs.getSavedDifficulty();
            expertMinMoves = prefs.getSavedEnsureMovabilityMinMoves();
            // minPossibleMovements still set by start() for custom/expert paths
            int minPossibleMovements = ensureMovability.minPossibleMovements;

            try {
                beginDealTracking();

                while (true) {
                    if (isCancelled()) {
                        return false;
                    }

                    if (dealCounter > MAX_DEALS) {
                        restoreBestDeal();
                        return true;
                    }

                    boolean won = currentGame.winTest();
                    if (DifficultyPolicy.isDealAcceptable(
                            difficulty, counter, won, openingMoves, expertMinMoves)) {
                        return true;
                    }
                    // Easy can win with a poor opening — keep it as fallback, then redeal
                    if (won) {
                        nextTry(true);
                        continue;
                    }

                    CardAndStack cardAndStack = currentGame.hintTest();

                    if (cardAndStack != null) {

                        Stack destination = cardAndStack.getStack();
                        Card card = cardAndStack.getCard();
                        Stack origin = card.getStack();

                        int size = origin.getSize() - card.getIndexOnStack();

                        ArrayList<Card> cardsToMove = new ArrayList<>(size);

                        for (int l = card.getIndexOnStack(); l < origin.getSize(); l++) {
                            cardsToMove.add(origin.getCard(l));
                        }

                        //TODO manage this in another way
                        if (currentGame instanceof Pyramid) {
                            currentGame.cardTest(destination, card);
                        }

                        moveToStack(cardsToMove, destination);

                        if (origin.getSize() > 0 && origin.getId() <= currentGame.getLastTableauId() && !origin.getTopCard().isUp()) {
                            origin.getTopCard().flip();
                        }

                        currentGame.testAfterMove();

                        mainStackAlreadyFlipped = false;
                        counter++;

                        // Fast-accept Medium/custom once the move floor is reached
                        if (counter >= minPossibleMovements
                                && DifficultyPolicy.isDealAcceptable(
                                difficulty, counter, false, openingMoves, expertMinMoves)) {
                            return true;
                        }
                    } else if (currentGame.hasMainStack()) {
                        int result = currentGame.mainStackTouch();

                        if (result == 0 || (result == 2 && mainStackAlreadyFlipped)) {
                            nextTry(false);
                        } else if (result == 2) {
                            mainStackAlreadyFlipped = true;
                        }

                    } else {
                        nextTry(false);
                    }
                }
            } catch (Exception e) {
                stopUiUpdates = false;
                restoreBestDeal();
                return false;
            }
        }

        private void beginDealTracking() {
            currentDealOrder = gameLogic.snapshotDealOrder();
            openingMoves = countOpeningMoves();
        }

        private int countOpeningMoves() {
            ArrayList<Card> visited = new ArrayList<>();
            int n = 0;
            CardAndStack next;
            while ((next = currentGame.hintTest(visited)) != null) {
                visited.add(next.getCard());
                n++;
                if (n > 40) {
                    break;
                }
            }
            return n;
        }

        private int countFoundationCards() {
            if (!currentGame.hasFoundationStacks()) {
                return 0;
            }
            int n = 0;
            try {
                for (int i = currentGame.getLastTableauId() + 1; i <= currentGame.getLastFoundationID(); i++) {
                    n += stacks[i].getSize();
                }
            } catch (Exception ignored) {
                return n;
            }
            return n;
        }

        private void considerBestDeal(boolean won) {
            int score = DifficultyPolicy.dealScore(won, counter, countFoundationCards());
            if (score > bestScore && currentDealOrder != null) {
                bestScore = score;
                bestDealOrder = currentDealOrder.clone();
            }
        }

        private void restoreBestDeal() {
            if (bestDealOrder != null) {
                gameLogic.applyDealOrder(bestDealOrder);
            }
        }

        private void nextTry(boolean won) {
            if (isCancelled()) {
                return;
            }

            considerBestDeal(won);
            counter = 0;
            dealCounter++;
            mainStackAlreadyFlipped = false;
            gameLogic.newGameForEnsureMovability();
            beginDealTracking();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            stopUiUpdates = false;

            if (!isInterrupted) {
                try {
                    ensureMovability.dismissDialog();
                } catch (IllegalStateException ignored) {
                    //Meh
                }

                gameLogic.redeal();
            }
        }

        @Override
        protected void onCancelled() {
            //will be called after the user presses the "cancel" button in the dialog and after
            //executing doInBackground() the last time

            stopUiUpdates = false;

            if (!isInterrupted) {
                try {
                    ensureMovability.dismissDialog();
                } catch (IllegalStateException ignored) {
                }
                gameLogic.redeal();
            }
        }

        public void interrupt() {
            isInterrupted = true;
            cancel(true);
        }
    }

    public interface ShowDialog {
        void show(DialogEnsureMovability dialog);
    }
}
