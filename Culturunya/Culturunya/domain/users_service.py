from django.db.models import Q

from persistence.models import Event

def get_all_events():
    #Obtiene todos los eventos de la base de datos.
    return list(Event.objects.all())

def filter_events(filters):
    query = Q()
    if "category" in filters:
        query &= Q(category__category=filters["category"])
    if "date_start" in filters:
        query &= Q(date_start__gte=filters["date_start"])
    if "date_end" in filters:
        query &= Q(date_end__lte=filters["date_end"])
    events = Event.objects.filter(query)
    return list(events)