@echo off
set /p env=Which environment ('sandbox', 'dev', 'test' or 'prod') would you like to build for?

if "%env%" NEQ "sandbox" (
    if "%env%" NEQ "dev" (
        if "%env%" NEQ "test" (
			if "%env%" NEQ "prod" goto invalidEnv
		)
    )
)

echo Selected environment to build: %env%

set /p imageName=Type name of docker image you like to build

if "%imageName%" NEQ "" (
    set imageName=iq-image
)

echo Selected environment to build: %env%

set SSB_INSTALL_DIR_NAME=iiq-app

set SPTARGET=%env%

set CURRENT_DIR=%cd%

cd %SSB_INSTALL_DIR_NAME%
CALL build.bat clean war
cd %CURRENT_DIR%

xcopy /S %SSB_INSTALL_DIR_NAME%\build\deploy\identityiq.war iiq-app-docker\volumes /Y

cd iiq-app-docker

echo Building IIQ Docker image

docker build -t %imageName% .

goto theEnd

:invalidEnv (
    echo Invalid env input value
    goto theEnd
)

:theEnd (
	cd %CURRENT_DIR%
)