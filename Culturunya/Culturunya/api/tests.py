
# -*- coding: utf-8 -*-

from django.test import TestCase

# tests/test_endpoints.py
import json
from datetime import datetime, timedelta
from unittest.mock import patch

from django.urls import reverse
from django.utils import timezone
from rest_framework.test import APITestCase, APIClient
from rest_framework import status
from rest_framework.authtoken.models import Token
from unittest.mock import patch, MagicMock
from persistence.models import (
    User, Event, Location, Category, Rating, TypeRating, POINTS_TO_NEXT_RANK, TypeRank
)


class BaseAPITestCase(APITestCase):
    """
    Crea un usuario normal y otro admin, con sus tokens,
    accesibles en self.user / self.admin y self.token / self.admin_token
    """
    def setUp(self):
        self.user = User.objects.create_user(
            username="pepe", password="abcd1234", email="pepe@test.com"
        )
        self.admin = User.objects.create_user(
            username="admin", password="admin1234",
            email="admin@test.com", is_admin=True
        )
        # tokens
        self.token = Token.objects.create(user=self.user)
        self.admin_token = Token.objects.create(user=self.admin)

    # helper: anade cabecera Token xxx
    def auth(self, token):
        self.client.credentials(HTTP_AUTHORIZATION=f"Token {token.key}")


#Endpoints sin autenticacion
class PublicEndpointsTests(BaseAPITestCase):

    @patch("api.views.get_all_events")
    def test_get_events_ok(self, mock_get):
        mock_get.return_value = [{"id": "1", "name": "Fake"}]
        url = reverse("get_events")
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, status.HTTP_200_OK)
        self.assertEqual(resp.json(), {"events": mock_get.return_value})
        mock_get.assert_called_once()

    def test_create_user_ok_and_conflict(self):
        # OK
        url = reverse("create_user")
        payload = {"username": "nuevo", "password": "asdf1234", "email": "nuevo@test.com"}
        resp = self.client.post(url, json.dumps(payload), content_type="application/json")
        self.assertEqual(resp.status_code, status.HTTP_201_CREATED)

        # 409 duplicado
        resp2 = self.client.post(url, json.dumps(payload), content_type="application/json")
        self.assertEqual(resp2.status_code, status.HTTP_409_CONFLICT)

    def test_create_user_missing_field_400(self):
        url = reverse("create_user")
        resp = self.client.post(url, json.dumps({"username": "x"}), content_type="application/json")
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)


#Autenticacion
class AuthEndpointsTests(BaseAPITestCase):

    def test_login_logout_cycle(self):
        url_login = reverse("api_token_auth")
        resp = self.client.post(url_login, {"username": "pepe", "password": "abcd1234"})
        self.assertEqual(resp.status_code, status.HTTP_200_OK)
        token_val = resp.json()["token"]
        # logout
        url_logout = reverse("logout")
        self.client.credentials(HTTP_AUTHORIZATION=f"Token {token_val}")
        resp2 = self.client.post(url_logout)
        self.assertEqual(resp2.status_code, status.HTTP_200_OK)
        # token debe quedar invalido
        resp3 = self.client.get(reverse("get_filtered_events"))  # requiere token
        self.assertEqual(resp3.status_code, status.HTTP_401_UNAUTHORIZED)


#Events
class EventsEndpointsTests(BaseAPITestCase):

    @patch("api.views.filter_events")
    def test_get_filtered_events_ok(self, mock_filter):
        mock_filter.return_value = [{"id": "1", "name": "Music Fest"}]
        self.auth(self.token)
        url = reverse("get_filtered_events")
        resp = self.client.get(url, {"categories": "Musica"})
        self.assertEqual(resp.status_code, status.HTTP_200_OK)
        self.assertIn("events", resp.json())

    def test_get_filtered_events_unauth_401(self):
        url = reverse("get_filtered_events")
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, status.HTTP_401_UNAUTHORIZED)


