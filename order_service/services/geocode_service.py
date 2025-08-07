import logging

import requests
from django.conf import settings


class GeoocodeService:
    """
    Class to handle geocoding operations using the Google Geocoding API.
    This service provides methods to convert addresses to geographic coordinates
    (latitude and longitude).
    """

    @staticmethod
    def address_to_coordinates(address: str) -> dict:
        url = "https://maps.googleapis.com/maps/api/geocode/json"
        params = {"address": address, "key": settings.GOOGLE_GEOCODE_API_KEY}

        response = requests.get(url, params, timeout=5)
        response.raise_for_status()
        data = response.json()

        if data["status"] != "OK":
            logging.error(
                f"Erro ao converter endereço: {address}. "
                f"Status: {data['status']}, Mensagem: {data.get('error_message', 'N/A')}"
            )
            raise ValueError("Erro ao converter endereço.")

        location = data["results"][0]["geometry"]["location"]
        return {"lat": location["lat"], "lng": location["lng"]}
