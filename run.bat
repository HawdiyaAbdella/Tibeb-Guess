@echo off
echo Building Tibeb Guess...
call mvn clean compile
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b %errorlevel%
)

echo.
echo Running Tibeb Guess...
call mvn javafx:run
pause


