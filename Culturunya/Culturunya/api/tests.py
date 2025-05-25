
# -*- coding: utf-8 -*-

from django.test import TestCase

# tests/test_endpoints.py
import json
from datetime import datetime, timedelta

from PIL import Image
import tempfile
from django.urls import reverse
from django.utils import timezone
from rest_framework.test import APITestCase, APIClient
from rest_framework import status
from rest_framework.authtoken.models import Token
from unittest.mock import patch, MagicMock,PropertyMock
from persistence.models import (
    User, Event, Location, Category, Rating, TypeRating, RANK_POINTS, TypeRank, Message, Question, QuestionTranslation,
    TypeRank, RANK_ORDER,
)

from django.core.files.uploadedfile import SimpleUploadedFile


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

    def test_create_rating_user_banned_from_comments(self):
        self.user.banned_from_comments = True
        self.user.save()
        self.auth(self.token)
        url = reverse("create_rating")
        payload = {
            "event_id": self.event.id,
            "rating": TypeRating.FUN,
            "comment": "Debería ignorarse"
        }
        resp = self.client.post(url, payload)
        self.assertEqual(resp.status_code, 201)
        self.assertIsNone(resp.json().get("comment"))

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

  
    #get_events metodo HTTP no permitido (else)
    
    def test_get_events_wrong_method_400(self):
        resp = self.client.post(reverse("get_events"))  # solo permite GET
        self.assertEqual(resp.status_code, status.HTTP_405_METHOD_NOT_ALLOWED)

   
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
        self.user.total_event_points = RANK_POINTS[TypeRank.BRONZE] - 20
        self.user.save()

        self.auth(self.token)
        url = reverse("obtain_location_points", args=[2])
        resp = self.client.put(url)
        self.assertEqual(resp.status_code, status.HTTP_200_OK)
        self.user.refresh_from_db()
        self.assertEqual(self.user.rank_event, TypeRank.BRONZE)
        self.assertEqual(self.user.total_event_points, RANK_POINTS[TypeRank.BRONZE])
        self.assertIn("Has subido de nivel", resp.json()["message"])


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
    
    @patch("api.views.get_admin_with_less_messages", side_effect=Exception("Error forzado"))
    def test_send_message_user_to_admin_exception(self, mock_admin):
        self.auth(self.token)  # usuario normal
        url = reverse("send_to_admin")
        payload = {"text": "hola"}

        resp = self.client.post(url, data=json.dumps(payload), content_type="application/json")

        self.assertEqual(resp.status_code, 400)
        self.assertIn("Error forzado", resp.json()["error"])

class ChatAdminEndpointsTests(BaseAPITestCase):

    @patch("api.views.get_messages_admin")
    @patch("api.views.get_messages")
    def test_list_chats_admin_ok(self, mock_get_messages, mock_get_admin):
        self.auth(self.admin_token)

        # Interlocutores
        u2 = User.objects.create(username="u2", email="u2@test.com")
        u3 = User.objects.create(username="u3", email="u3@test.com")

        mock_get_admin.return_value.values_list.return_value = [
            (u2.id, self.admin.id), (self.admin.id, u3.id)
        ]

        fake_msg = Message(
            id=1, text="hola", sender=self.admin, receiver=self.user,
            date_written=timezone.now()
        )
        mock_get_messages.return_value.order_by.return_value.first.return_value = fake_msg

        url = reverse("list_chats_admin")
        resp = self.client.get(url)

        self.assertEqual(resp.status_code, 200)
        self.assertEqual(len(resp.json()), 2)

    def test_list_chats_admin_forbidden_for_regular_user(self):
        self.auth(self.token)
        url = reverse("list_chats_admin")
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, 403)



# UPDATE_USERNAME
class UpdateUsernameTests(BaseAPITestCase):

    def test_update_username_ok(self):
        self.auth(self.token)
        url = reverse("update_username")
        new_name = "nuevopepe"
        resp = self.client.put(
            url, data=json.dumps({"username": new_name}),
            content_type="application/json"
        )
        self.assertEqual(resp.status_code, 200)
        self.user.refresh_from_db()
        self.assertEqual(self.user.username, new_name)



