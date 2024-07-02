@echo off

rem Répertoire de base
set BASE_DIR=%CD%


set JAR_DEST_DIR=D:\fianarana\naina\frameworktest\lib

if not exist bin mkdir bin

set CLASSPATH=bin
for %%a in (%BASE_DIR%\lib\*.jar) do (
    set CLASSPATH=%CLASSPATH%;%%a
)

echo Compilation java
for /r %BASE_DIR%\src %%f in (*.java) do javac -classpath %CLASSPATH% -d bin %%f

echo Création du fichier JAR...
jar cf %JAR_DEST_DIR%\framework.jar -C bin .

echo Compilation et création du JAR terminées.