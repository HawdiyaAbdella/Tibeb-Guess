package com.tibebguess;

/**
 * GameManager
 *
 * Concrete implementation of {@link AbstractGameManager}. All UI specific
 * logic lives here while the base class encapsulates the shared mechanics. This
 * showcases inheritance and polymorphism.
 */
public class GameManager extends AbstractGameManager {
    private static final int INITIAL_HEARTS = 3;
    private static final int ROUND_TIME_SECONDS = 30;
    private static final int SCORE_PER_CORRECT = 10;

    private final GameController controller;

    public GameManager(GameController controller) {
        super(INITIAL_HEARTS, ROUND_TIME_SECONDS, SCORE_PER_CORRECT);
        this.controller = controller;
    }

    @Override
    protected void onRoundStarted(Proverb proverb) {
        controller.showProverbImage(proverb);
        controller.resetRoundUI();
        controller.updateUI();
    }

    @Override
    protected void onTimerUpdated(int secondsRemaining) {
        controller.updateTimer(secondsRemaining);
    }

    @Override
    protected void onStatusChanged(int score, int hearts) {
        controller.updateUI();
    }

    @Override
    protected void onProverbRevealed(Proverb proverb, String reason) {
        controller.revealAnswer(proverb, reason);
        controller.showFeedback(reason);
    }

    @Override
    protected void onAnswerEvaluated(boolean isCorrect, int score, int hearts) {
        if (isCorrect) {
            controller.showFeedback("Correct! +" + SCORE_PER_CORRECT + " points");
        } else {
            controller.showFeedback("Wrong! -1 heart");
        }
    }

    @Override
    protected void onGameFinished(int score, int hearts, int completedRounds, int totalRounds) {
        controller.showFinalResult(score, hearts, completedRounds, totalRounds);
    }

    @Override
    protected void onInitializationError(String message) {
        controller.showFeedback(message);
    }
}

