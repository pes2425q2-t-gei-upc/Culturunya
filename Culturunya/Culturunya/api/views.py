from django.shortcuts import render
from django.http import JsonResponse

def test_api(request):
    return JsonResponse({"message": "Testing API okay"})
def data_test(request):
    return JsonResponse({"message": "Testing API DATA"})
