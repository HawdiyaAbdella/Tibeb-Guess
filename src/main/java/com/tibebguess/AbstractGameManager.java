package com.tibebguess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

/**
 * AbstractGameManager
 *
 * Provides the reusable, core game-loop logic (encapsulation) that can be
 * reused by different kinds of proverb games. Subclasses inherit the common
 * mechanics (inheritance) and override the template hooks below to customize
 * UI behaviour (polymorphism).
 */
public abstract class AbstractGameManager {
    private static final long NEXT_ROUND_DELAY_MS = 1500;

    private final int initialHearts;
    private final int roundTimeSeconds;
    private final int pointsPerCorrect;

    private int hearts;
    private int score;
    private int currentIndex;
    private int timeRemaining;
    private boolean gameActive;
    private Timer timer;
    private List<Proverb> allProverbs = new ArrayList<>();
    private List<Proverb> gameProverbs = new ArrayList<>();
    private Proverb.Difficulty selectedDifficulty;

    protected AbstractGameManager(int initialHearts, int roundTimeSeconds, int pointsPerCorrect) {
        this.initialHearts = initialHearts;
        this.roundTimeSeconds = roundTimeSeconds;
        this.pointsPerCorrect = pointsPerCorrect;
        this.selectedDifficulty = Proverb.Difficulty.MEDIUM; // default
        resetState();
    }

    /**
     * Template hook for subclasses to react when a new round begins.
     */
    protected abstract void onRoundStarted(Proverb proverb);

    /**
     * Template hook for subclasses to update their timer UI.
     */
    protected abstract void onTimerUpdated(int secondsRemaining);

    /**
     * Template hook for subclasses to react to score/heart changes.
     */
    protected abstract void onStatusChanged(int score, int hearts);

    /**
     * Template hook for subclasses to reveal the proverb text.
     */
    protected abstract void onProverbRevealed(Proverb proverb, String reason);

    /**
     * Template hook for subclasses to show answer feedback.
     */
    protected abstract void onAnswerEvaluated(boolean isCorrect, int score, int hearts);

    /**
     * Template hook for subclasses to show the final results.
     */
    protected abstract void onGameFinished(int score, int hearts, int completedRounds, int totalRounds);

    /**
     * Template hook for subclasses to report initialization failures.
     */
    protected abstract void onInitializationError(String message);

    /**
     * Initializes the game with the supplied proverbs.
     */
    public void initializeProverbs(List<Proverb> proverbList) {
        if (proverbList == null) {
            this.allProverbs = new ArrayList<>();
        } else {
            this.allProverbs = new ArrayList<>(proverbList);
        }
        prepareGameProverbs();
    }

    /**
     * Sets the selected difficulty level and prepares proverbs accordingly.
     */
    public void setDifficulty(Proverb.Difficulty difficulty) {
        this.selectedDifficulty = difficulty;
        prepareGameProverbs();
    }

    /**
     * Filters proverbs based on the selected difficulty and shuffles them for random play.
     * For null difficulty, randomly assigns images from all difficulty folders.
     */
    private void prepareGameProverbs() {
        List<Proverb> baseProverbs;
        if (selectedDifficulty == null) {
            // For random mode, create new proverbs with random images from all folders
            List<String> allImages = getAllImagesFromResources();
            baseProverbs = new ArrayList<>();
            for (Proverb original : allProverbs) {
                String randomImage = allImages.isEmpty() ? "/images/placeholder.jpg" : 
                    allImages.get(new java.util.Random().nextInt(allImages.size()));
                baseProverbs.add(new Proverb(original.getText(), randomImage, Proverb.Difficulty.MEDIUM));
            }
        } else {
            baseProverbs = allProverbs.stream()
                .filter(proverb -> proverb.getDifficulty() == selectedDifficulty)
                .collect(Collectors.toList());
        }
        this.gameProverbs = baseProverbs;
        // Shuffle for random order
        Collections.shuffle(gameProverbs);
    }
    
