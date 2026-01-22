#!/bin/bash

# ===== CONFIGURACAO =====
DB_HOST="localhost"
DB_USER="root"
DB_PASS="root"
DB_NAME="l2jdb"
SQL_FOLDER="sql"
MYSQL_CMD="mysql"
# ========================

clear
echo "====================================="
echo " DATABASE INSTALLER - LINUX"
echo "====================================="
echo
echo "[1] Instalacao COMPLETA (apaga tudo)"
echo "[2] Instalacao PARCIAL (sem characters)"
echo "[Q] Sair"
echo

read -p "Escolha uma opcao: " OPTION

if [[ "$OPTION" == "1" ]]; then
    echo "Apagando e recriando banco..."
    $MYSQL_CMD -h $DB_HOST -u $DB_USER -p$DB_PASS -e "DROP DATABASE IF EXISTS $DB_NAME; CREATE DATABASE $DB_NAME;"
elif [[ "$OPTION" == "2" ]]; then
    echo "Instalacao parcial selecionada."
elif [[ "$OPTION" == "Q" || "$OPTION" == "q" ]]; then
    exit 0
else
    echo "Opcao invalida."
    exit 1
fi

echo
echo "Importando arquivos SQL..."
for file in $SQL_FOLDER/*.sql; do
    echo "Importando $file"
    $MYSQL_CMD -h $DB_HOST -u $DB_USER -p$DB_PASS $DB_NAME < "$file"
done

echo
echo "INSTALACAO CONCLUIDA!"
