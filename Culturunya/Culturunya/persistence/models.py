from django.db import models


class Location(models.Model):
    longitude = models.FloatField()
    latitude = models.FloatField()
    address = models.CharField(max_length=255)
    city = models.CharField(max_length=100)
    comarca = models.CharField(max_length=100)
    province = models.CharField(max_length=100)

    def __str__(self):
        return f"{self.address}, {self.city}, {self.province}"

class Category(models.Model):
    TYPE_CATEGORY_CHOICES = [
        ('Comedy', 'Comedy'),
        ('Theater', 'Theater'),
        ('Cinema', 'Cinema'),
        ('ForChildren', 'ForChildren'),
        ('Exposition', 'Exposition'),
        ('Show', 'Show'),
        ('Carnival', 'Carnival'),
        ('Concert', 'Concert'),
        ('HolidayEvent', 'Holiday Event'),
        ('Festival', 'Festival'),
        ('Dance', 'Dance'),
        ('Conference', 'Conference'),
    ]
    category = models.CharField(max_length=255, choices=TYPE_CATEGORY_CHOICES, unique=True)
    def __str__(self):
        return f"{self.category}"

class Event(models.Model):
    id = models.CharField(max_length=255, primary_key=True)
    name = models.CharField(max_length=255)
    date_start = models.DateTimeField()
    date_end = models.DateTimeField()
    description = models.TextField()
    price = models.FloatField()
    categories = models.ManyToManyField(Category, related_name='events')

    def __str__(self):
        return f"{self.name}"