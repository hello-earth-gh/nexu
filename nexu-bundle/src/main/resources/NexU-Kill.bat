@echo off
REM for /f "tokens=1" %%i in ('.\java\bin\jps.exe -m ^| find "NexU"') do ( taskkill /F /PID %%i )
wmic Path win32_process Where "CommandLine Like '%%nexu.jar%%'" Call Terminate