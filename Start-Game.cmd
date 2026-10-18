@echo off
setlocal
cd /d "%~dp0"
set "GAME_EXE=%~dp0desktop\build\jpackage\Arknights Pixel Dungeon\Arknights Pixel Dungeon.exe"
if exist "%GAME_EXE%" (
    start "" "%GAME_EXE%"
    exit /b 0
)
echo The Windows game package is missing.
echo Run Build-Desktop.cmd first, then run this launcher again.
pause
exit /b 1
