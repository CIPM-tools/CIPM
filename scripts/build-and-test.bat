call scripts\build.bat
cd commit-based-cipm
call scripts\internal\start-update-site-server.bat
call scripts\internal\check-update-site-server.bat
call ..\mvnw.cmd verify -P test
cd ..
call scripts\internal\stop-update-site-server.bat
