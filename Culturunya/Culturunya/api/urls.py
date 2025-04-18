
from django.urls import path
from .views import (
    test_api, data_test, post_test, put_test, delete_test,
    get_events, get_filtered_events, create_user, create_rating_endpoint,
    CustomObtainAuthToken, delete_own_account, ChangePasswordView, UserProfileView, get_conversation_with_admin,
    get_conversation_with_user, send_message_user_to_admin, send_message_admin_to_user,
)
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
    #un endpoint GET para obtener los eventos que un usuario tiene en su calendario personal
    #otro endpoint POST/DELETE para añadir/quitar eventos de su calendario personal
    path('create_user/', create_user, name='create_user'),

    path('ratings/', create_rating_endpoint, name='create_rating'),
    path('login/', CustomObtainAuthToken.as_view(), name='api_token_auth'),
    path('delete_account/', delete_own_account, name='delete_own_account'),
    path('change_password/', ChangePasswordView.as_view(), name='change_password'),
    path('user/profile/', UserProfileView.as_view(), name='user_profile'),
    path('chat/send_to_admin/', send_message_user_to_admin, name='send_to_admin'),
    path('chat/send_to_user/', send_message_admin_to_user, name='send_to_user'),
    path('chat/with_admin/', get_conversation_with_admin, name='get_conversation_with_admin'),
    path('chat/with_user/<int:user_id>', get_conversation_with_user, name='get_conversation_with_user'),
]
