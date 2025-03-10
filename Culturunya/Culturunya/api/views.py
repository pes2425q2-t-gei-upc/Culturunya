from django.shortcuts import render
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt

@csrf_exempt
def test_api(request):
    return JsonResponse({"message": "Testing API okay"})
@csrf_exempt
def data_test(request):
    return JsonResponse({"message": "Testing API DATA"})
@csrf_exempt
def delete_test(request):
    if request.method == "DELETE":
        return JsonResponse({"message": "Delete request received"})
    else:
        return JsonResponse({"error": "Invalid request method"}, status=400)