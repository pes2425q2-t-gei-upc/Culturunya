
from django.urls import path
from .views import test_api
from .views import data_test
from .views import delete_test
urlpatterns = [#se concatena con el path de url del proyecto
    path('test/', test_api, name='test_api'),
    path('data/', data_test, name='data_test'),
    path('delete/', delete_test, name='delete_test'),
]
