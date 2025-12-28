package com.tibebguess;

import java.io.InputStream;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * GameController connects JavaFX UI with the game logic.
 * Handles screens, buttons, images, and updates.
 */
public class GameController {

    private static final String ANSWER_HINT_TEXT = "\"Show Answer\" በሚጠቀሙ ጊዜ ይታያል።";
    private Font amharicFont;
    private Label answerTitle;

    // Screens
    private VBox startScreen;
    private VBox gameScreen;
    private ScrollPane gameScrollPane;
    private VBox resultScreen;

    // Image and answer
    private StackPane imageWrapper;
    private ImageView imageView;
    private Label imagePlaceholder;
    private VBox answerCard;
    private Label proverbTextLabel;
    private Label answerSubtitleLabel;
    private HBox answerButtonsContainer;
    private Button correctButton;
    private Button wrongButton;
    private Button showAnswerButton;
    private Label feedbackLabel;

    // Status labels
    private Label timerLabel;
    private Label scoreLabel;
    private Label heartsLabel;

    private boolean answerRevealed;

    // Root and game manager
    private StackPane root;
    private AbstractGameManager gameManager;

    public GameController(Stage primaryStage) {
        this.root = new StackPane();
        this.answerRevealed = false;
        initializeUI();
    }

    public void setGameManager(AbstractGameManager gameManager) {
        this.gameManager = gameManager;
    }

    // Initialize UI screens and layout
    private void initializeUI() {
        // Load Amharic font
        // Force the JVM to recognize the Ethiopic font family
    try (InputStream is = getClass().getResourceAsStream("/fonts/NotoSansEthiopic-Regular.ttf")) {
        if (is != null) {
            amharicFont = Font.loadFont(is, 12); // This registers "Noto Sans Ethiopic" globally
            System.out.println("Loaded Amharic font: " + (amharicFont != null ? amharicFont.getFamily() : "null"));
        }
    } catch (Exception e) {
        System.err.println("Could not load Amharic font: " + e.getMessage());
    }

    // Existing CSS loading
    root.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Root gradient
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        // Screens
        startScreen = createStartScreen();
        gameScreen = createGameScreen(amharicFont);
        gameScrollPane = new ScrollPane(gameScreen);
        gameScrollPane.setFitToWidth(true);
        gameScrollPane.setFitToHeight(true);
        gameScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        gameScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        gameScrollPane.setPannable(true);
        gameScrollPane.setStyle("-fx-background-color: transparent;");
        gameScrollPane.setVisible(false);

        resultScreen = createResultScreen();
        resultScreen.setVisible(false);

        gameScreen.prefWidthProperty().bind(root.widthProperty());
        gameScreen.prefHeightProperty().bind(root.heightProperty());

        root.getChildren().addAll(startScreen, gameScrollPane, resultScreen);
    }

