package com.tibebguess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class ProverbLoader {
    

    private static List<String> getImagesForDifficulty(Proverb.Difficulty difficulty) {
        List<String> images = new ArrayList<>();
        String dirPath = "/images/" + difficulty.name().toLowerCase() + "/";
        
        try {
            Enumeration<URL> resources = ProverbLoader.class.getClassLoader().getResources("images/" + difficulty.name().toLowerCase());
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                
                if (resource.getProtocol().equals("file")) {
                    java.io.File dir = new java.io.File(resource.getFile());
                    if (dir.exists() && dir.isDirectory()) {
                        java.io.File[] files = dir.listFiles((file, name) -> 
                            name.toLowerCase().endsWith(".jpg") || 
                            name.toLowerCase().endsWith(".png") || 
                            name.toLowerCase().endsWith(".jpeg"));
                        if (files != null) {
                            for (java.io.File file : files) {
                                images.add(dirPath + file.getName());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error scanning images for " + difficulty + ": " + e.getMessage());
        }
        
        return images;
    }
    
    private static List<String> getAllImages() {
        List<String> allImages = new ArrayList<>();
        for (Proverb.Difficulty diff : Proverb.Difficulty.values()) {
            allImages.addAll(getImagesForDifficulty(diff));
        }
        return allImages;
    }
   
    
    public static List<Proverb> loadFromFile(String resourcePath) {
        List<Proverb> proverbs = new ArrayList<>();
        
        try {
            InputStream inputStream = ProverbLoader.class.getResourceAsStream(resourcePath);
            if (inputStream == null) {
                System.err.println("Resource file not found: " + resourcePath);
                return getDefaultProverbs();
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );
            
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    String text = parts[0].trim();
                    String imagePath = parts[1].trim();
                    
                    // Determine difficulty from image path
                    Proverb.Difficulty difficulty = Proverb.Difficulty.EASY; // default
                    if (imagePath.contains("/easy/")) {
                        difficulty = Proverb.Difficulty.EASY;
                    } else if (imagePath.contains("/medium/")) {
                        difficulty = Proverb.Difficulty.MEDIUM;
                    } else if (imagePath.contains("/hard/")) {
                        difficulty = Proverb.Difficulty.HARD;
                    }
                    
                    proverbs.add(new Proverb(text, imagePath, difficulty));
                } else {
                    System.err.println("Invalid format at line " + lineNumber + ": " + line);
                }
                lineNumber++;
            }
            
            reader.close();
            
            if (proverbs.isEmpty()) {
                System.err.println("No proverbs loaded from file. Using defaults.");
                return getDefaultProverbs();
            }
            
        } catch (Exception e) {
            System.err.println("Error loading proverbs from file: " + e.getMessage());
            return getDefaultProverbs();
        }
        
        return proverbs;
    }
    
    /**
     * Returns default hardcoded proverbs
     * This is used as fallback if file loading fails
     */
    public static List<Proverb> getDefaultProverbs() {
        List<Proverb> proverbs = new ArrayList<>();
        
        
        // Additional placeholders to reach 15+ entries
        proverbs.add(new Proverb("ፍየል ከመድረሷ ቅጠል መበጠሷ።", "/images/easy/proverb1.jpg", Proverb.Difficulty.EASY));
        proverbs.add(new Proverb("50 ሎሚ ለአንድ ሰው ሸክሙ ለ50 ሰው ጌጡ ነው።", "/images/easy/proverb2.jpg", Proverb.Difficulty.EASY));
        proverbs.add(new Proverb("ድር ቢያብር አንበሳ ያስር።", "/images/easy/proverb4.jpg", Proverb.Difficulty.EASY));
        proverbs.add(new Proverb("ባልና ሚስት ከአንድ ባህር ይቀዳል።", "/images/easy/proverb7.jpg", Proverb.Difficulty.EASY));
        proverbs.add(new Proverb("ተልባ ቢንጫጫ በአንድ ሙቀጫ።", "/images/easy/proverb11.jpg", Proverb.Difficulty.EASY));
        proverbs.add(new Proverb("ጅብ ማያቀው ሀገር ሄዶ ቁርበት አንጥፉልኝ ይላል።", "/images/easy/proverb12.jpg", Proverb.Difficulty.EASY));
        proverbs.add(new Proverb("ዶሮ ብታልም ጥሬዋን።", "/images/easy/proverb15.jpg", Proverb.Difficulty.EASY));
        proverbs.add(new Proverb("ፍየል ከመድረሷ ቅጠል መበጠሷ።", "/images/proverb8.jpg", Proverb.Difficulty.MEDIUM));
        proverbs.add(new Proverb("ፍየል ከመድረሷ ቅጠል መበጠሷ።", "/images/proverb9.jpg", Proverb.Difficulty.MEDIUM));
        proverbs.add(new Proverb("ፍየል ከመድረሷ ቅጠል መበጠሷ።", "/images/proverb10.jpg", Proverb.Difficulty.HARD));
        proverbs.add(new Proverb("ፍየል ከመድረሷ ቅጠል መበጠሷ።", "/images/proverb11.jpg", Proverb.Difficulty.EASY));
        proverbs.add(new Proverb("ፍየል ከመድረሷ ቅጠል መበጠሷ።", "/images/proverb12.jpg", Proverb.Difficulty.EASY));
        proverbs.add(new Proverb("ፍየል ከመድረሷ ቅጠል መበጠሷ።", "/images/proverb13.jpg", Proverb.Difficulty.MEDIUM));
        proverbs.add(new Proverb("ፍየል ከመድረሷ ቅጠል መበጠሷ።", "/images/proverb14.jpg", Proverb.Difficulty.MEDIUM));
        proverbs.add(new Proverb("ፍየል ከመድረሷ ቅጠል መበጠሷ።", "/images/proverb15.jpg", Proverb.Difficulty.HARD));
        
        return proverbs;
    }
}