#Ratings
class RatingEndpointsTests(BaseAPITestCase):

    def setUp(self):
        super().setUp()
        # Creamos un evento real para los tests de Rating
        loc = Location.objects.create(
            longitude=1, latitude=1, address="x", city="c", comarca="y", province="z"
        )
        self.event = Event.objects.create(
            id="e1", name="Concierto", date_start=timezone.now(),
            date_end=timezone.now() + timedelta(hours=2),
            description="desc",
            price=0, location=loc
        )
        self.auth(self.token)

    def test_create_rating_ok(self):
        url = reverse("create_rating")
        payload = {"event_id": self.event.id, "rating": TypeRating.FUN}
        resp = self.client.post(url, payload)
        self.assertEqual(resp.status_code, status.HTTP_201_CREATED)
        self.assertEqual(Rating.objects.count(), 1)

    def test_create_rating_invalid_choice_400(self):
        url = reverse("create_rating")
        payload = {"event_id": self.event.id, "rating": "Terrible"}
        resp = self.client.post(url, payload)
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)

    def test_get_event_comments(self):
        Rating.objects.create(
            event=self.event, user=self.user, rating=TypeRating.AWESOME, comment="wow!"
        )
        url = reverse("get_event_comments", args=[self.event.id])
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, status.HTTP_200_OK)
        self.assertEqual(len(resp.json()), 1)


#Users
class UserEndpointsTests(BaseAPITestCase):

    def test_change_password_wrong_old(self):
        self.auth(self.token)
        url = reverse("change_password")
        payload = {"old_password": "mal", "new_password": "nuevo123"}
        resp = self.client.put(url, payload)
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)

    def test_update_language_ok_and_invalid(self):
        self.auth(self.token)
        url = reverse("update_language")
        resp = self.client.put(url, json.dumps({"language": "EN"}), content_type="application/json")
        self.assertEqual(resp.status_code, status.HTTP_200_OK)

        resp2 = self.client.put(url, json.dumps({"language": "FR"}), content_type="application/json")
        self.assertEqual(resp2.status_code, status.HTTP_400_BAD_REQUEST)

    def test_delete_account(self):
        self.auth(self.token)
        url = reverse("delete_own_account")
        resp = self.client.delete(url)
        self.assertEqual(resp.status_code, status.HTTP_200_OK)
        self.assertFalse(User.objects.filter(username="pepe").exists())


#Admin
class AdminEndpointsTests(BaseAPITestCase):

    def test_list_reports_forbidden_for_user(self):
        self.auth(self.token)
        resp = self.client.get(reverse("list_reports"))
        self.assertEqual(resp.status_code, status.HTTP_403_FORBIDDEN)

    def test_list_reports_ok_admin(self):
        self.auth(self.admin_token)
        resp = self.client.get(reverse("list_reports"))
        self.assertEqual(resp.status_code, status.HTTP_200_OK)

