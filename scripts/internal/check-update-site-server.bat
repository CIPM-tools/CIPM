:: Check that the server for the update site runs
curl --retry 100 --retry-connrefused --retry-delay 1 http://localhost:8081/administration/status
if %ERRORLEVEL% NEQ 0 (exit /B 1)
