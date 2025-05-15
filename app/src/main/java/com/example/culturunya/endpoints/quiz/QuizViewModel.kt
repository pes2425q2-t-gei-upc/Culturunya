package com.example.culturunya.endpoints.quiz

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.culturunya.R
import com.example.culturunya.controllers.Api
import com.example.culturunya.controllers.UserRepository
import com.example.culturunya.models.quiz.QuizQuestion
import com.example.culturunya.models.currentSession.CurrentSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

data class QuizState(
    val currentQuestion: QuizQuestion? = null,
    val currentPoints: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCorrectAnimation: Boolean = false,
    val showIncorrectAnimation: Boolean = false
)

class QuizViewModel : ViewModel() {
    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state

    private val repository = UserRepository(Api.instance)
    private lateinit var context: Context
    private var questions: List<QuizQuestion> = emptyList()

    fun setContext(context: Context) {
        this.context = context
        loadQuestionsFromJson()
        loadCurrentPoints()
    }

    private fun loadQuestionsFromJson() {
        try {
            val inputStream = context.assets.open("quiz/quiz_200.json")
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
                    question = if (CurrentSession.language == "es") 
                        questionObj.getString("question_es")
                    else 
                        questionObj.getString("question_en"),
                    options = optionsList,
                    correctAnswer = correctAnswer
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
                _state.value = _state.value.copy(currentPoints = userInfo.current_quiz_points)
            } catch (e: Exception) {
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
            showIncorrectAnimation = !isCorrect
        )

        // Actualitzar punts
        val newPoints = if (isCorrect) {
            _state.value.currentPoints + 1
        } else {
            maxOf(0, _state.value.currentPoints - 1)
        }
        
        _state.value = _state.value.copy(currentPoints = newPoints)

        // Després d'un moment, carregar nova pregunta
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500) // Temps per veure l'animació
            _state.value = _state.value.copy(
                showCorrectAnimation = false,
                showIncorrectAnimation = false
            )
            loadNewQuestion()
        }
    }

    fun savePoints() {
        viewModelScope.launch {
            try {
                // TODO: Implementar crida al servidor quan estigui disponible
                // Per ara només actualitzem CurrentSession
                CurrentSession.current_quiz_points = _state.value.currentPoints
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Error guardant els punts: ${e.message}")
            }
        }
    }
} 