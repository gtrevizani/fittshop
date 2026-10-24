@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@IF "%DEBUG%" == "" @ECHO OFF
@SETLOCAL

SET ERROR_CODE=0

@REM Set local scope for the variables with windows NT shell
IF "%OS%"=="Windows_NT" @SETLOCAL

@REM ==== START VALIDATION ====
IF NOT "%JAVA_HOME%" == "" GOTO OkJHome

ECHO.
ECHO Error: JAVA_HOME not found in your environment. >&2
ECHO Please set the JAVA_HOME variable in your environment to match the >&2
ECHO location of your Java installation. >&2
ECHO.
GOTO error

:OkJHome
IF EXIST "%JAVA_HOME%\bin\java.exe" GOTO init

ECHO.
ECHO Error: JAVA_HOME is set to an invalid directory. >&2
ECHO JAVA_HOME = "%JAVA_HOME%" >&2
ECHO Please set the JAVA_HOME variable in your environment to match the >&2
ECHO location of your Java installation. >&2
ECHO.
GOTO error

:init
@REM Find the project base dir, i.e. the dir that contains the folder ".mvn".
@REM Fallback to current dir if not found.

SET MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" GOTO endDetectBaseDir

SET EXEC_DIR=%CD%
SET WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%\.mvn" GOTO baseDirFound
CD ..
IF "%WDIR%"=="%CD%" GOTO baseDirNotFound
SET WDIR=%CD%
GOTO findBaseDir

:baseDirFound
SET MAVEN_PROJECTBASEDIR=%WDIR%
CD "%EXEC_DIR%"
GOTO endDetectBaseDir

:baseDirNotFound
SET MAVEN_PROJECTBASEDIR=%EXEC_DIR%
CD "%EXEC_DIR%"

:endDetectBaseDir

SET WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

"%JAVA_HOME%\bin\java.exe" %JVM_CONFIG_MAVEN_PROPS% -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %*
IF ERRORLEVEL 1 GOTO error
GOTO end

:error
SET ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
IF "%OS%"=="Windows_NT" ENDLOCAL

@REM For backward compatibility
SET ERRORLEVEL=%ERROR_CODE%
EXIT /B %ERROR_CODE%
