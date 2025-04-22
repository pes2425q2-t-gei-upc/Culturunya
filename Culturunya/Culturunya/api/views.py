import json

from django.http import JsonResponse

# DRF / Auth
from rest_framework.decorators import (
    api_view, authentication_classes, permission_classes
)
from rest_framework.authentication import TokenAuthentication

# DRF Token login
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token
from rest_framework.views import APIView
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework import status

# Swagger
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi

from api.serializers import UserProfileSerializer, ChangePasswordSerializer
# Services
from domain.users_service import get_all_events, filter_events, create_user_service, create_rating, create_message, \
    get_messages
from persistence.models import User


#
# LOGIN
#
class CustomObtainAuthToken(ObtainAuthToken):
    """
    Endpoint para hacer login con username/password y recibir un token
    """
    @swagger_auto_schema(
        operation_description="Login con usuario y contrasena para obtener un token de autenticacion.",
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            required=['username', 'password'],
            properties={
                'username': openapi.Schema(type=openapi.TYPE_STRING),
                'password': openapi.Schema(type=openapi.TYPE_STRING),
            },
        ),
        responses={
            200: openapi.Response(
                description="Login exitoso, token devuelto",
                examples={
                    "application/json": {
                        "token": "abcd1234...",
                        "user_id": 1,
                        "username": "usuario",
                        "email": "usuario@ejemplo.com"
                    }
                }
            ),
            400: "Credenciales invalidas"
        },
        # No ponemos security aqui, pues es publico (el usuario no tiene token aun).
    )
    def post(self, request, *args, **kwargs):
        serializer = self.serializer_class(data=request.data,
                                           context={'request': request})
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']
        token, created = Token.objects.get_or_create(user=user)
        return Response({
            'token': token.key,
            'user_id': user.id,
            'email': user.email
        })

@swagger_auto_schema(
    method='post',
    operation_description="Cierra la sesión del usuario autenticado (requiere token).",
    security=[{'Token': []}],
    responses={
        200: openapi.Response(description="Sesión cerrada exitosamente"),
        400: openapi.Response(description="Sesión no existente o ya eliminada"),
    }
)
@api_view(['POST'])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def logout_view(request):
    """
    Logout del usuario actual invalidando su token.
    """
    try:
        request.user.auth_token.delete()
    except (AttributeError, Token.DoesNotExist):
        return Response({"error": "Sesión no existente o ya eliminada"}, status=status.HTTP_400_BAD_REQUEST)

    return Response({"message": "Sesión cerrada exitosamente"}, status=status.HTTP_200_OK)


#
# ENDPOINTS ABIERTOS (no requieren token)
#
@swagger_auto_schema(
    method='post',
    operation_description="Crea un nuevo usuario con nombre, password y correo.",
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=['username', 'password', 'email'],
        properties={
            'username': openapi.Schema(type=openapi.TYPE_STRING, description="Nombre de usuario"),
            'password': openapi.Schema(type=openapi.TYPE_STRING, description="Password"),
            'email': openapi.Schema(type=openapi.TYPE_STRING, format=openapi.FORMAT_EMAIL, description="Correo electronico"),
        },
    ),
    responses={201: "Usuario creado", 400: "Datos invalidos"},
)
@api_view(["POST"])
def create_user(request):
    """
    Endpoint publico para registrar usuario (no requiere token).
    """
    try:
        data = json.loads(request.body)
        username = data.get("username")
        password = data.get("password")
        email = data.get("email")

        if not all([username, password, email]):
            return JsonResponse({"error": "Todos los campos son obligatorios"}, status=400)
        user = create_user_service(data)
        return JsonResponse({
            "message": "Usuario creado correctamente", 
            "username": username, 
            "email": email
        }, status=201)

    except json.JSONDecodeError:
        return JsonResponse({"error": "Formato JSON invalido"}, status=400)

