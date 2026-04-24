#!/bin/sh
# Maven Wrapper script for Unix/Mac
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(dirname "$0")}"
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Downloading Maven Wrapper..."
    if command -v curl > /dev/null 2>&1; then
        curl -o "$WRAPPER_JAR" "$DOWNLOAD_URL" -s
    elif command -v wget > /dev/null 2>&1; then
        wget -q -O "$WRAPPER_JAR" "$DOWNLOAD_URL"
    fi
fi

exec "$JAVA_HOME/bin/java" \
    -classpath "$WRAPPER_JAR" \
    "-Dmaven.multiModuleProjectDirectory=$MAVEN_PROJECTBASEDIR" \
    org.apache.maven.wrapper.MavenWrapperMain "$@"
