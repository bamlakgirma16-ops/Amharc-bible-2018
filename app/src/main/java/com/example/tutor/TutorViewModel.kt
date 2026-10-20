package com.example.tutor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TutorViewModel(private val repository: TutorRepository) : ViewModel() {

    // Language setting
    private val _language = MutableStateFlow(AppLanguage.AMHARIC)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.AMHARIC) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.AMHARIC
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    // Search and filter state
    val searchQuery = MutableStateFlow("")
    val selectedSubject = MutableStateFlow("All")
    val selectedTabFilter = MutableStateFlow("Tutors") // "Tutors", "Students", "All"

    val allUsers: StateFlow<List<TutorUser>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tutors: StateFlow<List<TutorUser>> = repository.tutors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentRequests: StateFlow<List<TutorUser>> = repository.studentRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered users for the Explore screen
    val filteredUsers: StateFlow<List<TutorUser>> = combine(
        allUsers,
        searchQuery,
        selectedSubject,
        selectedTabFilter
    ) { users, query, subject, tab ->
        users.filter { user ->
            // Tab filter
            val matchesTab = when (tab) {
                "Tutors" -> user.isTutor
                "Students" -> !user.isTutor
                else -> true
            }

            // Subject filter
            val matchesSubject = if (subject == "All" || subject == "ሁሉም") {
                true
            } else {
                user.subjects.contains(subject, ignoreCase = true)
            }

            // Query filter
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                user.fullName.contains(query, ignoreCase = true) ||
                        user.phone.contains(query) ||
                        user.subjects.contains(query, ignoreCase = true) ||
                        user.location.contains(query, ignoreCase = true) ||
                        user.bio.contains(query, ignoreCase = true)
            }

            matchesTab && matchesSubject && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Interactive Bot Registration state
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _botStep = MutableStateFlow(BotStep.START)
    val botStep: StateFlow<BotStep> = _botStep.asStateFlow()

    // Temporary registration data collected by the bot
    private var pendingRole: String = ""
    private var pendingName: String = ""
    private var pendingPhone: String = ""

    init {
        startBotConversation()
    }

    fun startBotConversation() {
        pendingRole = ""
        pendingName = ""
        pendingPhone = ""
        _botStep.value = BotStep.ROLE_SELECTION
        _chatMessages.value = listOf(
            ChatMessage(
                sender = MessageSender.BOT,
                text = "እንኳን ወደ AnnA Tutor በደህና መጡ! 🎓\n\nእባክዎን አገልግሎቱን በምን መልክ እንደሚፈልጉ ከታች ካሉት አማራጮች ይምረጡ:",
                quickReplies = listOf("አስጠኚ (Tutor)", "ተማሪ/ወላጅ (Student/Parent)")
            )
        )
    }

    fun handleUserChatInput(input: String) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return

        // Add user message
        val current = _chatMessages.value.toMutableList()
        current.add(ChatMessage(sender = MessageSender.USER, text = trimmed))
        _chatMessages.value = current

        // Process state transition matching the Telegram Python bot workflow
        when (_botStep.value) {
            BotStep.ROLE_SELECTION -> {
                if (trimmed.contains("አስጠኚ") || trimmed.contains("Tutor")) {
                    pendingRole = "አስጠኚ (Tutor)"
                } else if (trimmed.contains("ተማሪ") || trimmed.contains("ወላጅ") || trimmed.contains("Student") || trimmed.contains("Parent")) {
                    pendingRole = "ተማሪ/ወላጅ (Student/Parent)"
                } else {
                    pendingRole = trimmed
                }

                _botStep.value = BotStep.NAME_INPUT
                addBotReply(
                    "መረጡት: *$pendingRole*.\n\nእባክዎን ሙሉ ስምዎን (First & Last Name) ይጻፉልን:",
                    emptyList()
                )
            }

            BotStep.NAME_INPUT -> {
                pendingName = trimmed
                _botStep.value = BotStep.PHONE_INPUT
                addBotReply(
                    "አመሰግናለን $pendingName! አሁን ደግሞ ማግኘት የሚችሉበትን ስልክ ቁጥርዎን (ለምሳሌ 09...) ይጻፉልን:",
                    listOf("0911000000", "0922000000")
                )
            }

            BotStep.PHONE_INPUT -> {
                pendingPhone = trimmed
                _botStep.value = BotStep.COMPLETED

                // Save directly to Room SQLite database (matching SQLite insert in Python bot)
                viewModelScope.launch {
                    val defaultSubjects = if (pendingRole.contains("አስጠኚ")) "General Academic Subjects" else "Academic Assistance"
                    val newUser = TutorUser(
                        telegramId = (1000000L..9999999L).random(),
                        fullName = pendingName,
                        phone = pendingPhone,
                        role = pendingRole,
                        subjects = defaultSubjects,
                        location = "Addis Ababa",
                        bio = "Registered via AnnA Tutor Telegram Bot"
                    )
                    repository.insertUser(newUser)
                }

                addBotReply(
                    "✅ *ምዝገባዎ በተሳካ ሁኔታ ተጠናቋል!*\n\n" +
                            "የ'አና ቱተር' (AnnA Tutor) ቡድን መረጃዎን መርምሮ በአጭር ጊዜ ውስጥ በስልክ ቁጥርዎ ($pendingPhone) ያነጋግርዎታል። እናመሰግናለን! 🙏",
                    listOf("🔄 አዲስ ምዝገባ ጀምር (New)", "📋 የአድሚን ዝርዝር እይ (Admin)")
                )
            }

            BotStep.COMPLETED -> {
                if (trimmed.contains("አዲስ") || trimmed.contains("New") || trimmed.contains("/start")) {
                    startBotConversation()
                } else {
                    addBotReply(
                        "ምዝገባዎ ቀድሞውኑ ተጠናቋል። አዲስ ለመጀመር ከታች ያለውን ይጫኑ ወይም ተጨማሪ መረጃ ይጠይቁ።",
                        listOf("🔄 አዲስ ምዝገባ ጀምር (Restart)")
                    )
                }
            }

            else -> {
                startBotConversation()
            }
        }
    }

    private fun addBotReply(text: String, quickReplies: List<String>) {
        val current = _chatMessages.value.toMutableList()
        current.add(
            ChatMessage(
                sender = MessageSender.BOT,
                text = text,
                quickReplies = quickReplies
            )
        )
        _chatMessages.value = current
    }

    // Direct Form Registration
    fun registerUserFromForm(
        fullName: String,
        phone: String,
        role: String,
        subjects: String,
        gradeLevel: String,
        location: String,
        bio: String,
        rate: String,
        mode: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val user = TutorUser(
                telegramId = (1000000L..9999999L).random(),
                fullName = fullName,
                phone = phone,
                role = role,
                subjects = subjects,
                gradeLevel = gradeLevel,
                location = location,
                bio = bio,
                rate = rate,
                mode = mode
            )
            repository.insertUser(user)
            onSuccess()
        }
    }

    // Delete user (Admin capability)
    fun deleteUser(user: TutorUser) {
        viewModelScope.launch {
            repository.deleteUser(user)
        }
    }

    // Format admin markdown for export
    fun getAdminMarkdown(): String {
        return repository.formatAdminMarkdown(allUsers.value)
    }
}

class TutorViewModelFactory(private val repository: TutorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TutorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TutorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
