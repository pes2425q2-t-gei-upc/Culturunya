from django.contrib import admin

# Register your models here.
from django.contrib import admin
from .models import User, Administrator

admin.site.register(User)
admin.site.register(Administrator)
