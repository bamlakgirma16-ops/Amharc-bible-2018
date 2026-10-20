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

enum class TutorSortOption(val labelAmharic: String, val labelEnglish: String) {
    RATING("በከፍተኛ ደረጃ (Top Rated)", "Top Rated"),
    PRICE_LOW_TO_HIGH("በዝቅተኛ ዋጋ (Price: Low to High)", "Price: Low to High"),
    PRICE_HIGH_TO_LOW("በከፍተኛ ዋጋ (Price: High to Low)", "Price: High to Low"),
    EXPERIENCE("በልምድ (Most Experienced)", "Most Experienced")
}

data class TutorFilterCriteria(
    val query: String = "",
    val expertise: String = "All",
    val onlyAvailable: Boolean = false,
    val maxHourlyRate: Double = 1000.0,
    val sortOption: TutorSortOption = TutorSortOption.RATING
)

class TutorProfilesViewModel(
    private val repository: TutorProfilesRepository
) : ViewModel() {

    // Filter and search criteria
    val searchQuery = MutableStateFlow("")
    val selectedExpertise = MutableStateFlow("All")
    val onlyAvailable = MutableStateFlow(false)
    val maxHourlyRate = MutableStateFlow(1000.0) // Max rate filter
    val sortOption = MutableStateFlow(TutorSortOption.RATING)

    val allTutorProfiles: StateFlow<List<TutorProfile>> = repository.allTutors
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected tutor for detail dialog
    private val _selectedTutor = MutableStateFlow<TutorProfile?>(null)
    val selectedTutor: StateFlow<TutorProfile?> = _selectedTutor.asStateFlow()

    fun selectTutor(tutor: TutorProfile?) {
        _selectedTutor.value = tutor
    }

    // Combine 5 filter flows into a single criteria flow
    private val filterCriteria = combine(
        searchQuery,
        selectedExpertise,
        onlyAvailable,
        maxHourlyRate,
        sortOption
    ) { query, expertise, available, maxRate, sort ->
        TutorFilterCriteria(query, expertise, available, maxRate, sort)
    }

    // Filtered and sorted tutor profiles
    val filteredTutors: StateFlow<List<TutorProfile>> = combine(
        allTutorProfiles,
        filterCriteria
    ) { tutors, criteria ->
        tutors.filter { tutor ->
            // Availability filter
            val matchesAvailability = !criteria.onlyAvailable || tutor.isAvailable

            // Max rate filter
            val matchesRate = tutor.hourlyRate <= criteria.maxHourlyRate

            // Expertise filter
            val matchesExpertise = if (criteria.expertise == "All" || criteria.expertise == "ሁሉም") {
                true
            } else {
                tutor.expertise.contains(criteria.expertise, ignoreCase = true)
            }

            // Query filter (name, expertise, location, bio)
            val matchesQuery = if (criteria.query.isBlank()) {
                true
            } else {
                tutor.name.contains(criteria.query, ignoreCase = true) ||
                        tutor.expertise.contains(criteria.query, ignoreCase = true) ||
                        tutor.location.contains(criteria.query, ignoreCase = true) ||
                        tutor.bio.contains(criteria.query, ignoreCase = true) ||
                        tutor.education.contains(criteria.query, ignoreCase = true)
            }

            matchesAvailability && matchesRate && matchesExpertise && matchesQuery
        }.let { list ->
            // Apply sorting
            when (criteria.sortOption) {
                TutorSortOption.RATING -> list.sortedByDescending { it.rating }
                TutorSortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.hourlyRate }
                TutorSortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.hourlyRate }
                TutorSortOption.EXPERIENCE -> list.sortedByDescending { it.experienceYears }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Add new tutor profile
    fun addTutorProfile(
        name: String,
        phone: String,
        expertise: String,
        hourlyRate: Double,
        bio: String,
        location: String,
        education: String,
        experienceYears: Int,
        isAvailable: Boolean = true,
        teachingMode: String = "Home & Online",
        telegramUsername: String = "",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val newProfile = TutorProfile(
                name = name,
                phone = phone,
                expertise = expertise,
                hourlyRate = hourlyRate,
                bio = bio,
                location = location,
                education = education,
                experienceYears = experienceYears,
                isAvailable = isAvailable,
                teachingMode = teachingMode,
                telegramUsername = telegramUsername
            )
            repository.insertTutor(newProfile)
            onSuccess()
        }
    }

    // Toggle tutor availability
    fun toggleTutorAvailability(tutor: TutorProfile) {
        viewModelScope.launch {
            repository.toggleAvailability(tutor)
        }
    }

    // Delete tutor profile
    fun deleteTutorProfile(tutor: TutorProfile) {
        viewModelScope.launch {
            repository.deleteTutor(tutor)
        }
    }
}

class TutorProfilesViewModelFactory(
    private val repository: TutorProfilesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TutorProfilesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TutorProfilesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