# GET_QUESTION
class QuestionEndpointTests(BaseAPITestCase):

    def setUp(self):
        super().setUp()
        q = Question.objects.create(question_es="¿Capital de España?")
        QuestionTranslation.objects.create(
            question=q, language="ES",
            text="¿Capital de España?", options=["Madrid", "Barcelona"]
        )
        self.question_id = q.id

    def test_get_question_ok(self):
        self.auth(self.token)
        url = reverse("get_question", args=[self.question_id])
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, 200)

    def test_get_question_not_found_404(self):
        self.auth(self.token)
        url = reverse("get_question", args=[999])
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, 404)



# OBTAIN_QUIZ_POINTS (sube y baja rango)
class QuizPointsRankTests(BaseAPITestCase):

    @patch("api.views.update_rank_from_adding_points", lambda r, p: TypeRank.BRONZE)
    def test_quiz_points_rank_up(self):
        self.auth(self.token)
        self.user.rank_quiz = TypeRank.UNRANKED
        self.user.total_quiz_points = RANK_POINTS[TypeRank.BRONZE] - 5
        self.user.save()

        url = reverse("obtain_quiz_points")
        resp = self.client.put(url, {"points": 10})
        self.assertEqual(resp.status_code, 200)
        self.user.refresh_from_db()
        self.assertEqual(self.user.rank_quiz, TypeRank.BRONZE)
        self.assertIn("Has subido de nivel", resp.json()["message"])

    @patch("api.views.update_rank_from_decreasing_points", lambda r, p: TypeRank.UNRANKED)
    def test_quiz_points_rank_down(self):
        self.auth(self.token)
        self.user.rank_quiz = TypeRank.BRONZE
        self.user.total_quiz_points = 5
        self.user.save()

        url = reverse("obtain_quiz_points")
        resp = self.client.put(url, {"points": -10})
        self.assertEqual(resp.status_code, 200)
        self.user.refresh_from_db()
        self.assertEqual(self.user.rank_quiz, TypeRank.UNRANKED)
        self.assertIn("bajado de nivel", resp.json()["message"].lower())



# GET_USERS (solo admin)
class GetUsersAdminOnlyTests(BaseAPITestCase):

    def test_get_users_forbidden_for_regular(self):
        self.auth(self.token)
        resp = self.client.get(reverse("get_users"))
        self.assertEqual(resp.status_code, 403)

    def test_get_users_ok_for_admin(self):
        self.auth(self.admin_token)
        resp = self.client.get(reverse("get_users"))
        self.assertEqual(resp.status_code, 200)
        self.assertGreaterEqual(len(resp.json()), 2)

class RedBranchesTests(APITestCase):
    """
    Cubre los else / except que estaban resaltados en rojo
    (get_events, get_filtered_events, create_rating, endpoints de chat, etc.)
    """

    #  set-up comun
    def setUp(self):
        self.user = User.objects.create_user(
            username="u1", password="pwd1234", email="u1@test.com"
        )
        self.admin = User.objects.create_user(
            username="ad1", password="pwd1234", email="ad1@test.com",
            is_admin=True
        )
        self.token = Token.objects.create(user=self.user)
        self.admin_token = Token.objects.create(user=self.admin)

        # Mini localización + evento real para tests de rating
        loc = Location.objects.create(
            longitude=1, latitude=1,
            address="c/ X", city="Y", comarca="Z", province="P"
        )
        self.event = Event.objects.create(
            id="ev1", name="Show",
            date_start=timezone.now(),
            date_end=timezone.now() + timedelta(hours=2),
            description="d", price=0, location=loc
        )

    # util
    def auth(self, token):
        self.client.credentials(HTTP_AUTHORIZATION=f"Token {token.key}")

    #  GET_EVENTS
    def test_get_events_method_not_allowed_405(self):
        resp = self.client.post(reverse("get_events"))
        self.assertEqual(resp.status_code, status.HTTP_405_METHOD_NOT_ALLOWED)
        self.assertIn("not allowed", resp.json()["detail"].lower())

    #  GET_FILTERED_EVENTS
    def test_get_filtered_events_method_not_allowed_405(self):
        """Cubre intento de acceder a get_filtered_events con método no permitido"""
        self.auth(self.token)
        resp = self.client.post(reverse("get_filtered_events"))

        self.assertEqual(resp.status_code, status.HTTP_405_METHOD_NOT_ALLOWED)
        self.assertIn("not allowed", resp.json()["detail"].lower())

    #  send_message_user_to_admin
    @patch("api.views.get_admin_with_less_messages", return_value=None)
    def test_send_message_user_to_admin_no_admins_404(self, mock_get_admin):
        self.auth(self.token)
        url = reverse("send_to_admin")
        resp = self.client.post(
            url, data=json.dumps({"text": "hola"}),
            content_type="application/json"
        )
        self.assertEqual(resp.status_code, status.HTTP_404_NOT_FOUND)
        self.assertIn("No hay admins", resp.json()["error"])



    #  get_conversation_with_admin
    def test_get_conversation_with_admin_forbidden_for_admin(self):
        self.auth(self.admin_token)
        resp = self.client.get(reverse("get_conversation_with_admin"))
        self.assertEqual(resp.status_code, status.HTTP_403_FORBIDDEN)

    #  get_conversation_with_admin
    def test_get_conversation_with_admin_no_admins_404(self):
        # quitamos a todos los admins
        User.objects.update(is_admin=False)
        self.auth(self.token)
        resp = self.client.get(reverse("get_conversation_with_admin"))
        self.assertEqual(resp.status_code, status.HTTP_404_NOT_FOUND)
        self.assertIn("No hay administradores", resp.json()["error"])

    #  get_conversation_with_user
    def test_get_conversation_with_user_forbidden_for_non_admin(self):
        self.auth(self.token)
        url = reverse("get_conversation_with_user", args=[self.admin.id])
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, status.HTTP_403_FORBIDDEN)

