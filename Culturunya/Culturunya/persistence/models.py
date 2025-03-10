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