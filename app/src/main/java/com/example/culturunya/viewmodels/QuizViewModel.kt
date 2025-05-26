package com.example.culturunya.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.R
import com.example.culturunya.Api
import com.example.culturunya.repositories.UserRepository
import com.example.culturunya.dataclasses.quiz.QuizQuestion
import com.example.culturunya.session.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

data class QuizState(
    val currentQuestion: QuizQuestion? = null,
    val currentPoints: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCorrectAnimation: Boolean = false,
    val showIncorrectAnimation: Boolean = false,
    val selectedOption: Int? = null
)

class QuizViewModel : ViewModel() {
    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state

    private val repository = UserRepository(Api.instance)
    private lateinit var context: Context
    private var questions: List<QuizQuestion> = emptyList()
    private val numQuestions = 329  // Nombre total de preguntes

    fun setContext(context: Context) {
        this.context = context
        loadQuestionsFromJson()
        loadCurrentPoints()
    }

    private fun loadQuestionsFromJson() {
        try {
            val inputStream = context.assets.open("quiz/quiz.json")
            val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
            val questionsArray = JSONArray(jsonString)
            
            Log.d("QuizViewModel", "JSON loaded, questions array length: ${questionsArray.length()}")
            
            questions = List(questionsArray.length()) { index ->
                val questionObj = questionsArray.getJSONObject(index)
                val optionsArray = questionObj.getJSONArray("options")
                val optionsList = List(optionsArray.length()) { optionsArray.getString(it) }
                val correctAnswer = optionsList.indexOf(questionObj.getString("correct_answer"))
                
                Log.d("QuizViewModel", "Processing question $index: ${questionObj.getString("question_es")}")
                
                QuizQuestion(
                    id = questionObj.getInt("id"),
                    question = questionObj.getString("question_${CurrentSession.language.lowercase()}"),
                    options = optionsList,
                    correctAnswer = correctAnswer,
                    image = questionObj.optString("image").takeIf { it != "null" && it.isNotBlank() },
                    points = questionObj.getInt("points")
                )
            }
            
            Log.d("QuizViewModel", "Questions loaded: ${questions.size}")
            
        } catch (e: Exception) {
            Log.e("QuizViewModel", "Error loading questions", e)
            _state.value = _state.value.copy(error = context.getString(R.string.quizError, e.message))
        }
    }

    private fun loadCurrentPoints() {
        viewModelScope.launch {
            try {
                val userInfo = repository.getProfileInfo("Token ${CurrentSession.token}")
                _state.value = _state.value.copy(currentPoints = userInfo.total_quiz_points)
            } catch (e: Exception) {
                Log.e("QUIZ_DEBUG", "Error obtenint dades de l'usuari: ${e.message}")
                _state.value = _state.value.copy(error = context.getString(R.string.quizError, e.message))
            }
        }
    }

    fun loadNewQuestion() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                // Intentar obtenir pregunta del servidor
                val randomId = Random.nextInt(1, 201)
                // TODO: Implementar crida al servidor quan estigui disponible
                // Per ara, agafem una pregunta aleatòria del JSON
                val randomQuestion = questions.random()
                _state.value = _state.value.copy(
                    currentQuestion = randomQuestion,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = context.getString(R.string.quizError, e.message),
                    isLoading = false
                )
            }
        }
    }

    fun checkAnswer(selectedOption: Int) {
        val currentQuestion = _state.value.currentQuestion ?: return
        val isCorrect = selectedOption == currentQuestion.correctAnswer
        _state.value = _state.value.copy(
            showCorrectAnimation = isCorrect,
            showIncorrectAnimation = !isCorrect,
            selectedOption = selectedOption
        )
        // Actualitzar punts
        val increment = if (isCorrect) currentQuestion.points else -1
        val newPoints = maxOf(0, _state.value.currentPoints + increment)
        _state.value = _state.value.copy(currentPoints = newPoints)

        // Guardar punts
        savePoints(increment)
        
        // Després d'un moment, carregar nova pregunta
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Temps per veure l'animació
            _state.value = _state.value.copy(
                showCorrectAnimation = false,
                showIncorrectAnimation = false,
                selectedOption = null
            )
            loadNewQuestion()
        }
    }

    fun savePoints(increment: Int = 0) {
        Log.d("QUIZ_DEBUG", "Enviant PUT /user/get_points_quiz/ amb increment: $increment i token: ${CurrentSession.token}")
        viewModelScope.launch {
            try {
                val result = repository.setQuizPoints("Token "+CurrentSession.token, increment)
                Log.d("QUIZ_DEBUG", "Resposta del backend a PUT: $result")
                CurrentSession.current_quiz_points = _state.value.currentPoints
            } catch (e: Exception) {
                Log.e("QUIZ_DEBUG", "Error guardant els punts: ${e.message}")
                _state.value = _state.value.copy(error = "Error guardant els punts: "+e.message)
            }
        }
    }
} 