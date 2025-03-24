from django.contrib.auth.models import AbstractUser
from django.db import models


class Location(models.Model):
    longitude = models.FloatField()
    latitude = models.FloatField()
    address = models.CharField(max_length=255)
    city = models.CharField(max_length=100)
    comarca = models.CharField(max_length=100)
    province = models.CharField(max_length=100)

    class Meta:
        constraints = [
            models.UniqueConstraint(fields=['longitude', 'latitude'], name='unique_lat_long')
        ]

    def __str__(self):
        return f"{self.address}, {self.city}, {self.province}"


class Category(models.Model):
    name = models.CharField(max_length=255, unique=True)

    def __str__(self):
        return self.name

    @staticmethod
    def get_or_create_category(category_name):
        category, created = Category.objects.get_or_create(name=category_name)
        return category

class Event(models.Model):
    id = models.CharField(max_length=255, primary_key=True)
    name = models.CharField(max_length=255)
    date_start = models.DateTimeField()
    date_end = models.DateTimeField()
    description = models.TextField()
    price = models.DecimalField(max_digits=10, decimal_places=2)
    location = models.ForeignKey(Location, on_delete=models.CASCADE, related_name='events', null=True)
    categories = models.ManyToManyField(Category, related_name='events')

    def to_dict(self):
        return {
            "id": self.id,
            "name": self.name,
            "date_start": self.date_start.isoformat(),
            "date_end": self.date_end.isoformat(),
            "description": self.description,
            "price": self.price,
            "location": {
                "city": self.location.city if self.location else None,
                "address": self.location.address if self.location else None,
                "latitude": self.location.latitude if self.location else None,
                "longitude": self.location.longitude if self.location else None,
            } if self.location else None,  # Evitar error si no hay location
            "categories": [cat.name for cat in self.categories.all()]  # Agregar categor�as
        }

    def __str__(self):
        return f"{self.name}"

class TypeRank(models.TextChoices):
    UNRANKED = "Unranked", "Sin Rango"
    BRONZE = "Bronze", "Bronce"
    SILVER = "Silver", "Plata"
    GOLD = "Gold", "Oro"
    RAMON_LLULL = "RamonLlull", "Ramon Llull"

POINTS_TO_NEXT_RANK = {
    TypeRank.UNRANKED: 100, #100 puntos para subir a bronce y consecutivamente
    TypeRank.BRONZE: 200,
    TypeRank.SILVER: 500,
    TypeRank.GOLD: 1000,
    TypeRank.RAMON_LLULL: None,  # Último rango, no sube más
}

class TypeRating(models.TextChoices):
    BAD = "Bad", "Malo"
    MEDIOCRE = "Mediocre", "Mediocre"
    KINDA_FUN = "KindaFun", "Entretenido"
    FUN = "Fun", "Divertido"
    AWESOME = "Awesome", "Increíble"

class Action(models.TextChoices):
    NO_ACTION = "NoAction", "Ninguna acción"
    WARNING = "Warning", "Aviso"
    BAN = "Ban", "Bloqueo"

class User(AbstractUser):
    email = models.EmailField(unique=True)
    phone_number = models.CharField(max_length=15, blank=True, null=True)
    profile_pic = models.ImageField(upload_to="profiles/", blank=True, null=True)
    birth_date = models.DateField(blank=True, null=True)
    language = models.CharField(max_length=20, choices=[("ES", "Español"), ("CAT", "Català")])
    rank_event = models.CharField(max_length=20, choices=TypeRank)
    rank_quiz = models.CharField(max_length=20, choices=TypeRating)
    current_event_points = models.IntegerField(default=0)
    current_quiz_points = models.IntegerField(default=0)
    points_to_next_rank_event = models.IntegerField(default=POINTS_TO_NEXT_RANK[TypeRank.UNRANKED])
    points_to_next_quiz_points = models.IntegerField(default=POINTS_TO_NEXT_RANK[TypeRank.UNRANKED])
    banned_from_comments = models.BooleanField(default=False)

    groups = models.ManyToManyField(
        "auth.Group",
        related_name="custom_user_groups",
        blank=True
    )
    user_permissions = models.ManyToManyField(
        "auth.Permission",
        related_name="custom_user_permissions",
        blank=True
    )

    def __str__(self):
        return self.username

class Administrator(User):
    pass

class Message(models.Model):
    text = models.TextField()
    read = models.BooleanField(default=False)
    date_written = models.DateTimeField(auto_now_add=True)
    date_received = models.DateTimeField(null=True, blank=True)
    date_read = models.DateTimeField(null=True, blank=True)

    def __str__(self):
        return f"Message: {self.text[:30]}..."

# Quiz Model
class Quiz(models.Model):
    points = models.IntegerField()

    def __str__(self):
        return f"Quiz with {self.points} points"

# Question Model
class Question(models.Model):
    quiz = models.ForeignKey(Quiz, on_delete=models.CASCADE, related_name="questions")
    question = models.TextField()
    answer = models.JSONField()
    points = models.IntegerField()

    def __str__(self):
        return self.question

# Participation Model
class Participation(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="participations")
    date = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Participation of {self.user.username} on {self.date}"

# Rating Model
class Rating(models.Model):
    user_email = models.EmailField()
    date = models.DateField(auto_now_add=True)
    comment = models.TextField(blank=True, null=True)
    rating = models.CharField(max_length=20, choices=TypeRating)

    def __str__(self):
        return f"Rating by {self.user_email}: {self.rating}"

# Report Model
class Report(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name="reports")
    message = models.TextField()
    date = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Report by {self.user.username} on {self.date}"

# ReportResolution Model
class ReportResolution(models.Model):
    report = models.OneToOneField(Report, on_delete=models.CASCADE, related_name="resolution")
    action = models.CharField(max_length=20, choices=Action)
    message = models.TextField()

    def __str__(self):
        return f"Resolution for Report {self.report.id}: {self.action}"