@swagger_auto_schema(
    method='get',
    operation_description="Obtiene todos los eventos. No requiere token.",
    responses={
        200: openapi.Response(description="Lista de eventos"),
        400: openapi.Response(description="Error en la solicitud"),
    }
)

@api_view(["GET"])
def get_events(request):
    if request.method == "GET":
        events = get_all_events()
        return JsonResponse({"events": events})
    else:
        return JsonResponse({"error": "Invalid request method"}, status=400)



#
# ENDPOINTS PROTEGIDOS (requieren token)
#


@swagger_auto_schema(
    method='get',
    operation_description="Test endpoint que requiere token.",
    security=[{'Token': []}],
    responses={200: openapi.Response(description="OK")},
)
@api_view(["GET"])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def test_api(request):
    return JsonResponse({"message": "Testing API okay"})


@swagger_auto_schema(
    method='get',
    operation_description="Devuelve data test, requiere token.",
    security=[{'Token': []}],
    responses={200: openapi.Response(description="OK")},
)
@api_view(["GET"])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def data_test(request):
    return JsonResponse({"message": "Testing API DATA"})


@swagger_auto_schema(
    method='post',
    operation_description="POST test, requiere token.",
    security=[{'Token': []}],
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        additional_properties=openapi.Schema(type=openapi.TYPE_STRING),
    ),
    responses={200: openapi.Response(description="OK")},
)
@api_view(["POST"])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def post_test(request):
    if request.method == "POST":
        try:
            data = json.loads(request.body)
            return JsonResponse({"message": "POST request received", "data": data})
        except json.JSONDecodeError:
            return JsonResponse({"error": "Invalid JSON"}, status=400)
    return JsonResponse({"error": "Invalid request method"}, status=400)


@swagger_auto_schema(
    method='put',
    operation_description="PUT test, requiere token.",
    security=[{'Token': []}],
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        additional_properties=openapi.Schema(type=openapi.TYPE_STRING),
    ),
    responses={200: openapi.Response(description="OK")},
)
@api_view(["PUT"])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def put_test(request):
    if request.method == "PUT":
        try:
            data = json.loads(request.body)
            return JsonResponse({"message": "PUT request received", "updated_data": data})
        except json.JSONDecodeError:
            return JsonResponse({"error": "Invalid JSON"}, status=400)
    return JsonResponse({"error": "Invalid request method"}, status=400)


@swagger_auto_schema(
    method='delete',
    operation_description="DELETE test, requiere token.",
    security=[{'Token': []}],
    responses={200: openapi.Response(description="OK")},
)
@api_view(["DELETE"])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def delete_test(request):
    if request.method == "DELETE":
        return JsonResponse({"message": "Delete request received"})
    else:
        return JsonResponse({"error": "Invalid request method"}, status=400)

@swagger_auto_schema(
    method='get',
    operation_description="Filtra los eventos por categorias y/o fechas. Requiere token.",
    security=[{'Token': []}],
    manual_parameters=[
        openapi.Parameter(
            'categories',
            openapi.IN_QUERY,
            description="Lista separada por comas (p. ej. 'Musica,Arte')",
            type=openapi.TYPE_STRING,
            required=False
        ),
        openapi.Parameter(
            'date_start_range',
            openapi.IN_QUERY,
            description="Fecha de inicio (formato YYYY-MM-DD)",
            type=openapi.TYPE_STRING,
            required=False
        ),
        openapi.Parameter(
            'date_end_range',
            openapi.IN_QUERY,
            description="Fecha de fin (formato YYYY-MM-DD)",
            type=openapi.TYPE_STRING,
            required=False
        ),
        openapi.Parameter(
            'longitude',
            openapi.IN_QUERY,
            description="longitude={longitude value}",
            type=openapi.TYPE_STRING,
            required=False
        ),
        openapi.Parameter(
            'latitude',
            openapi.IN_QUERY,
            description="latitude={latitude value}",
            type=openapi.TYPE_STRING,
            required=False
        ),
        openapi.Parameter(
            'range',
            openapi.IN_QUERY,
            description="range={range value in km}",
            type=openapi.TYPE_STRING,
            required=False
        ),
    ],
    responses={
        200: openapi.Response(description="Lista de eventos filtrados"),
        400: "Parametros invalidos",
    }
)
@api_view(["GET"])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def get_filtered_events(request):
    if request.method == "GET":
        filters = request.GET.dict()
        filtered_events = filter_events(filters)
        return JsonResponse({"events": filtered_events}, safe=False)
    return JsonResponse({"error": "Invalid request method"}, status=400)