def generate_test_image():
    image = Image.new("RGB", (10, 10), color="red")
    temp = tempfile.NamedTemporaryFile(suffix=".jpg")
    image.save(temp, format="JPEG")
    temp.seek(0)
    return temp

class RedBranchesTests(BaseAPITestCase):
    
    #   SEND_MESSAGE_*  
    
    @patch("api.views.create_message")
    def test_send_message_admin_to_user_receiver_not_found_400(self, mock_create):
        """Dispara el bloque DoesNotExist → 400"""
        self.auth(self.admin_token)
        url = reverse("send_to_user")
        payload = {"receiver_id": 9999, "text": "hi"}      # id inexistente
    
        resp = self.client.post(url, json.dumps(payload),
                                content_type="application/json")
    
        self.assertEqual(resp.status_code, 400)
        self.assertIn("destinatario no encontrado", resp.json()["error"].lower())
        mock_create.assert_not_called()

    @patch("api.views.create_message", side_effect=Exception("boom"))
    def test_send_message_admin_to_user_generic_error_400(self, mock_create):
        """Cubre el except Exception general"""
        self.auth(self.admin_token)
        user_id = self.user.id
        url = reverse("send_to_user")

        payload = {"receiver_id": user_id, "text": "err"}
        resp = self.client.post(url, json.dumps(payload),
                                content_type="application/json")

        self.assertEqual(resp.status_code, 400)
        self.assertIn("boom", resp.json()["error"])

    @patch("api.views.get_admin_with_less_messages", side_effect=Exception("ups"))
    def test_send_message_user_to_admin_generic_error_400(self, mock_get):
        """Hace saltar el except de send_message_user_to_admin"""
        self.auth(self.token)
        url = reverse("send_to_admin")
        resp = self.client.post(url, json.dumps({"text": "x"}),
                                content_type="application/json")
        self.assertEqual(resp.status_code, 400)
        self.assertIn("ups", resp.json()["error"])
    #
    #  GET_CONVERSATION_WITH_ADMIN / USER 
    #
    @patch("api.views.get_messages")
    def test_get_conversation_with_admin_ok(self, mock_get):
        
        msg = Message(id=1, text="hola", sender=self.admin,
                      receiver=self.user, date_written=timezone.now())
        mock_get.return_value = [msg]

        self.auth(self.token)
        url = reverse("get_conversation_with_admin")
        resp = self.client.get(url)

        self.assertEqual(resp.status_code, 200)
        body = resp.json()
        self.assertEqual(body[0]["from"], "Administrador")
        mock_get.assert_called_once()

    def test_get_conversation_with_admin_no_admin_404(self):
        
        User.objects.filter(id=self.admin.id).delete()      # eliminamos al unico admin
        self.auth(self.token)
        url = reverse("get_conversation_with_admin")
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, 404)

    @patch("api.views.get_messages")
    def test_get_conversation_with_user_ok(self, mock_get):
        """Admin ve su chat con un user concreto"""
        self.auth(self.admin_token)
        msg = Message(id=2, text="hey", sender=self.user,
                      receiver=self.admin, date_written=timezone.now())
        mock_get.return_value = [msg]

        url = reverse("get_conversation_with_user", args=[self.user.id])
        resp = self.client.get(url)

        self.assertEqual(resp.status_code, 200)
        self.assertEqual(resp.json()[0]["to"], self.admin.username)
        mock_get.assert_called_once()
    
    # CHANGE PASSWORD  
    
    def test_change_password_success(self):
        self.auth(self.token)
        url = reverse("change_password")
        payload = {"old_password": "abcd1234", "new_password": "nuevo1234"}
        resp = self.client.put(url, payload)

        self.assertEqual(resp.status_code, 200)
        # el usuario puede logearse con la nueva contrasena
        self.user.refresh_from_db()
        self.assertTrue(self.user.check_password("nuevo1234"))
    
    #USER PROFILE
    
    def test_user_profile_view_ok(self):
        self.auth(self.token)
        resp = self.client.get(reverse("user_profile"))
        self.assertEqual(resp.status_code, 200)
        self.assertEqual(resp.json()["email"], self.user.email)
    
    #UPLOAD PIC
    
    
    def test_upload_profile_pic_ok(self):
        self.auth(self.token)
        url = reverse("upload_profile_pic")
        image = generate_test_image()
        resp = self.client.post(
            url, {"profile_pic": image}, format="multipart"
        )
        self.assertEqual(resp.status_code, 200)
        self.assertIn("profile_pic", resp.json())
    
    #   UPDATE LANGUAGE / USERNAME (except)
    
    def test_update_language_json_error_500(self):
        self.auth(self.token)
        url = reverse("update_language")
        resp = self.client.put(url, "}{", content_type="application/json")
        self.assertEqual(resp.status_code, 500)
        self.assertIn("Expecting value", resp.json()["error"])

    def test_update_username_json_error_500(self):
        self.auth(self.token)
        url = reverse("update_username")
        resp = self.client.put(url, "}{", content_type="application/json")
        self.assertEqual(resp.status_code, 500)
        self.assertIn("Expecting value", resp.json()["error"])
    
    #REPORTES
    
    @patch("api.views.create_report", return_value=({"msg": "ok"}, 201))
    def test_send_report_ok(self, mock_create):
        self.auth(self.token)
        url = reverse("create_report")
        payload = {"rating_id": 1, "message": "spam"}
        resp = self.client.post(url, payload)
        self.assertEqual(resp.status_code, 201)
        mock_create.assert_called_once()

    @patch("api.views.create_resolved_report",
           return_value=({"msg": "resuelto"}, 200))
    def test_resolve_report_ok(self, mock_resolve):
        self.auth(self.admin_token)
        url = reverse("resolve_report", args=[5])
        resp = self.client.post(url, {"action": "Warning", "message": "cuidado"})
        self.assertEqual(resp.status_code, 200)
        mock_resolve.assert_called_once()
    #
    #   OBTAIN LOCATION POINTS (sin rank-up) 
    #
    def test_obtain_location_points_no_rank_up(self):
        loc = Location.objects.create(longitude=1, latitude=1,
                                      address="a", city="c", comarca="co", province="p")
        ev = Event.objects.create(
            id="55", name="expo", date_start=timezone.now(),
            date_end=timezone.now() + timedelta(hours=2),
            description="d", price=0, location=loc
        )
        # usuario ya en bronze muy holgado; +20 no cambia rango
        self.user.rank_event = TypeRank.BRONZE
        self.user.total_event_points = RANK_POINTS[TypeRank.BRONZE] + 10
        self.user.save()

        self.auth(self.token)
        url = reverse("obtain_location_points", args=[55])
        resp = self.client.put(url)
        self.assertEqual(resp.status_code, 200)
        self.user.refresh_from_db()
        self.assertEqual(self.user.rank_event, TypeRank.BRONZE)
        self.assertIn("Puntos obtenidos", resp.json()["message"])




    #  SEND MESSAGE USER TO ADMIN (no hay admins)
    @patch("api.views.get_admin_with_less_messages", return_value=None)
    def test_send_message_user_to_admin_no_admins(self, mock_admin):
        self.auth(self.token)
        url = reverse("send_to_admin")
        resp = self.client.post(url, json.dumps({"text": "hola"}), content_type="application/json")
        self.assertEqual(resp.status_code, 404)
        self.assertIn("No hay admins", resp.json()["error"])


    #  GET CONVERSATION WITH ADMIN (es admin y no debería)
    def test_get_conversation_with_admin_is_admin_403(self):
        self.user.is_admin = True
        self.user.save()
        self.auth(self.token)
        url = reverse("get_conversation_with_admin")
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, 403)
        self.assertIn("Un administrador no usa este endpoint", resp.json()["error"])


    #  GET CONVERSATION WITH USER (no es admin)
    def test_get_conversation_with_user_is_not_admin_403(self):
        self.user.is_admin = False
        self.user.save()
        self.auth(self.token)
        url = reverse("get_conversation_with_user", args=[self.admin.id])
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, 403)
        self.assertIn("Solo los administradores pueden acceder", resp.json()["error"])


    #  CHANGE PASSWORD (serializer inválido)
    def test_change_password_invalid_serializer(self):
        self.auth(self.token)
        url = reverse("change_password")
        payload = {"wrong_field": "abc", "new_password": "nuevo1234"}
        resp = self.client.put(url, payload)
        self.assertEqual(resp.status_code, 400)


    #  UPLOAD PROFILE PIC no valido
    def test_upload_profile_pic_invalid_serializer(self):
        self.auth(self.token)
        url = reverse("upload_profile_pic")
        fake_file = SimpleUploadedFile("file.txt", b"not an image", content_type="text/plain")
        resp = self.client.post(url, {"profile_pic": fake_file}, format="multipart")
        self.assertEqual(resp.status_code, 400)


    #  QUIZ POINTS:
    def test_obtain_quiz_points_positive_no_rank_up(self):
        self.auth(self.token)
        self.user.rank_quiz = TypeRank.BRONZE
        self.user.total_quiz_points = 0
        self.user.save()
        url = reverse("obtain_quiz_points")
        resp = self.client.put(url, {"points": 1})
        self.assertEqual(resp.status_code, 200)
        self.assertIn("Puntos obtenidos", resp.json()["message"])




    #  GET QUIZ RANKING (leaderboard)
    @patch("api.views.get_quiz_ranking_leaderboard", return_value=[])
    def test_get_quiz_ranking_ok(self, mock_func):
        self.auth(self.token)
        url = reverse("quiz_leaderboard")
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, 200)
        mock_func.assert_called_once()


    #  GET EVENT ASSISTANCE RANKING (leaderboard)
    @patch("api.views.get_events_ranking_leaderboard", return_value=[])
    def test_get_event_assistance_ranking_ok(self, mock_func):
        self.auth(self.token)
        url = reverse("events_leaderboard")
        resp = self.client.get(url)
        self.assertEqual(resp.status_code, 200)
        mock_func.assert_called_once()

