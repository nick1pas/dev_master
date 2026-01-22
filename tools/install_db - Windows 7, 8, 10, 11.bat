@echo off
title Database Installer (Windows)

REM ===== CONFIGURACAO =====
set MYSQL_BIN="C:\Program Files\MariaDB 10.4\bin\mysql.exe"
set DB_HOST=localhost
set DB_USER=root
set DB_PASS=root
set DB_NAME=l2jdb
set SQL_FOLDER=sql
REM ========================

if not exist %MYSQL_BIN% (
    echo ERRO: mysql.exe nao encontrado!
    echo Verifique o caminho em MYSQL_BIN
    pause
    exit /b
)

echo =====================================
echo DATABASE INSTALLER - WINDOWS
echo =====================================
echo.
echo [1] Instalacao COMPLETA (apaga tudo)
echo [2] Instalacao PARCIAL (sem characters)
echo [Q] Sair
echo.

set /p OPTION=Escolha uma opcao:

if "%OPTION%"=="1" goto FULL
if "%OPTION%"=="2" goto PARTIAL
if "%OPTION%"=="Q" goto END
if "%OPTION%"=="q" goto END
goto END

:FULL
echo Apagando e recriando banco...
%MYSQL_BIN% -h %DB_HOST% -u %DB_USER% -p%DB_PASS% -e "DROP DATABASE IF EXISTS %DB_NAME%; CREATE DATABASE %DB_NAME%;"
goto INSTALL

:PARTIAL
echo Instalacao parcial selecionada.
goto INSTALL

:INSTALL
echo.
echo Importando arquivos SQL...
for %%F in (%SQL_FOLDER%\*.sql) do (
    echo Importando %%F
    %MYSQL_BIN% -h %DB_HOST% -u %DB_USER% -p%DB_PASS% %DB_NAME% < %%F
)

echo.
echo INSTALACAO CONCLUIDA!
pause
goto END

:END
exit