@swagger_auto_schema(
    method='post',
    operation_description="Crear un nuevo rating de usuario para un evento (requiere token).",
    security=[{'Token': []}],
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=['event_id', 'rating'],
        properties={
            'event_id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID del evento'),
            'rating': openapi.Schema(type=openapi.TYPE_STRING, description='Valoracion del evento'),
            'comment': openapi.Schema(type=openapi.TYPE_STRING, description='Comentario opcional')
        }
    ),
    responses={
        201: openapi.Response(
            description="Rating creado con exito",
            schema=openapi.Schema(
                type=openapi.TYPE_OBJECT,
                properties={
                    'id': openapi.Schema(type=openapi.TYPE_INTEGER),
                    'event_id': openapi.Schema(type=openapi.TYPE_INTEGER),
                    'user_id': openapi.Schema(type=openapi.TYPE_INTEGER),
                    'rating': openapi.Schema(type=openapi.TYPE_STRING),
                    'comment': openapi.Schema(type=openapi.TYPE_STRING),
                    'date': openapi.Schema(type=openapi.TYPE_STRING, format='date'),
                }
            )
        ),
        400: openapi.Response(description="Error en la solicitud")
    }
)
@api_view(['POST'])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def create_rating_endpoint(request):
    """
    Creacion de rating para un evento, requiere token.
    """
    try:
        user = request.user  # del token
        event_id = request.data['event_id']
        user_id = user.id
        rating = request.data['rating']
        comment = request.data.get('comment', None)

        rating_obj = create_rating(event_id, user_id, rating, comment)
        return Response({
            "id": rating_obj.id,
            "event_id": rating_obj.event.id,
            "user_id": rating_obj.user.id,
            "rating": rating_obj.rating,
            "comment": rating_obj.comment,
            "date": rating_obj.date
        }, status=status.HTTP_201_CREATED)

    except KeyError as e:
        return Response({"error": f"Falta el campo obligatorio: {str(e)}"}, status=status.HTTP_400_BAD_REQUEST)
    except ValueError as e:
        return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)

@swagger_auto_schema(
    method='delete',
    operation_description="Permite que un usuario autenticado elimine su propia cuenta.",
    security=[{'Token': []}],
        responses={
            204: "Cuenta eliminada exitosamente.",
            401: "No autenticado.",
        }
)
@api_view(["DELETE"])
@permission_classes([IsAuthenticated])
def delete_own_account(request):
    user = request.user
    username = user.username
    user.delete()
    return Response({"message": f"Cuenta '{username}' eliminada correctamente."}, status=status.HTTP_200_OK)



@swagger_auto_schema(
    method='post',
    operation_description="Enviar un mensaje al admin.",
    security=[{'Token': []}],
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=['text'],
        properties={
            'text': openapi.Schema(type=openapi.TYPE_STRING, description='contenido del mensaje'),
        }
    ),
    responses={201: "Mensaje enviado", 401: "No autenticado", 404: "Admin no encontrado"}
)
@api_view(["POST"])
@permission_classes([IsAuthenticated])
def send_message_user_to_admin(request):
    try:
        data = json.loads(request.body)
        text = data['text']
        user = User.objects.get(id=request.user.id)
        if user.is_admin:
            return Response({"error": "Un administrador no usa este endpoint"}, status=403)
        # Buscar primer administrador disponible
        admin = User.objects.filter(is_admin=True).first()
        create_message(user.id, admin.id, text)

        return Response({"message": "Mensaje enviado con éxito"}, status=201)

    except Exception as e:
        return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)

