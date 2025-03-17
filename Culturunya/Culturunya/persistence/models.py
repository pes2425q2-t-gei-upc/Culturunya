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
            "location": self.location.id if self.location else None
        }

    def __str__(self):
        return f"{self.name}"