class ExtraBranchesTests(BaseAPITestCase):
    
    
    # create_user JSON malformado
    
    def test_create_user_bad_json_400(self):
        url = reverse("create_user")
        resp = self.client.post(url, "}{", content_type="application/json")  # JSON invalido
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)

    
    # google_auth token de Google invalido
    
    @patch("api.views.id_token.verify_oauth2_token", side_effect=ValueError)
    def test_google_auth_invalid_token(self, mock_verify):
        url = reverse("google_auth")
        resp = self.client.post(url, {"id_token": "falso"})
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn("ID token invalido", resp.json()["error"])

    
    #get_events metodo HTTP no permitido (else)
    
    def test_get_events_wrong_method_400(self):
        resp = self.client.post(reverse("get_events"))  # solo permite GET
        self.assertEqual(resp.status_code, status.HTTP_405_METHOD_NOT_ALLOWED)

    
    # post_test / put_test JSON invalido
    
    def test_post_test_invalid_json(self):
        self.auth(self.token)
        url = reverse("post_test")
        resp = self.client.post(url, "{", content_type="application/json")
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)

    def test_put_test_invalid_json(self):
        self.auth(self.token)
        url = reverse("put_test")
        resp = self.client.put(url, "{", content_type="application/json")
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)

    
    #create_rating_endpoint faltan campos (KeyError)
    
    def test_create_rating_missing_field_400(self):
        self.auth(self.token)
        url = reverse("create_rating")
        resp = self.client.post(url, {"event_id": 123})  # falta 'rating'
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn("Falta el campo obligatorio", resp.json()["error"])

    
    #create_rating_endpoint ValueError del servicio
    @patch("api.views.create_rating", side_effect=ValueError("boom"))
    def test_create_rating_service_error_400(self, mock_create):
        self.auth(self.token)
        url = reverse("create_rating")
        resp = self.client.post(url, {"event_id": 123, "rating": "Fun"})
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn("boom", resp.json()["error"])

    
    # obtain_location_points usuario ya asistio (403)
    def test_obtain_location_points_already_assisted(self):
        # evento numerico porque la ruta espera int
        event = Event.objects.create(
            id="1", name="Expo", date_start=timezone.now(),
            date_end=timezone.now() + timedelta(hours=1),
            description="d", price=0
        )
        self.user.events_assisted.add(event)
        self.user.save()

        self.auth(self.token)
        url = reverse("obtain_location_points", args=[1])
        resp = self.client.put(url)
        self.assertEqual(resp.status_code, status.HTTP_403_FORBIDDEN)

    
    # obtain_location_points subir de rango -----
    
    def test_obtain_location_points_rank_up(self):
        #evento nuevo
        event = Event.objects.create(
            id="2", name="Concierto", date_start=timezone.now(),
            date_end=timezone.now() + timedelta(hours=1),
            description="d", price=0
        )
        # situamos al usuario justo antes de subir
        self.user.rank_event = TypeRank.UNRANKED
        self.user.current_event_points = POINTS_TO_NEXT_RANK[TypeRank.UNRANKED] - 20
        self.user.save()

        self.auth(self.token)
        url = reverse("obtain_location_points", args=[2])
        resp = self.client.put(url)
        self.assertEqual(resp.status_code, status.HTTP_200_OK)
        self.user.refresh_from_db()
        self.assertEqual(self.user.rank_event, TypeRank.BRONZE)
        self.assertEqual(self.user.current_event_points, 0)
        self.assertIn("Has subido de nivel", resp.json()["message"])


class LogoutEdgeCaseTests(BaseAPITestCase):
    """Cubre el except (AttributeError, Token.DoesNotExist) en logout_view"""

    def test_logout_without_token_record_returns_400(self):
        self.auth(self.token)
        url = reverse("logout")
        self.token.delete()

        resp = self.client.post(url)
        self.assertEqual(resp.status_code, status.HTTP_401)
        self.assertIn("Sesion no existente", resp.json()["error"])



