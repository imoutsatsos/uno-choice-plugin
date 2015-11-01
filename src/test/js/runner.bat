REM This batch script is a wrapper for phantomjs
REM It first checks if the program exists and is executable, if so
REM runs phantomjs normally. Otherwise just pretend nothing happened
REM and keeps with the rest of the project build.

where phantomjs > tmp.txt
SET /p PHANTOMJS_EXEC=<tmp.txt
del tmp.txt

IF EXIST "%PHANTOMJS_EXEC%" ( 
	phantomjs %1 %2
) ELSE (
	exit 0
)
