#!/bin/bash

BASE_DIR=$(dirname -- "$( readlink -f -- "$0"; )")

# Verifica se pelo menos um argumento foi passado
if [ "$#" -lt 1 ]; then
  echo "Uso: $0 number_of_users"
  exit 1
fi

# chama o programa java com os arg passados para o script bash
echo "Etapa 1 - WebStatsSemMain"
time java -cp $BASE_DIR/bin/ WebStatsSemMain "$@"

echo ""
echo "Etapa 2 - WebStatsSynMain"
time java -cp $BASE_DIR/bin/ WebStatsSynMain "$@"

echo ""
echo "Etapa 3 - WebStatsAtmVarMain"
time java -cp $BASE_DIR/bin/ WebStatsAtmVarMain "$@"