@swagger_auto_schema(
    method="post",
    operation_summary="Admin envía mensaje a un usuario",
    security=[{'Token': []}],
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=["receiver_id", "text"],
        properties={
            "receiver_id": openapi.Schema(type=openapi.TYPE_INTEGER, description="ID del usuario destinatario"),
            "text": openapi.Schema(type=openapi.TYPE_STRING, description="Contenido del mensaje"),
        },
    ),
    responses={201: "Mensaje enviado", 401: "No autenticado", 404: "Usuario no encontrado"}
)
@api_view(["POST"])
@permission_classes([IsAuthenticated])  # Puedes hacer una permission extra que restrinja solo a admins si quieres
def send_message_admin_to_user(request):
    try:
        data = json.loads(request.body)
        receiver_id = data.get("receiver_id")
        text = data.get("text")
        user = User.objects.get(id=request.user.id)

        if not user.is_admin:
            return Response({"error": "Solo los administradores pueden enviar mensajes desde este endpoint"},
                            status=403)

        receiver = User.objects.get(id=receiver_id)
        create_message(user.id, receiver.id, text)

        return Response({"message": "Mensaje enviado con éxito"}, status=201)

    except Exception as e:
        return Response({"error": str(e)}, status=400)

@swagger_auto_schema(
    method="get",
    operation_summary="Obtener el chat con el admin",
    security=[{'Token': []}],
    responses={
        200: openapi.Response(
            description="Lista de mensajes entre usuario y admin",
            schema=openapi.Schema(
                type=openapi.TYPE_ARRAY,
                items=openapi.Schema(
                    type=openapi.TYPE_OBJECT,
                    properties={
                        'from': openapi.Schema(type=openapi.TYPE_STRING, description="Username del que envia el mensaje, en el caso del admin es 'Administrador'"),
                        'to': openapi.Schema(type=openapi.TYPE_STRING, description="Username del que recibe el mensaje, en el caso del admin es 'Administrador'"),
                        'text': openapi.Schema(type=openapi.TYPE_STRING, description="Contenido del mensaje"),
                        'date': openapi.Schema(type=openapi.TYPE_STRING, format='date')
                    }
                )
            )
        ),
        403: openapi.Response(description="Un admin no usa este endpoint"),
        404: openapi.Response(description="No hay administradores disponibles"),
    }
)
@api_view(["GET"])
@permission_classes([IsAuthenticated])
def get_conversation_with_admin(request):
    user = User.objects.get(id=request.user.id)

    if user.is_admin:
        return Response({"error": "Un administrador no usa este endpoint"}, status=403)

    # Buscar el primer admin (representante del "soporte")
    admin = User.objects.filter(is_admin=True).first()
    if not admin:
        return Response({"error": "No hay administradores disponibles"}, status=404)

    messages = get_messages(user.id, admin.id)

    result = [{
        "from": "Administrador" if msg.sender.is_admin else msg.sender.username,
        "to": "Administrador" if msg.receiver.is_admin else msg.receiver.username,
        "text": msg.text,
        "date": msg.date_written,
    } for msg in messages]

    return Response(result)

