
from django.urls import path
from .views import create_rating_endpoint, test_api, post_test, put_test, get_events, get_filtered_events, create_user, create_rating
from .views import data_test
from .views import delete_test
urlpatterns = [#se concatena con el path de url del proyecto
    path('test/', test_api, name='test_api'),
    path('data/', data_test, name='data_test'),
    path('post/', post_test, name='post_test' ),
    path('put/', put_test, name='put_test'),
    path('delete/', delete_test, name='delete_test'),
    path('events/', get_events, name='get_events'),
    path('events/filter/', get_filtered_events, name='get_filtered_events'),
    path('create_user/', create_user, name='create_user'),
    path('ratings/', create_rating_endpoint, name='create_rating'),

    #un endpoint GET para obtener los eventos que un usuario tiene en su calendario personal
    #otro endpoint POST/DELETE para añadir/quitar eventos de su calendario personal
]
