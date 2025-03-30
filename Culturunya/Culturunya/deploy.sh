#!/bin/bash

# --- CONFIGURACIÓN ---
REPO_DIR="~/Culturunya/Culturunya" 
BRANCH="DEV_Back"                  
SERVER_IP="0.0.0.0"
SERVER_PORT="8089"
PYTHON_CMD="~/Culturunya/Culturunya/Culturunya/Culturunya/venv/bin/python"
PIP_CMD="~/Culturunya/Culturunya/Culturunya/Culturunya/venv/bin/pip"      
MAIL_SUBJECT_ERROR="Error en despliegue Django"
MAIL_SUBJECT_SUCCESS="Despliegue Django Exitoso"
MAIL_RECIPIENTS="richard.pie@estudiantat.upc.edu martin.viteri@estudiantat.upc.edu"

#Emails Region
function send_error_mail() {
    local error_message="$1"
    echo "$error_message" | mail -s "$MAIL_SUBJECT_ERROR" $MAIL_RECIPIENTS
}

function send_success_mail() {
    local success_message="$1"
    echo "$success_message" | mail -s "$MAIL_SUBJECT_SUCCESS" $MAIL_RECIPIENTS
}

#Logic Region
cd "$REPO_DIR" || {
    send_error_mail "No se pudo acceder al directorio $REPO_DIR"
    exit 1
}

#Hacemos fetch de la rama remota para comprobar si hay commits nuevos
git fetch origin "$BRANCH" 2>/dev/null || {
    send_error_mail "Error al hacer git fetch en la rama $BRANCH"
    exit 1
}

#Comparamos commits local vs remoto
LOCAL_COMMIT=$(git rev-parse "$BRANCH")
REMOTE_COMMIT=$(git rev-parse "origin/$BRANCH")

if [ "$LOCAL_COMMIT" != "$REMOTE_COMMIT" ]; then
    # Hay cambios, así que procedemos con la actualización

    # 2.1. Detener el runserver en segundo plano si está corriendo
    P_ID=$(pgrep -f "manage.py runserver $SERVER_IP:$SERVER_PORT")
    if [ -n "$P_ID" ]; then
        kill "$P_ID" || {
            send_error_mail "No se pudo detener el proceso runserver con PID $P_ID"
            exit 1
        }
        # Damos unos segundos para asegurarnos de que se detenga
        sleep 2
    fi

    # 2.2. Hacer git pull para actualizar a la última versión
    git pull origin "$BRANCH" || {
        send_error_mail "Error al hacer git pull en la rama $BRANCH"
        exit 1
    }

    # 2.3. Instalar dependencias
    $PIP_CMD install -r requirements.txt || {
        send_error_mail "Error instalando dependencias con requirements.txt"
        exit 1
    }

    # 2.4. Aplicar migraciones
    $PYTHON_CMD manage.py migrate || {
        send_error_mail "Error aplicando migraciones con manage.py migrate"
        exit 1
    }

    # 2.5. Levantar el servidor en segundo plano
    nohup $PYTHON_CMD manage.py runserver "$SERVER_IP:$SERVER_PORT" > /dev/null 2>&1 &

    # 2.6. Si todo ha salido bien, enviamos correo de éxito
    send_success_mail "El despliegue de la app Django se ha realizado correctamente. El servidor se ha iniciado en $SERVER_IP:$SERVER_PORT."
else
    # No hay cambios, no hacemos nada.
    exit 0
fi