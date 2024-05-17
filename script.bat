@echo off

rem Définir les variables
set "appname=springproject"
set "librariesFolder=lib"
set "sourceFolder=src"
set "jspFolder=web"
set "xmlfolder=conf"
set "libFolder=lib"
set "tempFolder=D:\fianarana\naina\temp"
set "webInfFolder=%tempFolder%\WEB-INF"
set "outputFolder=classes"
set "tomcatWebappsPath=D:\apache-tomcat-10.1.16-windows-x64\apache-tomcat-10.1.16\webapps"
set "frameworkTestFolder=D:\fianarana\naina\frameworktest\lib"
set "tomcatBinPath=D:\apache-tomcat-10.1.16-windows-x64\apache-tomcat-10.1.16\bin"

rem Vérifier si le serveur Tomcat est déjà démarré
tasklist | find /i "java.exe" > nul
if errorlevel 1 goto tomcatNotRunning
echo Serveur Tomcat déjà démarré, redémarrage en cours...
goto tomcatRestart

:tomcatNotRunning
echo Serveur Tomcat non démarré, démarrage en cours...
call "%tomcatBinPath%\startup.bat"
if errorlevel 1 goto tomcatStartFailed

rem Créer le dossier WEB-INF s'il n'existe pas
if not exist "%webInfFolder%" mkdir "%webInefFolder%"

rem Supprimer les fichiers existants
del /s /q "%webInfFolder%\*.jsp"
del /s /q "%webInfFolder%\*.xml"
del /s /q "%webInfFolder%\%outputFolder%\*.class"

rem Créer un fichier temporaire avec la liste des fichiers Java
dir /s /b /a-d "%sourceFolder%\*.java" > temp.txt

rem Créer le dossier WEB-INF\lib s'il n'existe pas
if not exist "%webInfFolder%\%libFolder%" mkdir "%webInfFolder%\%libFolder%"

rem Copier les bibliothèques dans le dossier WEB-INF\lib
xcopy "%librariesFolder%" "%webInfFolder%\%libFolder%" /s /e /i /y 

rem Copier les fichiers XML dans le dossier WEB-INF
xcopy "%xmlfolder%" "%webInfFolder%" 

rem Copier les fichiers JSP dans le dossier WEB-INF
xcopy "%jspFolder%" "%webInfFolder%" /s /e /i /y 

rem Compiler les fichiers Java
javac -d "%webInfFolder%\%outputFolder%" -classpath "%librariesFolder%\*;%CLASSPATH%" @temp.txt

rem Créer le fichier JAR
jar cf "%frameworkTestFolder%\application.jar" -C "%webInfFolder%\%outputFolder%" .

rem Créer le fichier WAR
jar -cvf "%tempFolder%\%appname%.war" -C "%webInfFolder%" .

rem Copier le fichier WAR dans le dossier des applications Tomcat
xcopy  "%tempFolder%\%appname%.war" "%tomcatWebappsPath%"

rem Copier le fichier JAR dans le dossier frameworktest
xcopy "%frameworkTestFolder%\application.jar" "%frameworkTestFolder%" /y

:tomcatRestart
rem Attendre 10 secondes avant de redémarrer Tomcat pour que les modifications soient prises en compte
timeout /t 10 > nul
call "%tomcatBinPath%\shutdown.bat"
call "%tomcatBinPath%\startup.bat"

:tomcatStartFailed
echo Erreur lors du démarrage du serveur Tomcat. Veuillez vérifier les logs.
goto end

:end
echo Application déployée avec succès !