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

from domain.users_service import get_all_events, filter_events


@csrf_exempt
@api_view(["GET"])
def test_api(request):
    return JsonResponse({"message": "Testing API okay"})

@csrf_exempt
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