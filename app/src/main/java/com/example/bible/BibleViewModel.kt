package com.example.bible

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TranslationMode {
    AMHARIC_ONLY,
    ENGLISH_ONLY,
    BILINGUAL
}

class BibleViewModel(private val repository: BibleRepository) : ViewModel() {

    // Active reading selections
    private val _selectedBook = MutableStateFlow("ኦሪት ዘፍጥረት")
    val selectedBook = _selectedBook.asStateFlow()

    private val _selectedChapter = MutableStateFlow(1)
    val selectedChapter = _selectedChapter.asStateFlow()

    // Active translation mode: Bilingual side-by-side is a key unique feature
    private val _translationMode = MutableStateFlow(TranslationMode.BILINGUAL)
    val translationMode = _translationMode.asStateFlow()

    // Real-time keyword search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    init {
        // Guarantee scripture pre-population on first load
        viewModelScope.launch {
            repository.ensurePrepopulated()
        }
    }

    // List of books we have preloaded scriptures for
    val availableBooks = listOf(
        Pair("ኦሪት ዘፍጥረት", "Genesis"),
        Pair("መዝሙረ ዳዊት", "Psalms"),
        Pair("የዮሐንስ ወንጌል", "John"),
        Pair("የማቴዎስ ወንጌል", "Matthew")
    )

    // Reactively load verses whenever book or chapter changes
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeVerses: StateFlow<List<BibleVerse>> = kotlinx.coroutines.flow.combine(
        _selectedBook,
        _selectedChapter
    ) { book, chapter ->
        Pair(book, chapter)
    }.flatMapLatest { (book, chapter) ->
        repository.getVersesForChapter(book, chapter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Reactively load bookmarks
    val bookmarkedVerses: StateFlow<List<BibleVerse>> = repository.bookmarkedVerses
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Reactively load all notes
    val allNotes: StateFlow<List<StudyNote>> = repository.allNotes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Reactively drive search results
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<BibleVerse>> = _searchQuery
        .flatMapLatest { query ->
            if (query.trim().isEmpty()) {
                MutableStateFlow(emptyList())
            } else {
                repository.search(query)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Actions
    fun selectBook(bookName: String) {
        _selectedBook.value = bookName
        // Reset chapter when switching books
        _selectedChapter.value = if (bookName == "የዮሐንስ ወንጌል") {
            // We have John 1 and John 3 preloaded
            1
        } else {
            1
        }
    }

    fun selectChapter(chapter: Int) {
        _selectedChapter.value = chapter
    }

    fun setTranslationMode(mode: TranslationMode) {
        _translationMode.value = mode
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleBookmark(verse: BibleVerse) {
        viewModelScope.launch {
            repository.toggleBookmark(verse.id, !verse.isBookmarked)
        }
    }

    fun setHighlight(verse: BibleVerse, color: String?) {
        viewModelScope.launch {
            repository.updateHighlight(verse.id, color)
        }
    }

    fun addNote(verse: BibleVerse, noteText: String) {
        if (noteText.trim().isEmpty()) return
        viewModelScope.launch {
            repository.addStudyNote(
                bookName = verse.bookName,
                chapter = verse.chapterNumber,
                verse = verse.verseNumber,
                noteText = noteText
            )
        }
    }

    fun deleteNote(noteId: Int) {
        viewModelScope.launch {
            repository.deleteStudyNote(noteId)
        }
    }
}

// Simple Factory provider for our ViewModel
class BibleViewModelFactory(private val repository: BibleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BibleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BibleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