    // Start screen with logo and start button
    private VBox createStartScreen() {
        VBox startContainer = new VBox(30); // Increased spacing between major sections
        startContainer.setAlignment(Pos.CENTER);
        startContainer.setPadding(new Insets(40));

        // Logo image - using Circle for perfect circular shape
        Circle logo = new Circle(75); // Radius of 75 for 150px diameter
        logo.setStroke(Color.web("#667eea"));
        logo.setStrokeWidth(2);

        try (InputStream stream = getClass().getResourceAsStream("/images/logo.png")) {
            if (stream != null) {
                Image img = new Image(stream);
                logo.setFill(new ImagePattern(img));
            } else {
                System.out.println("Logo image not found at /images/logo.png. Please add your logo image to src/main/resources/images/logo.png");
                logo.setFill(Color.web("#667eea"));
            }
        } catch (Exception e) {
            System.err.println("Error loading logo image: " + e.getMessage());
            logo.setFill(Color.web("#667eea"));
        }

        Label title = new Label("Tibeb Guess");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 56));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("Ethiopian Proverb Guessing Game");
        subtitle.setFont(Font.font("Arial", 20));
        subtitle.setTextFill(Color.WHITE);
        subtitle.setOpacity(0.9);

        // Group logo, title, and subtitle together with close spacing
        VBox titleGroup = new VBox(8); // Small spacing between logo, title, and subtitle
        titleGroup.setAlignment(Pos.CENTER);
        titleGroup.getChildren().addAll(logo, title, subtitle);


        HBox difficultyButtons = new HBox(15);
        difficultyButtons.setAlignment(Pos.CENTER);

        Button easyButton = new Button("EASY");
        Button mediumButton = new Button("MEDIUM");
        Button hardButton = new Button("HARD");
        

        easyButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        easyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 8; -fx-cursor: hand;");
        easyButton.setOnAction(e -> selectDifficulty(Proverb.Difficulty.EASY, easyButton, mediumButton, hardButton));

        mediumButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        mediumButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 8; -fx-cursor: hand;");
        mediumButton.setOnAction(e -> selectDifficulty(Proverb.Difficulty.MEDIUM, mediumButton, easyButton, hardButton));

        hardButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        hardButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 8; -fx-cursor: hand;");
        hardButton.setOnAction(e -> selectDifficulty(Proverb.Difficulty.HARD, hardButton, easyButton, mediumButton));

        

        difficultyButtons.getChildren().addAll(easyButton, mediumButton, hardButton);

        // Initially select medium
        selectDifficulty(Proverb.Difficulty.MEDIUM, mediumButton, easyButton, hardButton);

        Button startButton = new Button("START GAME");
        startButton.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        startButton.setStyle("-fx-background-color: #ff6b9d; -fx-text-fill: white; -fx-padding: 20 50; -fx-background-radius: 10; -fx-cursor: hand;");
        startButton.setOnAction(e -> {
            startScreen.setVisible(false);
            gameScrollPane.setVisible(true);
            if (gameManager != null) gameManager.startGame();
        });

        startContainer.getChildren().addAll(titleGroup, difficultyButtons, startButton);
        return startContainer;
    }

    private void selectDifficulty(Proverb.Difficulty difficulty, Button selectedButton, Button... otherButtons) {
        // Update button styles
        String selectedColor = selectedButton.getText().equals("EASY") ? "#4CAF50" :
                               selectedButton.getText().equals("MEDIUM") ? "#FF9800" : "#f44336";
        selectedButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 8; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(33,150,243,0.5), 5,0,0,0);");

        for (Button button : otherButtons) {
            String color = button.getText().equals("EASY") ? "#4CAF50" :
                          button.getText().equals("MEDIUM") ? "#FF9800" :
                          button.getText().equals("HARD") ? "#f44336" : "#9C27B0";
            button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 8; -fx-cursor: hand;");
        }

        // Set difficulty in game manager
        if (gameManager != null) {
            gameManager.setDifficulty(difficulty);
        }
    }

    // Main game screen with status, image, and answer card
    private VBox createGameScreen(Font amharicFont) {
        VBox mainContainer = new VBox(30);
        mainContainer.setPadding(new Insets(30, 40, 60, 40));
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.setStyle("-fx-background-color:linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        // Status bar
        HBox statusBar = new HBox(40);
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setPadding(new Insets(15));

        HBox timerBox = new HBox(8);
        timerBox.setAlignment(Pos.CENTER);
        Label clockIcon = new Label("🕐");
        clockIcon.setFont(Font.font(20));
        timerLabel = new Label("30");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        timerLabel.setTextFill(Color.WHITE);
        timerBox.getChildren().addAll(clockIcon, timerLabel);

        heartsLabel = new Label("❤❤❤");
        heartsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        heartsLabel.setTextFill(Color.RED);

        HBox scoreBox = new HBox(8);
        scoreBox.setAlignment(Pos.CENTER);
        Label scoreText = new Label("SCORE");
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        scoreText.setTextFill(Color.WHITE);
        scoreLabel = new Label("0");
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        scoreLabel.setTextFill(Color.WHITE);
        scoreBox.getChildren().addAll(scoreText, scoreLabel);

        // Difficulty indicator
        Label difficultyLabel = new Label("MEDIUM");
        difficultyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        difficultyLabel.setTextFill(Color.CYAN);
        difficultyLabel.setId("difficultyLabel");

        statusBar.getChildren().addAll(timerBox, heartsLabel, scoreBox, difficultyLabel);

        // Content row: image + answer card
        HBox contentRow = new HBox(30);
        contentRow.setAlignment(Pos.CENTER);
        contentRow.setMaxWidth(1100);
        contentRow.setMinHeight(420);

        imageWrapper = new StackPane();
        imageWrapper.prefWidthProperty().bind(root.widthProperty().multiply(0.55));
        imageWrapper.prefHeightProperty().bind(root.heightProperty().multiply(0.55));
        imageWrapper.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 15,0,0,6);");

        imageView = new ImageView();
        imageView.fitWidthProperty().bind(imageWrapper.widthProperty().subtract(40));
        imageView.fitHeightProperty().bind(imageWrapper.heightProperty().subtract(40));
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        imagePlaceholder = new Label("Add your proverb image to src/main/resources/images and update proverbs.txt.");
        imagePlaceholder.setWrapText(true);
        imagePlaceholder.setTextFill(Color.GRAY);
        imagePlaceholder.setFont(Font.font(16));
        imagePlaceholder.setMaxWidth(420);
        imagePlaceholder.setAlignment(Pos.CENTER);

        imageWrapper.getChildren().addAll(imageView, imagePlaceholder);

        // Answer card
        answerCard = new VBox(12);
        answerCard.setAlignment(Pos.TOP_CENTER);
        answerCard.setPadding(new Insets(25));
        answerCard.prefWidthProperty().bind(root.widthProperty().multiply(0.35));
        answerCard.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 15,0,0,6);");
        answerCard.setVisible(false);
        answerCard.setManaged(false);

        Label answerTitle = new Label("Answer");
        
