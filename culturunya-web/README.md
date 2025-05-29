# Culturunya User Management

Aplicación web para gestionar usuarios de Culturunya.

## Características

- Lista de usuarios registrados
- Edición de información de usuario
- Cambio de contraseña
- Eliminación de usuarios
- Interfaz moderna y responsive

## Requisitos

- Node.js (versión 14 o superior)
- npm (incluido con Node.js)
- Cuenta de Firebase para hosting

## Instalación

1. Clona el repositorio:
```bash
git clone [URL_DEL_REPOSITORIO]
cd culturunya-web
```

2. Instala las dependencias:
```bash
npm install
```

3. Configura Firebase:
   - Crea un proyecto en Firebase Console
   - Obtén las credenciales de configuración
   - Actualiza el archivo `src/firebase.js` con tus credenciales

4. Inicia el servidor de desarrollo:
```bash
npm run dev
```

## Despliegue en Firebase

1. Instala Firebase CLI globalmente:
```bash
npm install -g firebase-tools
```

2. Inicia sesión en Firebase:
```bash
firebase login
```

3. Inicializa el proyecto:
```bash
firebase init
```

4. Construye el proyecto:
```bash
npm run build
```

5. Despliega:
```bash
firebase deploy
```

## Tecnologías utilizadas

- React
- Material-UI
- Firebase
- Axios
- React Router
