from rest_framework import serializers
from django.contrib.auth import get_user_model

from persistence.models import Report, ReportResolution, Rating

User = get_user_model()

class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = [
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
            'current_event_points',
            'current_quiz_points',
            'points_to_next_rank_event',
            'points_to_next_quiz_points',
        ]

class UserSimpleInfoSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['username', 'email', 'profile_pic']
    profile_pic = serializers.ImageField(read_only=True)

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