answerTitle.getStyleClass().add("label");
        answerTitle.setFont(amharicFont != null ? Font.font(amharicFont.getFamily(), FontWeight.BOLD, 24) : Font.font(24));
        answerTitle.setTextFill(Color.web("#333333"));

        answerSubtitleLabel = new Label(ANSWER_HINT_TEXT);
        answerSubtitleLabel.setFont(amharicFont != null ? Font.font(amharicFont.getFamily(), 16) : Font.font(16));
        answerSubtitleLabel.setTextFill(Color.web("#666666"));
        answerSubtitleLabel.setWrapText(true);
        answerSubtitleLabel.setAlignment(Pos.CENTER);

        proverbTextLabel = new Label();
        proverbTextLabel.setFont(amharicFont != null ? Font.font(amharicFont.getFamily(), FontWeight.BOLD, 18) : Font.font("Arial", FontWeight.BOLD, 18));
        proverbTextLabel.setTextFill(Color.web("#222222"));
        proverbTextLabel.setWrapText(true);
        proverbTextLabel.setAlignment(Pos.CENTER);
        proverbTextLabel.setMaxWidth(360);

        answerButtonsContainer = new HBox(15);
        answerButtonsContainer.setAlignment(Pos.CENTER);
        answerButtonsContainer.setVisible(false);
        answerButtonsContainer.setManaged(false);
        answerButtonsContainer.setPadding(new Insets(10, 0, 0, 0));

        correctButton = new Button("CORRECT");
        correctButton.setFont(amharicFont != null ? Font.font(amharicFont.getFamily(), FontWeight.BOLD, 18) : Font.font("Arial", FontWeight.BOLD, 18));
        correctButton.setStyle("-fx-background-color: linear-gradient(to right, #4CAF50, #6fdc6f); -fx-text-fill: white; -fx-padding: 14 36; -fx-background-radius: 12; -fx-cursor: hand;");
        correctButton.setOnAction(e -> handleCorrectAnswer());

        wrongButton = new Button("WRONG");
        wrongButton.setFont(amharicFont != null ? Font.font(amharicFont.getFamily(), FontWeight.BOLD, 18) : Font.font("Arial", FontWeight.BOLD, 18));
        wrongButton.setStyle("-fx-background-color: linear-gradient(to right, #f5576c, #f093fb); -fx-text-fill: white; -fx-padding: 14 36; -fx-background-radius: 12; -fx-cursor: hand;");
        wrongButton.setOnAction(e -> handleWrongAnswer());

        answerButtonsContainer.getChildren().addAll(correctButton, wrongButton);
        setAnswerButtonsEnabled(false);

        feedbackLabel = new Label();
        feedbackLabel.setFont(amharicFont != null ? Font.font(amharicFont.getFamily(), FontWeight.BOLD, 18) : Font.font("Arial", FontWeight.BOLD, 18));
        feedbackLabel.setTextFill(Color.web("#444444"));
        feedbackLabel.setAlignment(Pos.CENTER);
        feedbackLabel.setWrapText(true);
        feedbackLabel.setVisible(false);

        answerCard.getChildren().addAll(answerTitle, answerSubtitleLabel, proverbTextLabel, answerButtonsContainer, feedbackLabel);
        contentRow.getChildren().addAll(imageWrapper, answerCard);

        showAnswerButton = new Button("Show Answer");
        showAnswerButton.setFont(amharicFont != null ? Font.font(amharicFont.getFamily(), FontWeight.BOLD, 18) : Font.font("Arial", FontWeight.BOLD, 18));
        showAnswerButton.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-padding: 14 40; -fx-background-radius: 12; -fx-cursor: hand;");
        showAnswerButton.setOnAction(e -> showAnswerEarly());

        mainContainer.getChildren().addAll(statusBar, contentRow, showAnswerButton);
        return mainContainer;
    }

    // Result screen at the end
    private VBox createResultScreen() {
        VBox resultContainer = new VBox(25);
        resultContainer.setAlignment(Pos.CENTER);
        resultContainer.setPadding(new Insets(40));

        VBox cardContainer = new VBox(25);
        cardContainer.setAlignment(Pos.CENTER);
        cardContainer.setPadding(new Insets(50));
        cardContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10,0,0,5);");
        cardContainer.setMaxWidth(600);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER);
        Label brokenHeart = new Label("💔");
        brokenHeart.setFont(Font.font(36));
        Label gameOver = new Label("Game Over");
        gameOver.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        gameOver.setTextFill(Color.BLACK);
        header.getChildren().addAll(brokenHeart, gameOver);

        Label heartsMessage = new Label("You've run out of hearts!");
        heartsMessage.setFont(Font.font("Arial", 18));
        heartsMessage.setTextFill(Color.GRAY);
        heartsMessage.setId("heartsMessage");

        VBox scoreContainer = new VBox(5);
        scoreContainer.setAlignment(Pos.CENTER);
        Label finalScoreTitle = new Label("FINAL SCORE");
        finalScoreTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        finalScoreTitle.setTextFill(Color.GRAY);
        Label finalScoreLabel = new Label();
        finalScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        finalScoreLabel.setTextFill(Color.BLACK);
        finalScoreLabel.setId("finalScore");

        Label difficultyResultLabel = new Label();
        difficultyResultLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        difficultyResultLabel.setTextFill(Color.BLUE);
        difficultyResultLabel.setId("difficultyResult");

        scoreContainer.getChildren().addAll(finalScoreTitle, finalScoreLabel, difficultyResultLabel);

        Label motivational = new Label("Well played! Keep learning and having fun with Ethiopian proverbs!");
        motivational.setFont(Font.font("Arial", 16));
        motivational.setTextFill(Color.GRAY);
        motivational.setWrapText(true);
        motivational.setMaxWidth(500);
        motivational.setAlignment(Pos.CENTER);
        motivational.setId("motivational");

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button playAgain = new Button("PLAY AGAIN");
        playAgain.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        playAgain.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 15 40; -fx-background-radius: 8; -fx-cursor: hand;");
        playAgain.setOnAction(e -> {
            resultScreen.setVisible(false);
            startScreen.setVisible(true);
            gameScrollPane.setVisible(false);
        });

        Button exit = new Button("EXIT");
        exit.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        exit.setStyle("-fx-background-color: #764ba2; -fx-text-fill: white; -fx-padding: 15 40; -fx-background-radius: 8; -fx-cursor: hand;");
        exit.setOnAction(e -> Platform.exit());

        buttonBox.getChildren().addAll(playAgain, exit);

        cardContainer.getChildren().addAll(header, heartsMessage, scoreContainer, motivational, buttonBox);
        resultContainer.getChildren().add(cardContainer);

        return resultContainer;
    }

    // Handle correct and wrong answers
    private void handleCorrectAnswer() {
        if (gameManager != null && answerRevealed) {
            setAnswerButtonsEnabled(false);
            gameManager.handleAnswer(true);
        }
    }

    private void handleWrongAnswer() {
        if (gameManager != null && answerRevealed) {
            setAnswerButtonsEnabled(false);
            gameManager.handleAnswer(false);
        }
    }

    private void setAnswerButtonsEnabled(boolean enabled) {
        correctButton.setDisable(!enabled);
        wrongButton.setDisable(!enabled);
    }

    private void resetAnswerCard() {
        answerCard.setVisible(false);
        answerCard.setManaged(false);
        answerButtonsContainer.setVisible(false);
        answerButtonsContainer.setManaged(false);
        showAnswerButton.setVisible(true);
        setAnswerButtonsEnabled(false);
        proverbTextLabel.setText("");
        answerSubtitleLabel.setText(ANSWER_HINT_TEXT);
        feedbackLabel.setVisible(false);
        answerRevealed = false;
    }

    // Update status UI
    public void updateUI() {
        Platform.runLater(() -> {
            if (gameManager != null) {
                scoreLabel.setText(String.valueOf(gameManager.getScore()));
                int hearts = gameManager.getHearts();
                StringBuilder h = new StringBuilder();
                for (int i = 0; i < hearts; i++) h.append("❤");
                heartsLabel.setText(h.toString());
                heartsLabel.setTextFill(hearts > 1 ? Color.RED : (hearts == 1 ? Color.ORANGE : Color.GRAY));
                updateDifficultyDisplay();
            }
        });
    }

    private void updateDifficultyDisplay() {
        Platform.runLater(() -> {
            Label difficultyLabel = (Label) root.lookup("#difficultyLabel");
            if (difficultyLabel != null && gameManager != null) {
                Proverb.Difficulty difficulty = gameManager.getSelectedDifficulty();
                String displayText = difficulty == null ? "RANDOM" : difficulty.toString();
                difficultyLabel.setText(displayText);
                if (difficulty == null) {
                    difficultyLabel.setTextFill(Color.MAGENTA);
                } else {
                    switch (difficulty) {
                        case EASY:
                            difficultyLabel.setTextFill(Color.LIGHTGREEN);
                            break;
                        case MEDIUM:
                            difficultyLabel.setTextFill(Color.CYAN);
                            break;
                        case HARD:
                            difficultyLabel.setTextFill(Color.ORANGE);
                            break;
                    }
                }
            }
        });
    }

    public void updateTimer(int seconds) {
        Platform.runLater(() -> {
            timerLabel.setText(String.valueOf(Math.max(seconds, 0)));
            if (seconds <= 5) timerLabel.setTextFill(Color.RED);
            else if (seconds <= 10) timerLabel.setTextFill(Color.ORANGE);
            else timerLabel.setTextFill(Color.WHITE);
        });
    }

    // Show proverb image
    public void showProverbImage(Proverb proverb) {
        Platform.runLater(() -> {
            resetAnswerCard();
            if (proverb == null) {
                imageView.setImage(null);
                imagePlaceholder.setText("No proverb data loaded.");
                imagePlaceholder.setVisible(true);
                return;
            }

            String path = proverb.getImagePath();
            if (path == null || path.isBlank()) {
                imageView.setImage(null);
                imagePlaceholder.setText("No image path provided.");
                imagePlaceholder.setVisible(true);
                return;
            }

            try (InputStream stream = getClass().getResourceAsStream(path)) {
                if (stream == null) throw new IllegalArgumentException("Image not found: " + path);
                Image img = new Image(stream);
                imageView.setImage(img);
                imagePlaceholder.setVisible(false);
            } catch (Exception e) {
                imageView.setImage(null);
                imagePlaceholder.setText("Missing image: " + path + "\nPlace under src/main/resources" + path);
                imagePlaceholder.setVisible(true);
                System.err.println("Error loading image: " + e.getMessage());
            }
        });
    }

    public void revealAnswer(Proverb proverb, String subtitle) {
        Platform.runLater(() -> {
            if (proverb == null) return;
    
            // 1. Set the text from your UTF-8 source
            proverbTextLabel.setText(proverb.getText());
            answerSubtitleLabel.setText(subtitle != null ? subtitle : "");
    
            // 2. FORCE the Amharic font (Nyala is built into Windows and prevents boxes)
            String amharicStyle = "-fx-font-family: 'Nyala', 'Noto Sans Ethiopic', 'Abyssinica SIL';";
            
            proverbTextLabel.setStyle(amharicStyle + " -fx-font-size: 20px;");
            answerSubtitleLabel.setStyle(amharicStyle + " -fx-font-size: 16px;");
    
            // FIX FOR LINE 440: Check if answerTitle exists before styling it
            // Ensure 'private Label answerTitle;' is declared at the top of your class
            if (answerTitle != null) {
                answerTitle.setStyle(amharicStyle + " -fx-font-size: 24px; -fx-font-weight: bold;");
            }
    
            // 3. Update visibility
            answerCard.setVisible(true);
            answerCard.setManaged(true);
            answerButtonsContainer.setVisible(true);
            answerButtonsContainer.setManaged(true);
            showAnswerButton.setVisible(false);
            setAnswerButtonsEnabled(true);
            feedbackLabel.setVisible(false);
            answerRevealed = true;
        });
    }

    private void showAnswerEarly() {
        if (gameManager != null && !answerRevealed) {
            gameManager.revealAnswerEarly("");
        }
    }

    public void showFinalResult(int score, int hearts, int completed, int total) {
        Platform.runLater(() -> {
            Label finalScoreLabel = (Label) resultScreen.lookup("#finalScore");
            Label heartsMessage = (Label) resultScreen.lookup("#heartsMessage");
            Label difficultyResultLabel = (Label) resultScreen.lookup("#difficultyResult");

            if (finalScoreLabel != null) finalScoreLabel.setText(String.valueOf(score));
            if (heartsMessage != null) {
                heartsMessage.setText(hearts <= 0 ? "You've run out of hearts!" : String.format("Finished! %d / %d proverbs", completed, total));
            }
            if (difficultyResultLabel != null && gameManager != null) {
                difficultyResultLabel.setText(gameManager.getSelectedDifficulty().toString() + " LEVEL");
            }

            gameScrollPane.setVisible(false);
            resultScreen.setVisible(true);
        });
    }

    public void showProverbImage() {
        if (gameManager != null) showProverbImage(gameManager.getCurrentProverb());
    }

    public void showFeedback(String message) {
        Platform.runLater(() -> {
            if (message == null || message.isBlank()) feedbackLabel.setVisible(false);
            else {
                feedbackLabel.setText(message);
                feedbackLabel.setVisible(true);
            }
        });
    }

    public void resetRoundUI() {
        Platform.runLater(this::resetAnswerCard);
    }

    public StackPane getRoot() {
        return root;
    }
}