    /**
     * Gets all available images from all difficulty folders in resources.
     */
    private List<String> getAllImagesFromResources() {
        List<String> allImages = new ArrayList<>();
        try {
            // Scan each difficulty folder
            for (Proverb.Difficulty diff : Proverb.Difficulty.values()) {
                String dirPath = "/images/" + diff.name().toLowerCase() + "/";
                java.util.Enumeration<java.net.URL> resources = 
                    AbstractGameManager.class.getClassLoader().getResources("images/" + diff.name().toLowerCase());
                
                while (resources.hasMoreElements()) {
                    java.net.URL resource = resources.nextElement();
                    if (resource.getProtocol().equals("file")) {
                        java.io.File dir = new java.io.File(resource.getFile());
                        if (dir.exists() && dir.isDirectory()) {
                            java.io.File[] files = dir.listFiles((file, name) -> 
                                name.toLowerCase().endsWith(".jpg") || 
                                name.toLowerCase().endsWith(".png") || 
                                name.toLowerCase().endsWith(".jpeg"));
                            if (files != null) {
                                for (java.io.File file : files) {
                                    allImages.add(dirPath + file.getName());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error scanning images: " + e.getMessage());
            // Fallback to some default images
            allImages.add("/images/easy/image1.jpg");
            allImages.add("/images/medium/image1.jpg");
            allImages.add("/images/hard/image1.jpg");
        }
        return allImages;
    }

    /**
     * Starts the game if proverbs are available.
     */
    public void startGame() {
        if (gameProverbs == null || gameProverbs.isEmpty()) {
            onInitializationError("No proverbs available for selected difficulty level. Please choose a different level.");
            return;
        }

        resetState();
        gameActive = true;
        onStatusChanged(score, hearts);
        startRound();
    }

    /**
     * Begins a new round.
     */
    protected void startRound() {
        if (!gameActive) {
            return;
        }

        if (currentIndex >= gameProverbs.size()) {
            finishGame();
            return;
        }

        cancelTimer();
        timeRemaining = getRoundTimeForDifficulty();

        Proverb current = getCurrentProverb();
        onRoundStarted(current);
        onStatusChanged(score, hearts);
        onTimerUpdated(timeRemaining);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeRemaining--;
                onTimerUpdated(timeRemaining);

                if (timeRemaining <= 0) {
                    handleTimeUp();
                    this.cancel();
                }
            }
        }, 1000, 1000);
    }

    /**
     * Handles the event when the timer hits zero.
     */
    protected void handleTimeUp() {
        cancelTimer();
        Proverb current = getCurrentProverb();
        if (current != null) {
            onProverbRevealed(current, "Time's up!");
        }
    }

    /**
     * Handles user answers.
     */
    public void handleAnswer(boolean isCorrect) {
        cancelTimer();

        if (isCorrect) {
            score += getPointsForDifficulty();
        } else if (hearts > 0) {
            hearts--;
        }

        onStatusChanged(score, hearts);
        onAnswerEvaluated(isCorrect, score, hearts);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                nextProverb();
            }
        }, NEXT_ROUND_DELAY_MS);
    }

    /**
     * Moves to the next proverb or finishes the game.
     */
    public void nextProverb() {
        currentIndex++;
        if (!checkGameOver()) {
            startRound();
        }
    }

    /**
     * Cancels the active timer (used by the UI for early reveals).
     */
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Checks whether the game has ended.
     */
    protected boolean checkGameOver() {
        if (hearts <= 0 || currentIndex >= gameProverbs.size()) {
            finishGame();
            return true;
        }
        return false;
    }

    private void finishGame() {
        gameActive = false;
        cancelTimer();
        onGameFinished(score, hearts, Math.min(currentIndex, gameProverbs.size()), gameProverbs.size());
    }

    private void resetState() {
        hearts = initialHearts;
        score = 0;
        currentIndex = 0;
        timeRemaining = getRoundTimeForDifficulty();
        cancelTimer();
    }

    /**
     * Gets the round time based on difficulty level.
     */
    private int getRoundTimeForDifficulty() {
        switch (selectedDifficulty) {
            case EASY:
                return roundTimeSeconds; // 30 seconds for easy
            case MEDIUM:
                return roundTimeSeconds-10; // 20 seconds for medium
            case HARD:
                return roundTimeSeconds-15 ; // 15 seconds for hard
            default:
                return roundTimeSeconds;
        }
    }

    /**
     * Gets the points awarded for correct answers based on difficulty level.
     */
    private int getPointsForDifficulty() {
        switch (selectedDifficulty) {
            case EASY:
                return pointsPerCorrect; // 10 points for easy
            case MEDIUM:
                return pointsPerCorrect + 5; // 15 points for medium
            case HARD:
                return pointsPerCorrect + 10; // 20 points for hard
            default:
                return pointsPerCorrect;
        }
    }

    // Encapsulated getters
    public int getHearts() {
        return hearts;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalProverbs() {
        return gameProverbs.size();
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public Proverb getCurrentProverb() {
        if (gameProverbs == null || gameProverbs.isEmpty() || currentIndex >= gameProverbs.size()) {
            return null;
        }
        return gameProverbs.get(currentIndex);
    }

    public Proverb.Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    /**
     * Allows the UI to reveal the answer before the timer expires.
     */
    public void revealAnswerEarly(String reason) {
        cancelTimer();
        Proverb current = getCurrentProverb();
        if (current != null) {
            onProverbRevealed(current, reason);
        }
    }
}

