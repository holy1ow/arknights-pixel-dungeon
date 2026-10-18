@echo off
setlocal
cd /d "%~dp0"
call "%~dp0gradlew.bat" desktop:release desktop:jpackageImage --console=plain --max-workers=2 --no-daemon
if errorlevel 1 (
    echo.
    echo Build failed. Check the errors above.
    pause
    exit /b 1
)
echo.
echo Build complete. Run Start-Game.cmd to play.
pause
exit /b 0
