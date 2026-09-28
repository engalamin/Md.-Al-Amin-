package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.QuizQuestion
import com.example.data.TopicCatalog
import com.example.data.TopicLesson
import com.example.data.local.AppDatabase
import com.example.data.local.QuizRecord
import com.example.data.local.SavedSolution
import com.example.data.local.UserProfile
import com.example.data.repository.MathpadRepository
import com.example.data.repository.SolvedResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
    DASHBOARD,
    LEARN,
    QUIZ,
    SAVED,
    PROFILE,
    SETTINGS
}

data class ChatMessage(
    val sender: String, // "user" or "tutor"
    val content: String
)

class MathpadViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = MathpadRepository(database.mathpadDao())

    // Active Screen
    private val _currentScreen = MutableStateFlow(ScreenTab.DASHBOARD)
    val currentScreen: StateFlow<ScreenTab> = _currentScreen.asStateFlow()

    // Screen backstack history for BackHandler support
    private val screenStack = mutableListOf(ScreenTab.DASHBOARD)

    // Data from database
    val savedSolutions: StateFlow<List<SavedSolution>> = repository.savedSolutions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizRecords: StateFlow<List<QuizRecord>> = repository.quizRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Solver state
    private val _problemText = MutableStateFlow("")
    val problemText: StateFlow<String> = _problemText.asStateFlow()

    private val _attachedBitmap = MutableStateFlow<Bitmap?>(null)
    val attachedBitmap: StateFlow<Bitmap?> = _attachedBitmap.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _studentGrade = MutableStateFlow("High school")
    val studentGrade: StateFlow<String> = _studentGrade.asStateFlow()

    private val _isSolving = MutableStateFlow(false)
    val isSolving: StateFlow<Boolean> = _isSolving.asStateFlow()

    private val _solutionResult = MutableStateFlow<SolvedResult?>(null)
    val solutionResult: StateFlow<SolvedResult?> = _solutionResult.asStateFlow()

    private val _isCurrentSaved = MutableStateFlow(false)
    val isCurrentSaved: StateFlow<Boolean> = _isCurrentSaved.asStateFlow()

    private val _followUpList = MutableStateFlow<List<ChatMessage>>(emptyList())
    val followUpList: StateFlow<List<ChatMessage>> = _followUpList.asStateFlow()

    private val _isFollowUpLoading = MutableStateFlow(false)
    val isFollowUpLoading: StateFlow<Boolean> = _isFollowUpLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Learn tab state
    private val _selectedTopicLesson = MutableStateFlow<TopicLesson?>(null)
    val selectedTopicLesson: StateFlow<TopicLesson?> = _selectedTopicLesson.asStateFlow()

    private val _topicFilter = MutableStateFlow("All")
    val topicFilter: StateFlow<String> = _topicFilter.asStateFlow()

    // Quiz tab state
    private val _quizTopic = MutableStateFlow("Quadratic Equations")
    val quizTopic: StateFlow<String> = _quizTopic.asStateFlow()

    private val _quizDifficulty = MutableStateFlow("Medium")
    val quizDifficulty: StateFlow<String> = _quizDifficulty.asStateFlow()

    private val _isQuizLoading = MutableStateFlow(false)
    val isQuizLoading: StateFlow<Boolean> = _isQuizLoading.asStateFlow()

    private val _quizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val quizQuestions: StateFlow<List<QuizQuestion>> = _quizQuestions.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)
    val selectedOption: StateFlow<Int?> = _selectedOption.asStateFlow()

    private val _isAnswerRevealed = MutableStateFlow(false)
    val isAnswerRevealed: StateFlow<Boolean> = _isAnswerRevealed.asStateFlow()

    private val _quizScore = MutableStateFlow(0)
    val quizScore: StateFlow<Int> = _quizScore.asStateFlow()

    private val _isQuizCompleted = MutableStateFlow(false)
    val isQuizCompleted: StateFlow<Boolean> = _isQuizCompleted.asStateFlow()

    // Saved detail sheet state
    private val _selectedSavedSolution = MutableStateFlow<SavedSolution?>(null)
    val selectedSavedSolution: StateFlow<SavedSolution?> = _selectedSavedSolution.asStateFlow()

    fun navigateTo(tab: ScreenTab) {
        if (_currentScreen.value != tab) {
            screenStack.add(tab)
            _currentScreen.value = tab
        }
    }

    fun handleBack(): Boolean {
        if (_selectedSavedSolution.value != null) {
            _selectedSavedSolution.value = null
            return true
        }
        if (_selectedTopicLesson.value != null) {
            _selectedTopicLesson.value = null
            return true
        }
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun setProblemText(text: String) {
        _problemText.value = text
        _errorMessage.value = null
    }

    fun setAttachedBitmap(bitmap: Bitmap?) {
        _attachedBitmap.value = bitmap
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun setStudentGrade(grade: String) {
        _studentGrade.value = grade
    }

    fun solve() {
        val text = _problemText.value.trim()
        val bitmap = _attachedBitmap.value
        if (text.isBlank() && bitmap == null) {
            _errorMessage.value = "Please type a problem or attach a photo first."
            return
        }

        _errorMessage.value = null
        _isSolving.value = true
        _solutionResult.value = null
        _isCurrentSaved.value = false
        _followUpList.value = emptyList()

        viewModelScope.launch {
            try {
                val result = repository.solveProblem(
                    problemText = if (text.isNotBlank()) text else "Solve the homework problem in the attached photo.",
                    bitmap = bitmap,
                    language = _selectedLanguage.value,
                    gradeLevel = _studentGrade.value
                )
                _solutionResult.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to solve problem"
            } finally {
                _isSolving.value = false
            }
        }
    }

    fun askFollowUp(promptText: String) {
        val solution = _solutionResult.value?.markdownText ?: return
        if (promptText.isBlank()) return

        val userMsg = ChatMessage("user", promptText)
        _followUpList.value = _followUpList.value + userMsg
        _isFollowUpLoading.value = true

        viewModelScope.launch {
            try {
                val reply = repository.askFollowUp(
                    currentSolution = solution,
                    followUpPrompt = promptText,
                    gradeLevel = _studentGrade.value
                )
                _followUpList.value = _followUpList.value + ChatMessage("tutor", reply)
            } catch (e: Exception) {
                _followUpList.value = _followUpList.value + ChatMessage("tutor", "I ran into a hiccup answering that: ${e.message}")
            } finally {
                _isFollowUpLoading.value = false
            }
        }
    }

    fun saveCurrentSolution() {
        val res = _solutionResult.value ?: return
        viewModelScope.launch {
            repository.saveSolution(
                title = res.title,
                content = res.markdownText,
                gradeLevel = _studentGrade.value
            )
            _isCurrentSaved.value = true
        }
    }

    fun clearProblem() {
        _problemText.value = ""
        _attachedBitmap.value = null
        _solutionResult.value = null
        _followUpList.value = emptyList()
        _errorMessage.value = null
        _isCurrentSaved.value = false
    }

    // Learn actions
    fun setTopicFilter(category: String) {
        _topicFilter.value = category
    }

    fun selectTopicLesson(lesson: TopicLesson?) {
        _selectedTopicLesson.value = lesson
    }

    fun quizMeOnLesson(lesson: TopicLesson) {
        _selectedTopicLesson.value = null
        _quizTopic.value = lesson.title
        navigateTo(ScreenTab.QUIZ)
        startQuiz()
    }

    // Quiz actions
    fun setQuizTopic(topic: String) {
        _quizTopic.value = topic
    }

    fun setQuizDifficulty(diff: String) {
        _quizDifficulty.value = diff
    }

    fun startQuiz() {
        startReinforcementQuiz(_quizTopic.value, null)
    }

    fun startReinforcementQuiz(topic: String, solvedContent: String? = null) {
        _quizTopic.value = topic
        _isQuizLoading.value = true
        _isQuizCompleted.value = false
        _quizScore.value = 0
        _currentQuestionIndex.value = 0
        _selectedOption.value = null
        _isAnswerRevealed.value = false
        navigateTo(ScreenTab.QUIZ)

        viewModelScope.launch {
            try {
                val questions = repository.generateReinforcementQuiz(
                    topic = topic,
                    solvedContent = solvedContent,
                    difficulty = _quizDifficulty.value,
                    gradeLevel = _studentGrade.value
                )
                _quizQuestions.value = questions
            } catch (e: Exception) {
                _quizQuestions.value = TopicCatalog.getQuizQuestionsForTopic(topic, _quizDifficulty.value)
            } finally {
                _isQuizLoading.value = false
            }
        }
    }

    fun answerQuizQuestion(optionIndex: Int) {
        if (_isAnswerRevealed.value) return
        _selectedOption.value = optionIndex
        _isAnswerRevealed.value = true
        val currentQ = _quizQuestions.value.getOrNull(_currentQuestionIndex.value) ?: return
        if (optionIndex == currentQ.correctIndex) {
            _quizScore.value += 1
        }
    }

    fun nextQuizQuestion() {
        if (_currentQuestionIndex.value + 1 < _quizQuestions.value.size) {
            _currentQuestionIndex.value += 1
            _selectedOption.value = null
            _isAnswerRevealed.value = false
        } else {
            _isQuizCompleted.value = true
            viewModelScope.launch {
                repository.saveQuizResult(
                    topic = _quizTopic.value,
                    difficulty = _quizDifficulty.value,
                    score = _quizScore.value,
                    total = _quizQuestions.value.size
                )
            }
        }
    }

    // Saved actions
    fun viewSavedSolution(solution: SavedSolution?) {
        _selectedSavedSolution.value = solution
    }

    fun deleteSavedSolution(id: Long) {
        viewModelScope.launch {
            repository.deleteSolution(id)
            if (_selectedSavedSolution.value?.id == id) {
                _selectedSavedSolution.value = null
            }
        }
    }

    // Profile actions
    fun saveProfile(name: String, grade: String) {
        viewModelScope.launch {
            repository.saveProfile(name = name.trim(), grade = grade)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            clearProblem()
            _selectedSavedSolution.value = null
            _selectedTopicLesson.value = null
        }
    }
}
