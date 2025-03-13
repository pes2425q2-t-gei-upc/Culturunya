from persistence.models import Event

def get_all_events():
    #Obtiene todos los eventos de la base de datos.
    return list(Event.objects.all())