class ExtraRedBranchesTests(BaseAPITestCase):
    #
    #  obtain_quiz_points → rama «Puntos decrementados» 
    #
    def test_obtain_quiz_points_negative_no_rank_down(self):
        """
        El usuario pierde puntos pero NO cambia de rango,
        por lo que debe entrar en el return "Puntos decrementados".
        """
        self.auth(self.token)                     # token ya creado en BaseAPITestCase

        # ― situación inicial: rango Bronze y 100 pts
        self.user.rank_quiz = TypeRank.BRONZE
        self.user.total_quiz_points = 100
        self.user.save()

        
        with patch("api.views.update_rank_from_decreasing_points",
                   side_effect=lambda rank, pts: rank):
            url = reverse("obtain_quiz_points")
            resp = self.client.put(url, {"points": -10})

        self.assertEqual(resp.status_code, status.HTTP_200_OK)
        self.assertEqual(resp.json()["message"], "Puntos decrementados")


    @patch("rest_framework.authentication.TokenAuthentication.authenticate")
    def test_logout_edge_case_without_token_record(self, mock_auth):
        """
        Fuerza AttributeError accediendo a user.auth_token (sin usar delattr).
        """
        mock_auth.return_value = (self.user, None)  
        url = reverse("logout")

        # Simular que user.auth_token levanta AttributeError
        with patch.object(type(self.user), "auth_token", new_callable=PropertyMock) as mock_token_prop:
            mock_token_prop.side_effect = AttributeError("Simulated missing token")
            resp = self.client.post(url)

        self.assertEqual(resp.status_code, status.HTTP_401_UNAUTHORIZED)
        self.assertIn("Sesión no existente", resp.json()["detail"])  