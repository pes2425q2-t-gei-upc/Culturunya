import json
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt

from domain.users_service import get_all_events


@csrf_exempt
def test_api(request):
    return JsonResponse({"message": "Testing API okay"})
@csrf_exempt
def data_test(request):
    return JsonResponse({"message": "Testing API DATA"})

@csrf_exempt
def post_test(request):
    if request.method == "POST":
        try:
            data = json.loads(request.body)
            return JsonResponse({"message": "POST request received", "data": data})
        except json.JSONDecodeError:
            return JsonResponse({"error": "Invalid JSON"}, status=400)
    return JsonResponse({"error": "Invalid request method"}, status=400)
@csrf_exempt
def put_test(request):
    if request.method == "PUT":
        try:
            data = json.loads(request.body)
            return JsonResponse({"message": "PUT request received", "updated_data": data})
        except json.JSONDecodeError:
            return JsonResponse({"error": "Invalid JSON"}, status=400)
    return JsonResponse({"error": "Invalid request method"}, status=400)
@csrf_exempt
def delete_test(request):
    if request.method == "DELETE":
        return JsonResponse({"message": "Delete request received"})
    else:
        return JsonResponse({"error": "Invalid request method"}, status=400)

@csrf_exempt
def get_events(request):
    if request.method == "GET":
        events = get_all_events()
        return JsonResponse({"events": events})
    else:
        return JsonResponse({"error": "Invalid request method"}, status=400)