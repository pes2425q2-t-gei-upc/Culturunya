import json
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from rest_framework.decorators import api_view
from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi

from rest_framework.response import Response

from domain.users_service import get_all_events, filter_events, create_user_service, create_rating


@csrf_exempt
@api_view(["GET"])
def test_api(request):
    return JsonResponse({"message": "Testing API okay"})

@csrf_exempt
@api_view(["GET"])
def data_test(request):
    return JsonResponse({"message": "Testing API DATA"})

@csrf_exempt
@api_view(["POST"])
def post_test(request):
    if request.method == "POST":
        try:
            data = json.loads(request.body)
            return JsonResponse({"message": "POST request received", "data": data})
        except json.JSONDecodeError:
            return JsonResponse({"error": "Invalid JSON"}, status=400)
    return JsonResponse({"error": "Invalid request method"}, status=400)

@csrf_exempt
@api_view(["PUT"])
def put_test(request):
    if request.method == "PUT":
        try:
            data = json.loads(request.body)
            return JsonResponse({"message": "PUT request received", "updated_data": data})
        except json.JSONDecodeError:
            return JsonResponse({"error": "Invalid JSON"}, status=400)
    return JsonResponse({"error": "Invalid request method"}, status=400)

@csrf_exempt
@api_view(["DELETE"])
def delete_test(request):
    if request.method == "DELETE":
        return JsonResponse({"message": "Delete request received"})
    else:
        return JsonResponse({"error": "Invalid request method"}, status=400)

@csrf_exempt
@api_view(["GET"])
def get_events(request):
    if request.method == "GET":
        events = get_all_events()
        return JsonResponse({"events": events})
    else:
        return JsonResponse({"error": "Invalid request method"}, status=400)

@csrf_exempt
@swagger_auto_schema(
    method='get',
    operation_description="Filtra los eventos por categorias y/o rangos de fecha (YYYY-MM-DD).",
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
def get_filtered_events(request):
    if request.method == "GET":
        filters = request.GET.dict()
        filtered_events = filter_events(filters)
        return JsonResponse({"events": filtered_events}, safe=False)
    
    return JsonResponse({"error": "Invalid request method"}, status=400)

@csrf_exempt
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
    try:
        data = json.loads(request.body)
        username = data.get("username")
        password = data.get("password")
        email = data.get("email")

        if not all([username, password, email]):
            return JsonResponse({"error": "Todos los campos son obligatorios"}, status=400)
        user = create_user_service(data)
        # Lógica para meter los datos en la base de datos cuando tengamos la clase usuario

        return JsonResponse({"message": "Usuario creado correctamente", "username": username, "password": password, "email": email}, status=201)

    except json.JSONDecodeError:
        return JsonResponse({"error": "Formato JSON inválido"}, status=400)



@csrf_exempt
@swagger_auto_schema(
    method='post',
    operation_description="Crear un nuevo rating de usuario para un evento.",
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        required=['event_id', 'user_id', 'rating'],
        properties={
            'event_id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID del evento'),
            'user_id': openapi.Schema(type=openapi.TYPE_INTEGER, description='ID del usuario'),
            'rating': openapi.Schema(type=openapi.TYPE_STRING, description='Valoración del evento'),
            'comment': openapi.Schema(type=openapi.TYPE_STRING, description='Comentario opcional')
        }
    ),
    responses={
        201: openapi.Response(
            description="Rating creado con éxito",
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
def create_rating_endpoint(request):
    try:
        event_id = request.data['event_id']
        user_id = request.data['user_id']
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
