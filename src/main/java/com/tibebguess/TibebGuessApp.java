package com.tibebguess;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

/**
 * Main Application Class
 * 
 * Tibeb Guess - A proverb guessing game
 * Displays images representing proverbs and reveals the text after 30 seconds.
 */
public class TibebGuessApp extends Application {
    
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Tibeb Guess - Proverb Game");
            
            // Initialize GameController
            GameController controller = new GameController(primaryStage);
            
            // Initialize GameManager via abstraction (polymorphism)
            AbstractGameManager gameManager = new GameManager(controller);
            controller.setGameManager(gameManager);
            
            // Load proverbs
            List<Proverb> proverbs = loadProverbs();
            gameManager.initializeProverbs(proverbs);
            
            // Create scene
            Scene scene = new Scene(controller.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();
            
            // Game will start when user clicks START GAME button
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
        }
    }
    
    /**
     * Loads proverbs from resources
     * Tries to load from a file first, falls back to default proverbs
     * @return List of Proverb objects
     */
    private List<Proverb> loadProverbs() {
        // Try to load from file first
        List<Proverb> proverbs = ProverbLoader.loadFromFile("/proverbs.txt");
        
        // If file loading failed or returned empty, use defaults
        if (proverbs == null || proverbs.isEmpty()) {
            proverbs = ProverbLoader.getDefaultProverbs();
        }
        
        return proverbs;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}



