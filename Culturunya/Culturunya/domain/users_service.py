from django.db.models import Q
from django.http import JsonResponse
from django.core import serializers
from datetime import datetime
import json

from persistence.models import Event

def get_all_events():
    return [event.to_dict() for event in Event.objects.all()]

def filter_events(filters):
    query = Q()
    if "categories" in filters:
        categories_list = filters["categories"].split(",")
        query &= Q(categories__name__in=categories_list)
    if "date_start_range" in filters:
        try:
            date_start_range = datetime.strptime(filters["date_start_range"], "%Y-%m-%d")
            query &= Q(date_start__gte=date_start_range)
        except ValueError:
            print("Error: Formato incorrecto en date_start_range. Se esperaba YYYY-MM-DD")
    if "date_end_range" in filters:
        try:
            date_end_range = datetime.strptime(filters["date_end_range"], "%Y-%m-%d")
            query &= Q(date_start__lte=date_end_range)
        except ValueError:
            print("Error: Formato incorrecto en date_end_range. Se esperaba YYYY-MM-DD")
    events = Event.objects.filter(query).distinct()
    return [event.to_dict() for event in events]