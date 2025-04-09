import json
from django.http import JsonResponse
from django.views.decorators.http import require_POST
from rest_framework import status
from rest_framework.response import Response

# DRF / Auth
from rest_framework.decorators import (
    api_view, authentication_classes, permission_classes
)
from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated

# DRF Token login
from rest_framework.authtoken.views import ObtainAuthToken
from rest_framework.authtoken.models import Token

# Swagger
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi

# Services
from domain.users_service import get_all_events, filter_events, create_user_service, create_rating


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
        response = super().post(request, *args, **kwargs)
        token = Token.objects.get(key=response.data['token'])
        user = token.user

        return Response({
            'token': token.key,
            'user_id': user.id,
            'email': user.email
        })


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
    operation_description="Obtiene todos los eventos. Requiere token.",
    security=[{'Token': []}],  # Esto le dice a Swagger que requiere autenticacion por token
    responses={
        200: openapi.Response(description="Lista de eventos"),
        401: openapi.Response(description="No autorizado - token no valido o faltante"),
    }
)

@api_view(["GET"])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def get_events(request):
    if request.method == "GET":
        events = get_all_events()
        return JsonResponse({"events": events})
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