class GoogleAuthTests(BaseAPITestCase):
    """Cubre la rama de exito y la de token invalido en google_auth"""

    @patch("api.views.id_token.verify_oauth2_token")
    def test_google_auth_success(self, mock_verify):
        """
        Devuelve 200 y crea/actualiza usuario
        """
        mock_verify.return_value = {
            "sub": "google123",
            "email": "nuevo@test.com",
            "name": "Nuevo Usuario"
        }

        url = reverse("google_auth")
        resp = self.client.post(url, {"id_token": "dummy"})
        self.assertEqual(resp.status_code, status.HTTP_200_OK)

        body = resp.json()
        self.assertIn("token", body)
        self.assertEqual(body["username"], "nuevo@test.com")

        # el usuario debe existir en BD
        self.assertTrue(User.objects.filter(email="nuevo@test.com").exists())

    @patch("api.views.id_token.verify_oauth2_token", side_effect=ValueError)
    def test_google_auth_invalid_token(self, mock_verify):
        """
        Fuerza el ValueError y cubre el return 400
        """
        url = reverse("google_auth")
        resp = self.client.post(url, {"id_token": "bad"})
        self.assertEqual(resp.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertIn("ID token invalido", resp.json()["error"])

class RatingEditDeleteTests(BaseAPITestCase):
    """Cubre edit_rating y delete_rating"""

    def setUp(self):
        super().setUp()
        loc = Location.objects.create(
            longitude=1, latitude=1,
            address="X", city="C", comarca="Y", province="Z"
        )
        self.event = Event.objects.create(
            id="ev1", name="Show",
            date_start=timezone.now(),
            date_end=timezone.now() + timedelta(hours=1),
            description="d", price=0, location=loc
        )
        # rating del usuario normal
        self.rating = Rating.objects.create(
            event=self.event, user=self.user,
            rating=TypeRating.FUN, comment="old"
        )
        # rating de otro user para probar 403
        self.other_user = self.admin  # reciclamos admin como otro user
        self.other_rating = Rating.objects.create(
            event=self.event, user=self.other_user,
            rating=TypeRating.BAD, comment="otro"
        )

    #  edit_rating 
    def test_edit_rating_ok(self):
        self.auth(self.token)
        url = reverse("edit_rating", args=[self.rating.id])
        payload = {"rating": TypeRating.AWESOME, "comment": "nuevo"}
        resp = self.client.put(url, payload)
        self.assertEqual(resp.status_code, 200)
        self.rating.refresh_from_db()
        self.assertEqual(self.rating.rating, TypeRating.AWESOME)
        self.assertEqual(self.rating.comment, "nuevo")

    def test_edit_rating_invalid_choice_400(self):
        self.auth(self.token)
        url = reverse("edit_rating", args=[self.rating.id])
        resp = self.client.put(url, {"rating": "Terrible", "comment": "x"})
        self.assertEqual(resp.status_code, 400)

    def test_edit_rating_not_owner_403(self):
        self.auth(self.token)
        url = reverse("edit_rating", args=[self.other_rating.id])
        resp = self.client.put(url, {"rating": TypeRating.FUN, "comment": "x"})
        self.assertEqual(resp.status_code, 403)

    def test_edit_rating_not_found_404(self):
        self.auth(self.token)
        url = reverse("edit_rating", args=[999])
        resp = self.client.put(url, {"rating": TypeRating.FUN, "comment": "x"})
        self.assertEqual(resp.status_code, 404)

    #  delete_rating 
    def test_delete_rating_ok(self):
        self.auth(self.token)
        url = reverse("delete_rating", args=[self.rating.id])
        resp = self.client.delete(url)
        self.assertEqual(resp.status_code, 200)
        self.assertFalse(Rating.objects.filter(id=self.rating.id).exists())

    def test_delete_rating_not_owner_403(self):
        self.auth(self.token)
        url = reverse("delete_rating", args=[self.other_rating.id])
        resp = self.client.delete(url)
        self.assertEqual(resp.status_code, 403)

    def test_delete_rating_not_found_404(self):
        self.auth(self.token)
        url = reverse("delete_rating", args=[999])
        resp = self.client.delete(url)
        self.assertEqual(resp.status_code, 404)


class ChatEndpointsTests(BaseAPITestCase):
    """Cubre send_message_user_to_admin y send_message_admin_to_user"""

    #  user -> admin 
    @patch("api.views.create_message")
    def test_send_message_user_to_admin_ok(self, mock_create):
        self.auth(self.token)               # usuario normal
        url = reverse("send_to_admin")
        resp = self.client.post(
            url, data=json.dumps({"text": "hola"}), content_type="application/json"
        )
        self.assertEqual(resp.status_code, status.HTTP_201_CREATED)
        mock_create.assert_called_once()    # se invoca servicio

    @patch("api.views.create_message")
    def test_send_message_user_to_admin_forbidden_for_admin(self, mock_create):
        self.auth(self.admin_token)         # intentamos como admin
        url = reverse("send_to_admin")
        resp = self.client.post(
            url, data=json.dumps({"text": "hola"}), content_type="application/json"
        )
        self.assertEqual(resp.status_code, 403)
        mock_create.assert_not_called()

    #  admin -> user 
    @patch("api.views.create_message")
    def test_send_message_admin_to_user_ok(self, mock_create):
        self.auth(self.admin_token)         # admin autenticado
        url = reverse("send_to_user")
        resp = self.client.post(
            url,
            data=json.dumps({"receiver_id": self.user.id, "text": "hi"}),
            content_type="application/json"
        )
        self.assertEqual(resp.status_code, 201)
        mock_create.assert_called_once()

    @patch("api.views.create_message")
    def test_send_message_admin_to_user_forbidden_for_user(self, mock_create):
        self.auth(self.token)               # usuario normal
        url = reverse("send_to_user")
        resp = self.client.post(
            url,
            data=json.dumps({"receiver_id": self.admin.id, "text": "hi"}),
            content_type="application/json"
        )
        self.assertEqual(resp.status_code, 403)
        mock_create.assert_not_called()

