:: Check Java version
"%JAVA_HOME%\bin\java" -version 2>&1|findstr /R "11\.0\."
if %ERRORLEVEL% NEQ 0 (exit /B 1)
