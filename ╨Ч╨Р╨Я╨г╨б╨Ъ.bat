@echo off
chcp 65001 >nul
echo.
echo  ╔══════════════════════════════════╗
echo  ║   School Management System       ║
echo  ║   Запуск приложения...           ║
echo  ╚══════════════════════════════════╝
echo.

REM Check Java
java -version >nul 2>&1
IF ERRORLEVEL 1 (
    echo ОШИБКА: Java не установлена!
    echo Скачайте с: https://adoptium.net
    echo.
    pause
    exit /B 1
)

cd /d "%~dp0"
call mvnw.cmd spring-boot:run

pause
