# -*- coding: utf-8 -*-
import requests
import json
from datetime import datetime

# URL del endpoint
URL = "https://analisi.transparenciacatalunya.cat/resource/rhpv-yr4f.json?$limit=1000&$offset=0&$where=data_fi IS NOT NULL AND data_inici IS NOT NULL"

def obtener_eventos_filtrados():
    try:
        # Hacer la solicitud GET al endpoint
        response = requests.get(URL)
        
        # Verificar si la solicitud fue exitosa
        if response.status_code == 200:
            eventos = response.json()
            
            # Filtrar los eventos entre 2025 y 2026
            eventos_filtrados = []
            for evento in eventos:
                try:
                    data_inici = datetime.strptime(evento["data_inici"], "%Y-%m-%dT%H:%M:%S.%f")
                    data_fi = datetime.strptime(evento["data_fi"], "%Y-%m-%dT%H:%M:%S.%f")
                    
                    if 2025 <= data_inici.year <= 2026 and 2025 <= data_fi.year <= 2026:
                        eventos_filtrados.append(evento)
                except (KeyError, ValueError):
                    # Si hay algún problema con el formato de la fecha, saltamos el evento
                    continue
            
            return json.dumps(eventos_filtrados, indent=4, ensure_ascii=False)
        
        else:
            print(f"Error en la solicitud: {response.status_code}")
            return None

    except requests.RequestException as e:
        print(f"Error en la solicitud: {e}")
        return None

if __name__ == "__main__":
    eventos_json = obtener_eventos_filtrados()
    if eventos_json:
        print(eventos_json)  # Esto hay que cambiarlo para poder guardar en la bd