:: Build the actual pipeline
call scripts\internal\start-update-site-server.bat
call scripts\internal\check-update-site-server.bat
cd commit-based-cipm
call ..\mvnw.cmd clean verify -P run-one
cd ..
call scripts\internal\stop-update-site-server.bat
call scripts\internal\start-update-site-server.bat
call scripts\internal\check-update-site-server.bat
cd commit-based-cipm
call ..\mvnw.cmd clean verify -P run-two
cd ..
call scripts\internal\stop-update-site-server.bat
call scripts\internal\start-update-site-server.bat
call scripts\internal\check-update-site-server.bat
cd commit-based-cipm
call ..\mvnw.cmd clean verify -P run-three
cd ..
call scripts\internal\stop-update-site-server.bat
exit /b 0
