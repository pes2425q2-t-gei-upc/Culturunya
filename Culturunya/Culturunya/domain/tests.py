# -*- coding: utf-8 -*-
"""
Tests unitarios para domain.users_service
Compatibles con manage.py test.
"""

from datetime import datetime, timedelta
from types import SimpleNamespace
from unittest.mock import MagicMock, patch, PropertyMock

from django.test import TestCase

import domain.users_service as us


# --- #
#                               DUMMIES / STUBS                               #
# --- #
class DummyEvent(SimpleNamespace):
    def to_dict(self):
        return {"id": self.id, "name": self.name}


class DummyUser(SimpleNamespace):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.received_messages = []
        self.sent_messages = []
        self.is_banned = False

    def save(self):
        pass

    def set_password(self, _pwd):
        pass


def _fake_report(resolved=False):
    return SimpleNamespace(
        id=5,
        reported_user=DummyUser(id=9),
        is_resolved=resolved,
        save=lambda: None,
    )

class DummyRating(SimpleNamespace):
    pass


# --- #
#                               TEST SUITE                                    #
# --- #
class SampleTests(TestCase):
    """Prueba de humo (verifica que el runner descubre tests)."""

    def test_dummy(self):
        self.assertEqual(1 + 1, 2)


class UsersServiceTests(TestCase):
    # --- get_all_events --- #
    @patch.object(us.Event.objects, "all")
    def test_get_all_events_returns_dicts(self, mock_all):
        mock_all.return_value = [
            DummyEvent(id=1, name="A"),
            DummyEvent(id=2, name="B"),
        ]
        expected = [{"id": 1, "name": "A"}, {"id": 2, "name": "B"}]

        result = us.get_all_events()
        self.assertEqual(result, expected)

    # ---- filter_events -- #
    @patch.object(us.Event.objects, "filter")
    def test_filter_events_calls_queryset(self, mock_filter):
        # el queryset devuelto por filter().distinct()
        mock_filter.return_value.distinct.return_value = [
            DummyEvent(id=99, name="X")
        ]
        filters = {"categories": "Music"}
        result = us.filter_events(filters)

        self.assertEqual(result, [{"id": 99, "name": "X"}])
        mock_filter.assert_called()  # se llam al ORM

    #  create_user_service  #
    @patch.object(us, "PersonalCalendar")
    @patch.object(us, "User")
    def test_create_user_service_crea_user_y_calendario(self, mock_user_cls,
                                                        mock_calendar):
        fake_user = DummyUser(id=77, username="u")
        mock_user_cls.return_value = fake_user
        fake_user.save = MagicMock()
        fake_user.set_password = MagicMock()

        data = {
            "username": "u",
            "password": "pwd",
            "email": "e@test.com",
            "fullname": "Nombre",
        }
        created = us.create_user_service(data)

        self.assertIs(created, fake_user)
        fake_user.set_password.assert_called_once_with("pwd")
        fake_user.save.assert_called_once()
        mock_calendar.objects.create.assert_called_once_with(user=fake_user)

    # --- create_rating --- #
    @patch.object(us.Rating.objects, "create")
    @patch.object(us.User.objects, "get")
    @patch.object(us.Event.objects, "get")
    def test_create_rating_success(self, mock_ev_get, mock_user_get, mock_create):
        dummy_event = DummyEvent(id=1)
        dummy_user = DummyUser(id=2)
        mock_ev_get.return_value = dummy_event
        mock_user_get.return_value = dummy_user
        mock_create.return_value = "RATING"

        result = us.create_rating(1, 2, "LIKE", "Nice")
        self.assertEqual(result, "RATING")
        mock_create.assert_called_once_with(
            event=dummy_event, user=dummy_user, rating="LIKE", comment="Nice"
        )

    @patch.object(us.Event.objects, "get", side_effect=us.ObjectDoesNotExist)
    def test_create_rating_value_error(self, _mock_ev_get):
        with self.assertRaises(ValueError):
            us.create_rating(1, 2, "LIKE")


    @patch.object(us.User.objects, "filter")
    def test_get_admin_with_less_messages(self, mock_filter):
        qs_stub = MagicMock()
        qs_stub.annotate.return_value.order_by.return_value.first.return_value = "ADMIN"
        mock_filter.return_value = qs_stub
        self.assertEqual(us.get_admin_with_less_messages(), "ADMIN")


    @patch.object(us.Message.objects, "create")
    @patch.object(us.User.objects, "get")
    def test_create_message_ok(self, mock_user_get, mock_msg_create):
        sender = DummyUser(id=1)
        receiver = DummyUser(id=2)
        mock_user_get.side_effect = [sender, receiver]
        mock_msg_create.return_value = "MSG"

        self.assertEqual(us.create_message(1, 2, "hi"), "MSG")
        mock_msg_create.assert_called_once()

    @patch.object(us.User.objects, "get", side_effect=us.ObjectDoesNotExist)
    def test_create_message_usuario_not_found(self, _mock_user_get):
        with self.assertRaises(ValueError):
            us.create_message(1, 2, "hi")


    @patch.object(us.Message.objects, "filter")
    @patch.object(us.User.objects, "get")
    def test_get_messages_ok(self, mock_user_get, mock_filter):
        u1 = DummyUser(id=1)
        u2 = DummyUser(id=2)
        mock_user_get.side_effect = [u1, u2]

        qs_stub = MagicMock()
        mock_filter.return_value.order_by.return_value = qs_stub

        result = us.get_messages(1, 2)
        self.assertIs(result, qs_stub)

    @patch.object(us.User.objects, "get", side_effect=us.ObjectDoesNotExist)
    def test_get_messages_error_user_missing(self, _mock_user_get):
        with self.assertRaises(ValueError):
            us.get_messages(1, 2)


    def _dummy_report_serializer(self, is_valid=True):
        ser = MagicMock()
        ser.is_valid.return_value = is_valid
        ser.errors = {"field": ["err"]}
        return ser

    @patch.object(us.ReportSerializer, "__init__", return_value=None)
    @patch.object(us.ReportSerializer, "is_valid", return_value=True)
    @patch.object(us.ReportSerializer, "save")
    @patch.object(us.Rating.objects, "get")
    def test_create_report_ok(self, mock_rating_get, _init, _is_valid, _save):
        rating = DummyRating(id=1, user=DummyUser(id=7), comment="spam")
        mock_rating_get.return_value = rating

        msg, code = us.create_report(
            {"rating_id": 1, "message": "bad"}, DummyUser(id=3)
        )
        self.assertEqual(code, 201)
        self.assertIn("Reporte", msg["message"])

    @patch.object(us.Rating.objects, "get")
    def test_create_report_self_report(self, mock_rating_get):
        user = DummyUser(id=5)
        rating = DummyRating(id=1, user=user, comment="x")
        mock_rating_get.return_value = rating

        msg, code = us.create_report({"rating_id": 1, "message": "bad"}, user)
        self.assertEqual(code, 403)
        self.assertIn("mismo", msg["error"])

    @patch.object(us.Rating.objects, "get", side_effect=us.ObjectDoesNotExist)
    def test_create_report_rating_not_found(self, _mock_rating_get):
        with self.assertRaises(ValueError):
            us.create_report({"rating_id": 9, "message": "x"}, DummyUser(id=1))


    def test_update_rank_from_adding_points(self):
        bottom = us.RANK_ORDER[0]         
        top    = us.RANK_ORDER[-1]        
        self.assertEqual(
            us.update_rank_from_adding_points(bottom, 999999),
            top
        )
        self.assertEqual(
            us.update_rank_from_adding_points(top, 0),
            top
        )

    


    @patch.object(us.User.objects, "order_by")
    def test_leaderboards(self, mock_order_by):
        users = [
            DummyUser(
                username="a",
                profile_pic=None,
                rank_event="gold",
                total_event_points=9,
                rank_quiz="silver",
                total_quiz_points=9,
            ),
            DummyUser(
                username="b",
                profile_pic=None,
                rank_event="silver",
                total_event_points=5,
                rank_quiz="bronze",
                total_quiz_points=5,
            ),
        ]
        mock_order_by.return_value = users

        ev_ld = us.get_events_ranking_leaderboard()
        qz_ld = us.get_quiz_ranking_leaderboard()

        self.assertEqual(ev_ld[0]["username"], "a")
        self.assertEqual(qz_ld[1]["username"], "b")
        self.assertEqual(ev_ld[0]["position"], 1)
        self.assertEqual(qz_ld[-1]["position"], len(users))

    @patch.object(us.Event.objects, "filter")
    def test_filter_events_category_dates_geo_ok(self, mock_filter):
        """categories + date ranges + geofencing (flujo nominal)"""
        dummy = DummyEvent(id=1, name="e")
        mock_filter.return_value.distinct.return_value = [dummy]

        result = us.filter_events({
            "categories": "Music,Art",
            "date_start_range": "2024-01-01",
            "date_end_range":   "2024-12-31",
            "latitude":  "41.0",
            "longitude": "2.0",
            "range":     "1.0",
        })

        self.assertEqual(result, [{"id": 1, "name": "e"}])
        mock_filter.assert_called_once()  

    @patch.object(us.Event.objects, "filter")
    def test_filter_events_invalid_dates_and_geo(self, mock_filter):
        """Dispara ValueError por fechas y lat/long no numericos"""
        mock_filter.return_value.distinct.return_value = [] 

        us.filter_events({
            "date_start_range": "BAD-DATE",
            "latitude":  "not-num",
            "longitude": "2.0",
            "range":     "1.0",
        })

        mock_filter.assert_called_once()                   
    def test_create_resolved_report_unauthorized(self):
        msg, code = us.create_resolved_report({}, DummyUser(is_admin=False), 5)
        self.assertEqual(code, 403)
        self.assertIn("No autorizado", msg["error"])

    @patch.object(us.Report.objects, "get", side_effect=us.Report.DoesNotExist)
    def test_create_resolved_report_not_found(self, _m_get):
        msg, code = us.create_resolved_report({}, DummyUser(is_admin=True), 99)
        self.assertEqual(code, 404)
        self.assertIn("no encontrado", msg["error"].lower())

    @patch.object(us.Report.objects, "get", return_value=_fake_report(resolved=True))
    def test_create_resolved_report_already_resolved(self, _m_get):
        msg, code = us.create_resolved_report({}, DummyUser(is_admin=True), 5)
        self.assertEqual(code, 400)
        self.assertIn("ya ha sido resuelto", msg["error"])

  
    @patch("domain.users_service.create_message", return_value=None)
    @patch.object(us.ReportResolutionSerializer, "save", return_value=None)
    @patch.object(us.ReportResolutionSerializer, "is_valid", return_value=True)
    @patch.object(us.Report.objects, "get", return_value=_fake_report())
    def test_create_resolved_report_warning_ok(
            self, _m_get_report, _m_is_valid, _m_save, _m_msg):
        admin = DummyUser(id=1, is_admin=True)
        msg, code = us.create_resolved_report(
            {"action": "Warning", "message": "cuidado"}, admin, 5
        )
        self.assertEqual(code, 200)
        self.assertIn("Report resuelto", msg["message"])
        _m_msg.assert_called_once()


    @patch("domain.users_service.create_message", return_value=None)
    @patch.object(us.Rating.objects, "filter")     
    @patch.object(us.User.objects, "get", return_value=DummyUser(id=9))
    @patch.object(us.ReportResolutionSerializer, "save", return_value=None)
    @patch.object(us.ReportResolutionSerializer, "is_valid", return_value=True)
    @patch.object(us.Report.objects, "get", return_value=_fake_report())
    def test_create_resolved_report_ban_ok(
            self, _m_get_report, _m_is_valid, _m_save, _m_user_get,
            _m_rating_filter, _m_msg):
        admin = DummyUser(id=1, is_admin=True)
        msg, code = us.create_resolved_report(
            {"action": "Ban", "message": "bloqueado"}, admin, 5
        )
        self.assertEqual(code, 200)
        _m_rating_filter.assert_called_once()
        _m_msg.assert_called_once()


    @patch.object(us.ReportResolutionSerializer, "is_valid", return_value=False)
    @patch.object(us.ReportResolutionSerializer, "errors", {"foo": ["bar"]})
    @patch.object(us.Report.objects, "get", return_value=_fake_report())
    def test_create_resolved_report_invalid_serializer(self, _m_get, _m_is_valid):
        admin = DummyUser(id=1, is_admin=True)
        msg, code = us.create_resolved_report({"action": "X"}, admin, 5)
        self.assertEqual(code, 400)
        self.assertIn("error", msg)

