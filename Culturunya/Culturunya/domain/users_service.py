from typing import List

from django.db.models import Q
from django.http import JsonResponse
from django.core import serializers
from datetime import datetime
import json
from math import radians, cos
from django.contrib.auth import get_user_model
from django.core.exceptions import ObjectDoesNotExist
from persistence.models import Event, PersonalCalendar, Rating, Message


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

    if "latitude" in filters and "longitude" in filters and "radius_km" in filters:
        try:
            center_lat = float(filters["latitude"])
            center_lng = float(filters["longitude"])
            radius_km  = float(filters["radius_km"])

            lat_range = radius_km / 110.574
            lng_range = radius_km / (111.320 * cos(radians(center_lat)))

            min_lat = center_lat - lat_range
            max_lat = center_lat + lat_range
            min_lng = center_lng - lng_range
            max_lng = center_lng + lng_range

            query &= (
                Q(location__latitude__range=(min_lat, max_lat)) &
                Q(location__longitude__range=(min_lng, max_lng))
            )

        except ValueError:
            print("Error: lat/long/radius_km deben ser valores numericos")
    events = Event.objects.filter(query).distinct()
    return [event.to_dict() for event in events]


User = get_user_model()

def create_user_service(data):

    username = data.get('username')
    password = data.get('password')
    email = data.get('email')
    fullname = data.get('fullname', '')         
    phone_number = data.get('phonenumber', None)
    birth_date = data.get('birthdate', None)    
    language = data.get('language', 'ES')

    # Crea instancia del modelo
    user = User(
        username=username,
        email=email,
        fullname=fullname,
        phone_number=phone_number,
        birth_date=birth_date,
        language=language,
    )
    # Manejo de password para que se guarde hasheado
    user.set_password(password)
    user.save()
    PersonalCalendar.objects.create(user=user)
    return user

def create_rating(event_id: int, user_id: int, rating: str, comment: str = None) -> Rating:
    try:
        event = Event.objects.get(id=event_id)
        user = User.objects.get(id=user_id)
    except ObjectDoesNotExist:
        raise ValueError("Usuario o evento no encontrado")

    return Rating.objects.create(
        event=event,
        user=user,
        rating=rating,
        comment=comment
    )

def create_message(sender_id: int, receiver_id: int, text: str) -> Message:
    try:
        sender = User.objects.get(id=sender_id)
        receiver = User.objects.get(id=receiver_id)
    except ObjectDoesNotExist:
        raise ValueError("Usuario o admin no encontrado")
    return Message.objects.create(
        sender=sender,
        receiver=receiver,
        text=text
    )

def get_messages(user1: int, user2: int):
    try:
        user1 = User.objects.get(id=user1)
        user2 = User.objects.get(id=user2)
    except ObjectDoesNotExist:
        raise ValueError("Usuario o admin no encontrado")
    return Message.objects.filter(
        Q(sender=user1, receiver=user2) | Q(sender=user2, receiver=user1)
    ).order_by("date_written")