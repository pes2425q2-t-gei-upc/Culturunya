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

    # - get_admin_with_less_messages  #
    @patch.object(us.User.objects, "filter")
    def test_get_admin_with_less_messages(self, mock_filter):
        qs_stub = MagicMock()
        qs_stub.annotate.return_value.order_by.return_value.first.return_value = "ADMIN"
        mock_filter.return_value = qs_stub
        self.assertEqual(us.get_corresponding_admin(), "ADMIN")

    # ---- create_message - #
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

    # ----- get_messages -- #
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

    # ----- create_report - #
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

    # ---- update_rank_* helpers ---- #
    def test_update_rank_from_adding_points(self):
        bottom = us.RANK_ORDER[0]         # 'unranked'
        top    = us.RANK_ORDER[-1]        # 'ramon_llull'
        self.assertEqual(
            us.update_rank_from_adding_points(bottom, 999999),
            top
        )
        self.assertEqual(
            us.update_rank_from_adding_points(top, 0),
            top
        )

    

    # --- leaderboards ---- #
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