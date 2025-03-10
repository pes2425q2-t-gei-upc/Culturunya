from django.shortcuts import render
from django.http import JsonResponse

def test_api(request):
    return JsonResponse({"message": "Testing API okay"})

def data_test(request):
    return JsonResponse({"message": "Testing API DATA"})

def delete_test(request):
    if request.method == "DELETE":
        return JsonResponse({"message": "Delete request received"})
    else:
        return JsonResponse({"error": "Invalid request method"}, status=400)