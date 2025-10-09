@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Gradle startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto initWrapper

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto initWrapper

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:initWrapper
call :ensureWrapperJar
if %ERRORLEVEL% neq 0 goto fail
goto execute

:execute
@rem Setup the command line

set CLASSPATH=


@rem Execute Gradle
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% "-Dorg.gradle.appname=%APP_BASE_NAME%" -classpath "%CLASSPATH%" -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%GRADLE_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega

:ensureWrapperJar
set WRAPPER_JAR=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
if exist "%WRAPPER_JAR%" goto :eof

set PROPERTIES_FILE=%APP_HOME%\gradle\wrapper\gradle-wrapper.properties
if not exist "%PROPERTIES_FILE%" goto missingWrapperProperties

for /f "usebackq tokens=1,* delims==" %%A in ("%PROPERTIES_FILE%") do (
    if "%%A"=="distributionUrl" set DISTRIBUTION_URL=%%B
)

if "%DISTRIBUTION_URL%"=="" goto missingDistributionUrl
set DISTRIBUTION_URL=%DISTRIBUTION_URL:\:=:%

for %%A in ("%DISTRIBUTION_URL%") do set DISTRIBUTION_FILENAME=%%~nxA
set DISTRIBUTION_NOZIP=%DISTRIBUTION_FILENAME:.zip=%
set VERSION_FRAGMENT=%DISTRIBUTION_NOZIP:gradle-=%
set VERSION=%VERSION_FRAGMENT:-bin=%
set VERSION=%VERSION:-all=%
set VERSION=%VERSION:-src=%

set BUILD_DIR=%APP_HOME%\gradle\wrapper\build
set DIST_ZIP=%BUILD_DIR%\distribution.zip
set SHARED_JAR=%BUILD_DIR%\gradle-wrapper-shared.jar
set MAIN_JAR=%BUILD_DIR%\gradle-wrapper-main.jar
set CLI_JAR=%BUILD_DIR%\gradle-cli.jar
set BASE_SERVICES_JAR=%BUILD_DIR%\gradle-base-services.jar
set FILES_JAR=%BUILD_DIR%\gradle-files.jar
set EXTRACT_DIR=%BUILD_DIR%\extracted
set CLASSES_DIR=%BUILD_DIR%\classes
set MANIFEST_FILE=%BUILD_DIR%\MANIFEST.MF
set SHARED_ENTRY=gradle-%VERSION%/lib/gradle-wrapper-shared-%VERSION%.jar
set MAIN_ENTRY=gradle-%VERSION%/lib/plugins/gradle-wrapper-main-%VERSION%.jar
set CLI_ENTRY=gradle-%VERSION%/lib/gradle-cli-%VERSION%.jar
set BASE_SERVICES_ENTRY=gradle-%VERSION%/lib/gradle-base-services-%VERSION%.jar
set FILES_ENTRY=gradle-%VERSION%/lib/gradle-files-%VERSION%.jar

if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
mkdir "%BUILD_DIR%"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed
mkdir "%EXTRACT_DIR%"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed
mkdir "%CLASSES_DIR%"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed

where powershell >NUL 2>&1
if %ERRORLEVEL% neq 0 goto missingPowershell

powershell -NoLogo -NoProfile -Command "Set-StrictMode -Version Latest; $ErrorActionPreference = 'Stop'; $url = '%DISTRIBUTION_URL%'; $zip = [System.IO.Path]::GetFullPath('%DIST_ZIP%'); [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile($url, $zip)"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed

if defined JAVA_HOME (
    set JAR_EXE=%JAVA_HOME%\bin\jar.exe
) else (
    set JAR_EXE=jar.exe
)

"%JAR_EXE%" --help >NUL 2>&1
if %ERRORLEVEL% neq 0 goto missingJarTool

powershell -NoLogo -NoProfile -Command "Set-StrictMode -Version Latest; $ErrorActionPreference = 'Stop'; $zip = '%DIST_ZIP%'; $shared = '%SHARED_ENTRY%'; $main = '%MAIN_ENTRY%'; $cli = '%CLI_ENTRY%'; $base = '%BASE_SERVICES_ENTRY%'; $files = '%FILES_ENTRY%'; $sharedOut = '%SHARED_JAR%'; $mainOut = '%MAIN_JAR%'; $cliOut = '%CLI_JAR%'; $baseOut = '%BASE_SERVICES_JAR%'; $filesOut = '%FILES_JAR%'; Add-Type -AssemblyName System.IO.Compression.FileSystem; $archive = [System.IO.Compression.ZipFile]::OpenRead($zip); try { $sharedEntry = $archive.GetEntry($shared); if ($null -eq $sharedEntry) { throw \"Entry $shared not found\" }; $sharedEntry.ExtractToFile($sharedOut, $true); $mainEntry = $archive.GetEntry($main); if ($null -eq $mainEntry) { throw \"Entry $main not found\" }; $mainEntry.ExtractToFile($mainOut, $true); $cliEntry = $archive.GetEntry($cli); if ($null -eq $cliEntry) { throw \"Entry $cli not found\" }; $cliEntry.ExtractToFile($cliOut, $true); $baseEntry = $archive.GetEntry($base); if ($null -eq $baseEntry) { throw \"Entry $base not found\" }; $baseEntry.ExtractToFile($baseOut, $true); $filesEntry = $archive.GetEntry($files); if ($null -eq $filesEntry) { throw \"Entry $files not found\" }; $filesEntry.ExtractToFile($filesOut, $true) } finally { $archive.Dispose() }"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed

if not exist "%SHARED_JAR%" goto wrapperBuildFailed
if not exist "%MAIN_JAR%" goto wrapperBuildFailed
if not exist "%CLI_JAR%" goto wrapperBuildFailed
if not exist "%BASE_SERVICES_JAR%" goto wrapperBuildFailed
if not exist "%FILES_JAR%" goto wrapperBuildFailed

pushd "%CLASSES_DIR%"
"%JAR_EXE%" xf "%SHARED_JAR%"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed
"%JAR_EXE%" xf "%MAIN_JAR%"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed
"%JAR_EXE%" xf "%CLI_JAR%"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed
"%JAR_EXE%" xf "%BASE_SERVICES_JAR%"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed
"%JAR_EXE%" xf "%FILES_JAR%"
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed
popd

>"%MANIFEST_FILE%" echo Manifest-Version: 1.0
>>"%MANIFEST_FILE%" echo Main-Class: org.gradle.wrapper.GradleWrapperMain

"%JAR_EXE%" cfm "%WRAPPER_JAR%" "%MANIFEST_FILE%" -C "%CLASSES_DIR%" .
if %ERRORLEVEL% neq 0 goto wrapperBuildFailed

if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"

goto :eof

:missingWrapperProperties
echo ERROR: gradle-wrapper.properties is required to assemble gradle-wrapper.jar. 1>&2
goto fail

:missingDistributionUrl
echo ERROR: distributionUrl is missing in gradle-wrapper.properties. 1>&2
goto fail

:missingPowershell
echo ERROR: PowerShell is required to assemble gradle-wrapper.jar on Windows. 1>&2
goto fail

:missingJarTool
echo ERROR: The jar tool is required to assemble gradle-wrapper.jar. 1>&2
goto fail

:wrapperBuildFailed
echo ERROR: Failed to assemble gradle-wrapper.jar. 1>&2
goto fail
