# -*- coding: utf-8 -*-
import requests
import json
import datetime
import re
import unicodedata
from django.utils import timezone
from dateutil import parser as date_parser
from persistence.models import Location, Event, Category
from django.core.files.base import ContentFile
from typing import Optional

BASE_IMG_URL = "https://agenda.cultura.gencat.cat"
today = datetime.date.today().strftime("%Y-%m-%d")
URL = (
    "https://analisi.transparenciacatalunya.cat/resource/rhpv-yr4f.json?"
    "$select=codi,latitud,longitud,adre_a,municipi,denominaci,data_inici,data_fi,gratuita,entrades,descripcio,tags_mbits,imatges"
    f"&$where=latitud IS NOT NULL AND "
    f"longitud IS NOT NULL AND "
    f"adre_a IS NOT NULL AND adre_a <> '' AND "
    f"municipi IS NOT NULL AND municipi <> '' AND "
    f"denominaci IS NOT NULL AND denominaci <> '' AND "
    f"data_inici IS NOT NULL AND data_inici >= '{today}T00:00:00' AND "
    f"data_fi IS NOT NULL AND data_fi <= '2028-01-01T00:00:00' AND "
    f"gratuita IS NOT NULL AND gratuita <> '' AND "
    f"entrades IS NOT NULL AND entrades <> '' AND "
    f"descripcio IS NOT NULL AND descripcio <> '' AND "
    f"tags_mbits IS NOT NULL AND tags_mbits <> ''"
    "&$order=data_inici ASC"
)

def _get_first_image_path(imatges_field: Optional[str]) -> Optional[str]:
    if not imatges_field:
        return None
    return imatges_field.split(",")[0].strip()


def obtener_precio(entrades: str, gratuita: str = None) -> float:
    if not entrades:
        return 0.0
    print(gratuita)
    if gratuita and "s" in gratuita:
        return 0.0
    texto_lower = entrades.lower()

    if "gratis" in texto_lower or "gratu" in texto_lower:
        return 0.0

    patron_precio = re.compile(
        r"(?:preu\s*:\s*)?(\d+(?:[\.,]\d+)?)(?=\s*\u20AC|\s|\$)",
        re.IGNORECASE
    )
    coincidencia = patron_precio.search(entrades)
    if coincidencia:
        valor = coincidencia.group(1).replace(",", ".")
        try:
            return float(valor)
        except ValueError:
            pass
    return 0.0


def extraer_datos(municipi):
    match = re.search(r'agenda:ubicacions/([^/]+)/([^/]+)/([^/]+)', municipi)
    if match:
        return {
            "province": match.group(1).replace("-", " "),
            "comarca": match.group(2).replace("-", " "),
            "city": match.group(3).replace("-", " ")
        }
    return {"province": None, "comarca": None, "city": None}

def extraer_categorias(tags_mbits):
    if not tags_mbits:
        return []
    categorias = []
    for tag in tags_mbits.split(","):
        parte_limpia = tag.split("/")[-1].replace("-", " ").strip()
        if parte_limpia:
            categorias.append(parte_limpia)
    return categorias

def obtener_eventos_filtrados():
    try:
        response = requests.get(URL)
        response.raise_for_status()
        return response.json()

    except requests.exceptions.HTTPError as http_err:
        print(f"Error HTTP: {http_err}")
        return []

    except requests.exceptions.RequestException as req_err:
        print(f"Error en la solicitud: {req_err}")
        return None



def procesar_eventos_y_guardar():

    eventos = obtener_eventos_filtrados()

    for e in eventos:
        try:
            
            event_id = e.get("codi")
            name = e.get("denominaci")
            data_inici = e.get("data_inici")
            data_fi = e.get("data_fi")
            descripcio = e.get("descripcio", "")
            gratuita = e.get("gratuita", "")
            entrades = e.get("entrades", "")

            try:
                date_start = date_parser.isoparse(data_inici)
            except:
                date_start = timezone.now()

            try:
                date_end = date_parser.isoparse(data_fi)
            except:
                date_end = date_start


            price_value = obtener_precio(entrades, gratuita)


            latitud = e.get("latitud")
            longitud = e.get("longitud")
            address = e.get("adre_a", "")
            dict_loc = extraer_datos(e.get("municipi", ""))


            loc, _ = Location.objects.get_or_create(
                longitude=float(longitud),
                latitude=float(latitud),
                defaults={
                    "address": address,
                    "city": dict_loc["city"] or "",
                    "comarca": dict_loc["comarca"] or "",
                    "province": dict_loc["province"] or "",
                }
            )

            img_path = _get_first_image_path(e.get("imatges"))
            full_img_url = f"{BASE_IMG_URL}{img_path}" if img_path else None

            event_obj, created = Event.objects.update_or_create(
                id=event_id,
                defaults={
                    "name": name,
                    "date_start": date_start,
                    "date_end": date_end,
                    "description": descripcio,
                    "price": price_value,
                    "location": loc
                }
            )
            if full_img_url and (not event_obj.image or event_obj.image.name == ""):
                try:
                    resp = requests.get(full_img_url, timeout=10)
                    resp.raise_for_status()
                    event_obj.image.save(
                        f"{event_id}.jpg",
                        ContentFile(resp.content),
                        save=True
                    )
                except Exception as ex:
                    print(f"No se pudo descargar imagen de {event_id}: {ex}")


            categorias = extraer_categorias(e.get("tags_mbits", ""))
            if categorias:

                event_obj.categories.clear()
                for cat_name in categorias:
                    cat = Category.get_or_create_category(cat_name)
                    event_obj.categories.add(cat)

            print(f"Evento [{event_id}] procesado. Creado={created}")
        except Exception as ex:
            print(f"Error procesando evento {e.get('codi')}: {ex}")

    print(f"Se procesaron {len(eventos)} eventos.")


procesar_eventos_y_guardar()
print("Finished")
