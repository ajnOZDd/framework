set "appname=springproject"
set "librariesFolder=lib"
set "sourceFolder=src"
set "jspFolder=web"
set "xmlfolder=conf"
set "tempFolder=D:\fianarana\naina\temp"
set "webInfFolder=%tempFolder%\WEB-INF"
set "outputFolder=classes"
set "xamppath=D:\apache-tomcat-10.1.16-windows-x64\apache-tomcat-10.1.16\webapps"
set "testpath=D:\fianarana\naina\framework\frameworkEtu2085test"

if not exist "%webInfFolder%" mkdir "%webInfFolder%"

del /s /q "%webInfFolder%\*.jsp
del /s /q "%webInfFolder%\*.xml
del /s /q "%webInfFolder%\%outputFolder%\*.class"

dir /s /b /a-d "%sourceFolder%\*.java" > temp.txt

if not exist "%webInfFolder%" mkdir "%webInfFolder%/%libFolder%"

xcopy "%librariesFolder%" "%webInfFolder%\%librariesFolder%" /s /e /i /y 

xcopy "%xmlfolder%" "%webInfFolder%" 

xcopy "%jspFolder%" "%webInfFolder%" /s /e /i /y 

javac -d "%webInfFolder%\%outputFolder%" -classpath "%librariesFolder%\*;%CLASSPATH%" @temp.txt

jar -cvf "%tempFolder%\%appname%.war" -C "%webInfFolder%" .

xcopy  "%tempFolder%\%appname%.war" "%xamppath%"


