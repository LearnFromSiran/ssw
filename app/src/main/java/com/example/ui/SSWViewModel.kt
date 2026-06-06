package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.db.AppRepository
import com.example.db.FavoriteWord
import com.example.model.QuizQuestion
import com.example.model.StudyChapter
import com.example.model.StudyData
import com.example.model.Vocabulary
import com.example.api.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SSWViewModel(private val repository: AppRepository) : ViewModel() {

    // --- User Profile Flow ---
    val userProfile = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // --- Saved / Favorite Words Flow ---
    val favoriteWords = repository.favoriteWords.map { list ->
        list.map { it.wordId }.toSet()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptySet()
    )

    // --- Quiz History Flow ---
    val quizHistory = repository.quizHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- Vocabulary Search and Categories Filter ---
    private val _vocabSearchQuery = MutableStateFlow("")
    val vocabSearchQuery = _vocabSearchQuery.asStateFlow()

    private val _selectedVocabCategory = MutableStateFlow("All")
    val selectedVocabCategory = _selectedVocabCategory.asStateFlow()

    val filteredVocabularies: StateFlow<List<Vocabulary>> = combine(
        _vocabSearchQuery,
        _selectedVocabCategory
    ) { query, category ->
        StudyData.vocabularies.filter { vocab ->
            val matchesCategory = category == "All" || vocab.category == category
            val matchesQuery = query.isEmpty() || 
                    vocab.word.contains(query, ignoreCase = true) ||
                    vocab.translation.contains(query, ignoreCase = true) ||
                    vocab.pronunciation.contains(query, ignoreCase = true) ||
                    vocab.romaji.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StudyData.vocabularies
    )

    // --- Quiz Mode State Management ---
    private val _activeQuizQuestionIndex = MutableStateFlow(0)
    val activeQuizQuestionIndex = _activeQuizQuestionIndex.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex = _selectedAnswerIndex.asStateFlow()

    private val _isAnswerSubmitted = MutableStateFlow(false)
    val isAnswerSubmitted = _isAnswerSubmitted.asStateFlow()

    private val _quizCorrectAnswersCount = MutableStateFlow(0)
    val quizCorrectAnswersCount = _quizCorrectAnswersCount.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished = _isQuizFinished.asStateFlow()

    val currentQuizQuestions = StudyData.quizQuestions // Standard full set

    // --- AI Tutor State ---
    private val _aiTutorResponse = MutableStateFlow<String?>(null)
    val aiTutorResponse = _aiTutorResponse.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading = _isAiLoading.asStateFlow()

    private val _aiTutorHistory = MutableStateFlow<List<Pair<String, Boolean>>>(emptyList()) // Pair of Prompt, IsUser
    val aiTutorHistory = _aiTutorHistory.asStateFlow()

    // --- Profile Save ---
    fun saveUserProfile(name: String, language: String) {
        viewModelScope.launch {
            repository.saveProfile(name, language)
        }
    }

    // --- Favorites Toggle ---
    fun toggleFavorite(wordId: Int) {
        viewModelScope.launch {
            val currentlyFavorites = favoriteWords.value
            val isFav = currentlyFavorites.contains(wordId)
            repository.toggleFavorite(wordId, isFav)
        }
    }

    // --- Search updates ---
    fun updateSearchQuery(query: String) {
        _vocabSearchQuery.value = query
    }

    fun updateSelectedCategory(category: String) {
        _selectedVocabCategory.value = category
    }

    // --- Quiz logic ---
    fun selectQuizAnswer(index: Int) {
        if (!_isAnswerSubmitted.value) {
            _selectedAnswerIndex.value = index
        }
    }

    fun submitQuizAnswer() {
        if (_selectedAnswerIndex.value != null && !_isAnswerSubmitted.value) {
            _isAnswerSubmitted.value = true
            val isCorrect = _selectedAnswerIndex.value == currentQuizQuestions[_activeQuizQuestionIndex.value].correctAnswerIndex
            if (isCorrect) {
                _quizCorrectAnswersCount.value += 1
            }
        }
    }

    fun nextQuizQuestion() {
        if (_isAnswerSubmitted.value) {
            _selectedAnswerIndex.value = null
            _isAnswerSubmitted.value = false
            if (_activeQuizQuestionIndex.value < currentQuizQuestions.lastIndex) {
                _activeQuizQuestionIndex.value += 1
            } else {
                _isQuizFinished.value = true
                // Persist score to DB
                viewModelScope.launch {
                    repository.addQuizResult(_quizCorrectAnswersCount.value, currentQuizQuestions.size)
                }
            }
        }
    }

    fun restartQuiz() {
        _activeQuizQuestionIndex.value = 0
        _selectedAnswerIndex.value = null
        _isAnswerSubmitted.value = false
        _quizCorrectAnswersCount.value = 0
        _isQuizFinished.value = false
    }

    fun clearQuizRecordLogs() {
        viewModelScope.launch {
            repository.clearQuizHistory()
        }
    }

    // --- AI Tutor Actions ---
    fun askAiTutor(prompt: String) {
        if (prompt.isBlank() || _isAiLoading.value) return

        val userLang = userProfile.value?.nativeLanguage ?: "English"

        viewModelScope.launch {
            _isAiLoading.value = true
            val updatedHistory = _aiTutorHistory.value + (prompt to true)
            _aiTutorHistory.value = updatedHistory

            val result = GeminiClient.askTutor(prompt, userLang)
            
            _aiTutorResponse.value = result
            _aiTutorHistory.value = updatedHistory + (result to false)
            _isAiLoading.value = false
        }
    }

    fun clearTutorChat() {
        _aiTutorHistory.value = emptyList()
        _aiTutorResponse.value = null
    }

    fun askAboutWord(vocab: Vocabulary) {
        val prompt = "Please explain the Japanese nursing term '${vocab.word}' (${vocab.pronunciation}), which means '${vocab.translation}' in English. Give me clinical context on when and how this word is used in a Japanese elderly care facility."
        askAiTutor(prompt)
    }

    fun askForMockQuiz() {
        val prompt = "Create a customized practice Specified Skilled Worker (SSW) nursing care multiple-choice question (with 4 options) in both Japanese and English. Then provide the correct answer with an analysis in simple terms."
        askAiTutor(prompt)
    }
}

class SSWViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SSWViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SSWViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
