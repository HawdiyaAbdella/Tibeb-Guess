# Quick Start Guide - How to Run Tibeb Guess

## Method 1: Using the Run Script (Easiest for Windows)

1. **Navigate to the project folder:**
   ```
   C:\Users\hp\Tibeb-Guess
   ```

2. **Double-click `run.bat`**
   - This will automatically build and run the application
   - OR open Command Prompt/PowerShell in the folder and type: `run.bat`

---

## Method 2: Using Maven Commands

### If you have Maven installed:

1. **Open Command Prompt or PowerShell** in the project folder

2. **Run the application:**
   ```bash
   mvn javafx:run
   ```

   This will:
   - Download dependencies (first time only)
   - Compile the code
   - Run the application

### First time setup (if Maven is not installed):

**Option A: Install Maven**
1. Download from: https://maven.apache.org/download.cgi
2. Extract and add to PATH
3. Verify: `mvn -version`

**Option B: Use an IDE** (Recommended - see Method 3)

---

## Method 3: Using an IDE (Recommended)

### IntelliJ IDEA:

1. **Open the project:**
   - File → Open → Select `C:\Users\hp\Tibeb-Guess` folder
   - Wait for Maven to sync (bottom right corner)

2. **Run the application:**
   - Find `TibebGuessApp.java` in the Project Explorer
   - Right-click on it
   - Select **"Run 'TibebGuessApp.main()'"**
   - OR click the green play button next to the main method

### Eclipse:

1. **Import the project:**
   - File → Import → Maven → Existing Maven Projects
   - Select the project folder
   - Click Finish

2. **Run the application:**
   - Right-click on `TibebGuessApp.java`
   - Run As → Java Application

### VS Code:

1. **Install extensions:**
   - Java Extension Pack (by Microsoft)
   - Extension Pack for Java

2. **Open the project:**
   - File → Open Folder → Select `C:\Users\hp\Tibeb-Guess`

3. **Run the application:**
   - Press `F5` or click the "Run" button
   - Select "Java" as the environment

---

## Method 4: Direct Java Command (Advanced)

If you have JavaFX SDK installed separately:

```bash
# Compile first
mvn clean compile

# Run with JavaFX modules
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -cp target/classes com.tibebguess.TibebGuessApp
```

---

## What to Expect

When you run the application:

1. ✅ **Start Screen** appears with:
   - "Tibeb Guess" title
   - "START GAME" button (pink button)

2. ✅ Click **"START GAME"**

3. ✅ **Game Screen** shows:
   - Proverb image in a white card
   - Timer (🕐), Hearts (❤), and Score at the top
   - "Show Answer" button

4. ✅ Play the game:
   - Wait for timer or click "Show Answer"
   - Click CORRECT or WRONG
   - Game continues until hearts = 0 or all proverbs shown

5. ✅ **Game Over Screen** appears with final score

---

## Troubleshooting

### Error: "mvn: command not found"
- **Solution:** Install Maven OR use an IDE (Method 3)

### Error: "JavaFX runtime components are missing"
- **Solution:** Make sure you're using Java 11+ and Maven downloads dependencies
- Try: `mvn clean install` first

### Error: "No proverbs loaded"
- **Solution:** The game will use default proverbs
- To add your own: Edit `src/main/resources/proverbs.txt`
- Add images to: `src/main/resources/images/`

### Application window doesn't appear
- Check if Java is installed: `java -version`
- Make sure Java 11 or higher is installed
- Try running from an IDE instead

### Images not showing
- Add your proverb images to: `src/main/resources/images/`
- Name them: `proverb1.jpg`, `proverb2.jpg`, etc.
- Update `proverbs.txt` with correct image paths

---

## Quick Checklist

- [ ] Java 11+ installed (`java -version`)
- [ ] Project folder opened
- [ ] Choose a method above to run
- [ ] Application window should open!

---

## Need Help?

- Check `README.md` for more details
- Make sure all files are in place:
  - `TibebGuessApp.java` (main entry point)
  - `GameController.java` (UI)
  - `GameManager.java` (game logic)
  - `Proverb.java` (data class)
  - `ProverbLoader.java` (data loader)



















