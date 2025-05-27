from rest_framework import serializers
from django.contrib.auth import get_user_model
from persistence.models import Report, ReportResolution, Rating, QuestionTranslation, User

User = get_user_model()

class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = [
            'id',
            'username',
            'first_name',
            'last_name',
            'email',
            'fullname',
            'phone_number',
            'profile_pic',
            'birth_date',
            'language',
            'rank_event',
            'rank_quiz',
            'total_event_points',
            'total_quiz_points',
            'banned_from_comments',
            'is_admin',
        ]

class UserSimpleInfoSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['username', 'email', 'profile_pic']
    profile_pic = serializers.ImageField(read_only=True, use_url=True)

class ProfilePicSerializer(serializers.ModelSerializer):
    class Meta:
        model  = User
        fields = ["profile_pic"]

class ChangePasswordSerializer(serializers.Serializer):
    old_password = serializers.CharField(required=True)
    new_password = serializers.CharField(required=True)

class GoogleAuthSerializer(serializers.Serializer):
    id_token = serializers.CharField()

class ReportSerializer(serializers.ModelSerializer):
    class Meta:
        model = Report
        fields = ['id', 'reported_user', 'comment', 'message', 'date', 'is_resolved']
        read_only_fields = ['id', 'reporter', 'comment', 'date', 'is_resolved']

class ReportResolutionSerializer(serializers.ModelSerializer):
    class Meta:
        model = ReportResolution
        fields = ['report', 'action', 'message']

class RatingSerializer(serializers.ModelSerializer):
    user = UserSimpleInfoSerializer('user', read_only=True)
    class Meta:
        model = Rating
        fields = ['user', 'id', 'date', 'rating', 'comment']

class QuestionTranslationSerializer(serializers.ModelSerializer):
    class Meta:
        model  = QuestionTranslation
        fields = ("id", "language", "text", "options")


class AdminChangeUserPasswordSerializer(serializers.Serializer):
    """
    Valida la peticion PUT del admin para cambiar la contrasena de otro usuario.
    """
    user_id = serializers.IntegerField()
    new_password = serializers.CharField(write_only=True, min_length=8)

    def validate_user_id(self, value):
        if not User.objects.filter(id=value).exists():
            raise serializers.ValidationError("Usuario no encontrado.")
        return value