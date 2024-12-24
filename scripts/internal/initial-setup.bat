call scripts\internal\check-java-version.bat

:: Initialize submodules
git submodule init
git submodule update

:: Prepare bundles from the CIPM-Pipeline
cd CIPM-Pipeline\cipm.consistency.bridge.eclipse\cipm.consistency.base.shared\dep-generator
call gradlew.bat bundle copyBundles
cd ..\..\..\..
robocopy CIPM-Pipeline\cipm.consistency.bridge.eclipse commit-based-cipm\bundles\fi /E

call scripts\internal\start-update-site-server.bat
call scripts\internal\check-update-site-server.bat

cd Vitruv
robocopy ..\.mvn .mvn /E
call ..\mvnw.cmd clean verify
cd ..

call scripts\internal\stop-update-site-server.bat
