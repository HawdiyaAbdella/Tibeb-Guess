package com.tibebguess;


public class Proverb {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    
    private String text;
    private String imagePath;
    private Difficulty difficulty;
    
  
    public Proverb(String text, String imagePath) {
        this(text, imagePath, Difficulty.EASY);
    }
    
   
    public Proverb(String text, String imagePath, Difficulty difficulty) {
        this.text = text;
        this.imagePath = imagePath;
        this.difficulty = difficulty;
    }
    
    
    public String getText() {
        return text;
    }
    
    
    public String getImagePath() {
        return imagePath;
    }
    
   
    public Difficulty getDifficulty() {
        return difficulty;
    }
}


