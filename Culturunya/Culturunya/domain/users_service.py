from typing import List

from django.db.models import Q
from django.http import JsonResponse
from django.core import serializers
from datetime import datetime
import json
from math import radians, cos
from django.contrib.auth import get_user_model
from django.core.exceptions import ObjectDoesNotExist

from api.serializers import ReportResolutionSerializer, ReportSerializer
from persistence.models import Event, PersonalCalendar, Rating, Message, Report


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

    if "latitude" in filters and "longitude" in filters and "range" in filters:
        try:
            center_lat = float(filters["latitude"])
            center_lng = float(filters["longitude"])
            range = float(filters["range"])

            lat_range = range / 110.574
            lng_range = range / (111.320 * cos(radians(center_lat)))

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

def get_messages_admin(admin):
    return Message.objects.filter(
        Q(sender=admin) | Q(receiver=admin)
    ).order_by("date_written")

def create_report(data, user):
    try:
        rating = Rating.objects.get(id=data['rating_id'])
    except ObjectDoesNotExist:
        raise ValueError("Valoración no encontrada")
    fields = {
        "reported_user": rating.user,
        "comment": rating.comment,
        "message": data['message'],
    }
    serializer = ReportSerializer(data=fields)
    if serializer.is_valid():
        serializer.save(reporter=user)
        return {"message": "Reporte enviado correctamente"}, 201
    return {serializer.errors: serializer.errors}, 400

def create_resolved_report(data, user, report_id):
    if not user.is_admin:
        return {"error": "No autorizado"}, 403
    try:
        report = Report.objects.get(id=report_id)
    except Report.DoesNotExist:
        return {"error": "Reporte no encontrado"}, 404

    if report.is_resolved:
        return {"error": "Este reporte ya ha sido resuelto"}, 400

    serializer = ReportResolutionSerializer(data=data)
    if serializer.is_valid():
        action = data["action"]
        message = data["message"]
        if action == "Warning":
            create_message(user.id, report.reported_user.id, message)
            serializer.save(
                report=report,
                resolved_by=user,
                message=message
            )
        elif action == "Ban":
            reported_user = User.objects.get(id=report.reported_user.id)
            reported_user.is_banned = True
            reported_user.save()
            create_message(user.id, reported_user.id, message)
            serializer.save(
                report=report,
                resolved_by=user,
                message=message
            )

        report.is_resolved = True
        report.save()
        return {"message": "Report resuelto correctamente"}, 200

    return {"error": serializer.errors}, 400