@echo off
SETLOCAL

SET "SCRIPT_DIR=%~dp0"
SET "MAVEN_VERSION=3.9.9"
SET "MAVEN_DIR=%SCRIPT_DIR%.mvn-dist\apache-maven-%MAVEN_VERSION%"
SET "MAVEN_ZIP=%SCRIPT_DIR%.mvn-dist\apache-maven-%MAVEN_VERSION%-bin.zip"
SET "MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip"
SET "MVN_CMD=%MAVEN_DIR%\bin\mvn.cmd"

REM Use JDK 21 if available
IF EXIST "C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot" (
    SET "JAVA_HOME=C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"
    SET "PATH=C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot\bin;%PATH%"
)

IF NOT EXIST "%MVN_CMD%" (
    echo.
    echo ============================================================
    echo  Maven not found. Downloading automatically...
    echo  This will take about 1 minute (one time only)
    echo ============================================================
    echo.

    IF NOT EXIST "%SCRIPT_DIR%.mvn-dist" mkdir "%SCRIPT_DIR%.mvn-dist"

    powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Write-Host 'Downloading Maven %MAVEN_VERSION%...'; (New-Object System.Net.WebClient).DownloadFile('%MAVEN_URL%', '%MAVEN_ZIP%'); Write-Host 'Done!' }"

    IF NOT EXIST "%MAVEN_ZIP%" (
        echo.
        echo ERROR: Failed to download Maven.
        echo Check your internet connection and try again.
        exit /B 1
    )

    echo Extracting Maven...
    powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%SCRIPT_DIR%.mvn-dist' -Force"
    del "%MAVEN_ZIP%"

    echo.
    echo ============================================================
    echo  Maven installed successfully!
    echo ============================================================
    echo.
)

"%MVN_CMD%" %*
ENDLOCAL