LOWEST  = us.RANK_ORDER[0]        
HIGHEST = us.RANK_ORDER[-1]       
PENULT  = us.RANK_ORDER[-2]       


class TestRankHelpers(TestCase):
    

    def test_rank_up_to_top(self):
        """
        Con suficientes puntos se asciende del rango mas bajo al mas alto.
        """
        many_points = max(us.RANK_POINTS.values()) + 1
        new_rank = us.update_rank_from_adding_points(LOWEST, many_points)
        self.assertEqual(new_rank, HIGHEST)

    def test_rank_add_points_no_change(self):
        """
        Si ya esta en el tope y los puntos no superan el siguiente umbral (no existe),
        el rango permanece igual.
        """
        still_top = us.update_rank_from_adding_points(HIGHEST, 0)
        self.assertEqual(still_top, HIGHEST)



    def test_rank_down_one_level(self):
        """
        Con pocos puntos baja exactamente un nivel: de HIGHEST - PENULT.
        Elegimos puntos en el limite inferior del penultimo rango.
        """
        edge_points = us.RANK_POINTS[PENULT]
        new_rank = us.update_rank_from_decreasing_points(HIGHEST, edge_points)
        self.assertEqual(new_rank, PENULT)

    def test_rank_down_no_change_at_bottom(self):
        """
        Si ya esta en el rango mas bajo y 'pierde' puntos, se mantiene.
        """
        stay = us.update_rank_from_decreasing_points(LOWEST, 0)
        self.assertEqual(stay, LOWEST)

    @patch.object(us.Message.objects, "filter")
    def test_get_messages_admin_invoca_queryset(self, mock_filter):
        admin = DummyUser(id=99)
        qs_stub = MagicMock()
        mock_filter.return_value.order_by.return_value = qs_stub

        result = us.get_messages_admin(admin)

        
        mock_filter.assert_called_once()
        self.assertIs(result, qs_stub)
    
    def test_update_rank_from_adding_points_sin_subir(self):
        """Puntos insuficientes: se entra en el else y 'established' pasa a True."""
        start_rank = us.RANK_ORDER[0]                
        pocos_puntos = 0                             
        self.assertEqual(
            us.update_rank_from_adding_points(start_rank, pocos_puntos),
            start_rank                               
        )

    @patch.object(us.Event.objects, "filter")
    def test_filter_events_error_date_end_range(self, mock_filter):
        mock_filter.return_value.distinct.return_value = []
        us.filter_events({"date_end_range": "31-12-2024"})
        mock_filter.assert_called_once()


    @patch.object(us.Event.objects, "filter")
    def test_filter_events_error_geo_values(self, mock_filter):
        mock_filter.return_value.distinct.return_value = []
        us.filter_events({"latitude": "bad", "longitude": "2.0", "range": "x"})
        mock_filter.assert_called_once()

    @patch.object(us.Rating.objects, "get")
    @patch.object(us, "ReportSerializer")
    def test_create_report_serializer_invalido_devuelve_400(
        self, mock_serializer_cls, mock_rating_get
    ):
       
        rating = DummyRating(id=1, user=DummyUser(id=77), comment="spam")
        mock_rating_get.return_value = rating


        ser_stub = MagicMock()
        ser_stub.is_valid.return_value = False
        ser_stub.errors = {"field": ["invalid"]}
        mock_serializer_cls.return_value = ser_stub


        msg, code = us.create_report({"rating_id": 1, "message": "xxx"}, DummyUser(id=10))


        self.assertEqual(code, 400)
        self.assertEqual(msg, {"error": ser_stub.errors})
        ser_stub.is_valid.assert_called_once()
        mock_serializer_cls.assert_called_once()


