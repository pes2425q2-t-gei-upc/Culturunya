#!/bin/bash

# --- Protección para evitar múltiples ejecuciones ---
LOCKFILE="/tmp/deploy.lock"

if [ -e "$LOCKFILE" ]; then
    echo "Ya se está ejecutando el script de despliegue."
    exit 1
fi

touch "$LOCKFILE"
trap 'rm -f "$LOCKFILE"' EXIT

# --- CONFIGURACIÓN ---
REPO_DIR="/home/alumne/Culturunya/Culturunya"
VENV_PATH="/home/alumne/Culturunya/Culturunya/Culturunya/Culturunya/venv"
PYTHON_CMD="$VENV_PATH/bin/python"
PIP_CMD="$VENV_PATH/bin/pip"
BRANCH="DEV_Back"
SERVER_IP="0.0.0.0"
SERVER_PORT="8089"
MAIL_SUBJECT_ERROR="Error en despliegue Django"
MAIL_SUBJECT_SUCCESS="Despliegue Django Exitoso"
MAIL_RECIPIENTS="richard.pie@estudiantat.upc.edu martin.viteri@estudiantat.upc.edu"

# Emails Region
function send_error_mail() {
    local error_message="$1"
    echo "$error_message" | mail -s "$MAIL_SUBJECT_ERROR" $MAIL_RECIPIENTS
}

function send_success_mail() {
    local success_message="$1"
    echo "$success_message" | mail -s "$MAIL_SUBJECT_SUCCESS" $MAIL_RECIPIENTS
}

# Logic Region
REPO_DIR="/home/alumne/Culturunya/Culturunya"
cd "$REPO_DIR" || {
    send_error_mail "No se pudo acceder al directorio $REPO_DIR"
    exit 1
}

# Hacemos fetch para comprobar si hay nuevos commits
git fetch origin "$BRANCH" 2>/dev/null || {
    send_error_mail "Error al hacer git fetch en la rama $BRANCH"
    exit 1
}

# Comparamos commits local vs remoto
LOCAL_COMMIT=$(git rev-parse "$BRANCH")
REMOTE_COMMIT=$(git rev-parse "origin/$BRANCH")

if [ "$LOCAL_COMMIT" != "$REMOTE_COMMIT" ]; then
    # Hay cambios, procedemos

    # 1. Detener procesos runserver del entorno virtual
    P_IDS=$(pgrep -f "$PYTHON_CMD manage.py runserver")
    if [ -n "$P_IDS" ]; then
        for PID in $P_IDS; do
            kill "$PID" || {
                send_error_mail "No se pudo detener el proceso runserver con PID: $PID"
                exit 1
            }
        done
        sleep 2
    fi

    # 2. Hacer pull
    git pull origin "$BRANCH" || {
        send_error_mail "Error al hacer git pull en la rama $BRANCH"
        exit 1
    }

    REPO_DIR="/home/alumne/Culturunya/Culturunya/Culturunya/Culturunya"
    cd "$REPO_DIR" || {
        send_error_mail "No se pudo acceder al directorio $REPO_DIR"
        exit 1
    }

    # 3. Instalar dependencias
    $PIP_CMD install -r /home/alumne/Culturunya/Culturunya/Culturunya/Culturunya/requirements.txt || {
        send_error_mail "Error instalando dependencias con requirements.txt"
        exit 1
    }

    # 4. Aplicar migraciones
    $PYTHON_CMD manage.py migrate || {
        send_error_mail "Error aplicando migraciones con manage.py migrate"
        exit 1
    }

    # 5. Levantar servidor en segundo plano
    nohup $PYTHON_CMD manage.py runserver "$SERVER_IP:$SERVER_PORT" > output.log 2>&1 &

    # 6. Enviar correo de éxito
    send_success_mail "El despliegue de la app Django se ha realizado correctamente. El servidor se ha iniciado en $SERVER_IP:$SERVER_PORT."
else
    # No hay cambios
    exit 0
fi
