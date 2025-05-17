import json
from pathlib import Path
from django.core.files import File
from persistence.models import Question, QuestionTranslation  # cambia 'myapp' por tu app

# Ruta al JSON
file_path = Path("persistence/quiz_200.json")

with open(file_path, "r", encoding="utf-8") as f:
    data = json.load(f)

for item in data:
    # Crear pregunta base
    question = Question(question_es=item["question_es"])

    # ⬇️ Añadir imagen si existe
    if item.get("image"):
        image_path = Path("media/questions") / item["image"]  # Ajusta según tu estructura
        if image_path.exists():
            with open(image_path, "rb") as img_file:
                question.image.save(item["image"], File(img_file), save=False)

    question.save()

    # Crear traducción en español
    QuestionTranslation.objects.create(
        question=question,
        language="ES",
        text=item["question_es"],
        options=item["options"]
    )

    # Crear traducción en inglés
    QuestionTranslation.objects.create(
        question=question,
        language="EN",
        text=item["question_en"],
        options=item["options"]
    )
