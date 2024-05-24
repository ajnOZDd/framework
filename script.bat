@echo off

REM Définir le chemin du projet
set PROJECT_DIR=D:\\fianarana\\naina\\framework
set testframework=D:\\fianarana\\naina\\frameworktest\\lib

REM Accéder au répertoire du projet
cd %PROJECT_DIR%

REM Définir le chemin du compilateur Java
set JAVA_HOME=C:\\Program Files\\Java\\jdk-17
set PATH=%JAVA_HOME%\\bin;%PATH%

REM Compiler les fichiers source Java
javac -d target\\classes src\\main\\java\\*.java

REM Créer le fichier JAR
jar cvf framework-1.0.jar -C target\\classes .

REM Copier le JAR vers le dossier de test
xcopy framework-1.0.jar %testframework% /y

echo Création du JAR terminée !