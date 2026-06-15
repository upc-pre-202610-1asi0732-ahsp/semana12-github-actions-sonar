#!/usr/bin/env bash
set -euo pipefail

SONAR_HOST_URL="${1:-}"
SONAR_TOKEN="${2:-}"
REPORT_TASK_FILE="${3:-}"

if [ -z "$SONAR_HOST_URL" ] || [ -z "$SONAR_TOKEN" ] || [ -z "$REPORT_TASK_FILE" ]; then
  echo "Uso: wait-for-sonar-quality-gate.sh <SONAR_HOST_URL> <SONAR_TOKEN> <REPORT_TASK_FILE>"
  exit 2
fi

if [ ! -f "$REPORT_TASK_FILE" ]; then
  echo "No existe el archivo de tarea de Sonar: $REPORT_TASK_FILE"
  exit 2
fi

CE_TASK_URL="$(grep '^ceTaskUrl=' "$REPORT_TASK_FILE" | cut -d'=' -f2- || true)"

if [ -z "$CE_TASK_URL" ]; then
  echo "No se encontró ceTaskUrl dentro de $REPORT_TASK_FILE"
  cat "$REPORT_TASK_FILE"
  exit 2
fi

echo "Esperando procesamiento de SonarQube..."
analysis_id=""

for i in $(seq 1 60); do
  task_json="$(curl -s -u "$SONAR_TOKEN:" "$CE_TASK_URL")"
  ce_status="$(echo "$task_json" | jq -r '.task.status // empty')"

  echo "Intento $i - Estado CE Task: $ce_status"

  if [ "$ce_status" = "SUCCESS" ]; then
    analysis_id="$(echo "$task_json" | jq -r '.task.analysisId // empty')"
    break
  fi

  if [ "$ce_status" = "FAILED" ] || [ "$ce_status" = "CANCELED" ]; then
    echo "La tarea de SonarQube terminó con estado: $ce_status"
    echo "$task_json"
    exit 1
  fi

  sleep 5
done

if [ -z "$analysis_id" ]; then
  echo "No se pudo obtener analysisId. SonarQube no terminó dentro del tiempo esperado."
  exit 1
fi

echo "Consultando Quality Gate..."
qg_json="$(curl -s -u "$SONAR_TOKEN:" "$SONAR_HOST_URL/api/qualitygates/project_status?analysisId=$analysis_id")"
qg_status="$(echo "$qg_json" | jq -r '.projectStatus.status // empty')"

echo "Quality Gate status: $qg_status"

if [ "$qg_status" != "OK" ]; then
  echo "Quality Gate no aprobado."
  echo "$qg_json" | jq .
  exit 1
fi

echo "Quality Gate aprobado."