@swagger_auto_schema(
    method="get",
    operation_summary="Obtener el chat con el user",
    security=[{'Token': []}],
    responses={
        200: openapi.Response(
            description="Chat obtenido",
            schema=openapi.Schema(
                type=openapi.TYPE_ARRAY,
                items=openapi.Schema(
                    type=openapi.TYPE_OBJECT,
                    properties={
                            'from': openapi.Schema(type=openapi.TYPE_STRING, description="Username del que envia el mensaje"),
                            'to': openapi.Schema(type=openapi.TYPE_STRING, description="Username del que recibe el mensaje"),
                            'text': openapi.Schema(type=openapi.TYPE_STRING, description="Contenido del mensaje"),
                            'date': openapi.Schema(type=openapi.TYPE_STRING, format='date')
                    }
                )
            )
        ),
        403: openapi.Response(description="Solo los admins pueden acceder a este endpoint"),
        404: openapi.Response(description="Usuario no encontrado"),
    }
)
@api_view(["GET"])
@permission_classes([IsAuthenticated])
def get_conversation_with_user(request, user_id):
    admin = User.objects.get(id=request.user.id)

    if not admin.is_admin:
        return Response({"error": "Solo los administradores pueden acceder a este recurso"}, status=403)

    messages = get_messages(admin.id, user_id)

    result = [{
        "from": msg.sender.username,
        "to": msg.receiver.username,
        "text": msg.text,
        "date": msg.date_written,
    } for msg in messages]

    return Response(result)


class ChangePasswordView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        request_body=ChangePasswordSerializer,
        operation_summary="Cambiar contraseña",
        operation_description="Permite a un usuario autenticado cambiar su contraseña actual.",
        responses={
            200: "Contraseña cambiada exitosamente.",
            400: "Error de validación o contraseña actual incorrecta.",
            401: "No autenticado.",
        }
    )
    def put(self, request):
        serializer = ChangePasswordSerializer(data=request.data)
        if serializer.is_valid():
            user = request.user
            if not user.check_password(serializer.validated_data['old_password']):
                return Response({"detail": "La contraseña actual es incorrecta."}, status=status.HTTP_400_BAD_REQUEST)
            user.set_password(serializer.validated_data['new_password'])
            user.save()
            return Response({"detail": "Contraseña cambiada correctamente."}, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class UserProfileView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        operation_summary="Obtener información del usuario autenticado",
        responses={200: UserProfileSerializer()}
    )
    def get(self, request):
        serializer = UserProfileSerializer(request.user)
        return Response(serializer.data)


@swagger_auto_schema(
    method="put",
    operation_summary="Actualizar idioma del usuario",
    operation_description="Permite a un usuario autenticado cambiar su idioma preferido. Solo se aceptan los valores 'ES' (Español) y 'EN' (English).",
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=["language"],
        properties={
            "language": openapi.Schema(
                type=openapi.TYPE_STRING,
                description="Código del idioma ('ES' o 'EN')",
                enum=["ES", "EN"]
            ),
        }
    ),
    responses={
        200: openapi.Response(
            description="Idioma actualizado correctamente",
            examples={
                "application/json": {
                    "message": "Idioma actualizado correctamente",
                    "language": "ES"
                }
            }
        ),
        400: "Idioma no válido",
        401: "No autenticado",
        500: "Error del servidor"
    },
    security=[{"Token": []}],
)
@api_view(["PUT"])
@permission_classes([IsAuthenticated])
def update_language(request):
    try:
        data = json.loads(request.body)
        new_language = data.get("language")

        if new_language not in ["ES", "EN"]:
            return Response({"error": "Idioma no valido"}, status=400)

        user = request.user
        user.language = new_language
        user.save()

        return Response({"message": "Idioma actualizado correctamente", "language": new_language}, status=200)
    except Exception as e:
        return Response({"error": str(e)}, status=500)


@swagger_auto_schema(
    method="put",
    operation_summary="Cambiar nombre de usuario",
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=["username"],
        properties={
            "username": openapi.Schema(
                type=openapi.TYPE_STRING,
                description="nombre del usuario",
            ),
        }
    ),
    responses={
        200: "Nombre de usuario actualizado correctamente",
        401: "No autenticado",
        500: "Error del servidor",
    },
    security=[{"Token": []}],
)
@api_view(["PUT"])
@permission_classes([IsAuthenticated])
def update_username(request):
    try:
        data = json.loads(request.body)
        new_username = data.get("username")
        user = request.user
        user.username = new_username
        user.save()

        return Response({"message": "Nombre de usuario actualizado correctamente"}, status=200)
    except Exception as e:
        return Response({"error": str(e)}, status=500)
