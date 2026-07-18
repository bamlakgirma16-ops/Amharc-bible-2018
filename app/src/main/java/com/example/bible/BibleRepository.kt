package com.example.bible

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BibleRepository(private val bibleDao: BibleDao) {

    // Retrieve flowing verses for active book/chapter
    fun getVersesForChapter(bookName: String, chapter: Int): Flow<List<BibleVerse>> {
        return bibleDao.getVerses(bookName, chapter)
    }

    // Retrieve all bookmarks
    val bookmarkedVerses: Flow<List<BibleVerse>> = bibleDao.getBookmarkedVerses()

    // Retrieve all notes
    val allNotes: Flow<List<StudyNote>> = bibleDao.getAllNotes()

    // Retrieve notes specific to a single verse
    fun getNotesForVerse(bookName: String, chapter: Int, verseNumber: Int): Flow<List<StudyNote>> {
        return bibleDao.getNotesForVerse(bookName, chapter, verseNumber)
    }

    // Search scriptures in real-time
    fun search(query: String): Flow<List<BibleVerse>> {
        val wrappedQuery = "%$query%"
        return bibleDao.searchVerses(wrappedQuery)
    }

    // Toggle bookmark status
    suspend fun toggleBookmark(id: Int, isBookmarked: Boolean) = withContext(Dispatchers.IO) {
        bibleDao.updateBookmark(id, isBookmarked)
    }

    // Set highlight color
    suspend fun updateHighlight(id: Int, color: String?) = withContext(Dispatchers.IO) {
        bibleDao.updateHighlight(id, color)
    }

    // Add study note
    suspend fun addStudyNote(bookName: String, chapter: Int, verse: Int, noteText: String) = withContext(Dispatchers.IO) {
        val newNote = StudyNote(
            bookName = bookName,
            chapterNumber = chapter,
            verseNumber = verse,
            noteText = noteText
        )
        bibleDao.insertNote(newNote)
    }

    // Delete study note
    suspend fun deleteStudyNote(id: Int) = withContext(Dispatchers.IO) {
        bibleDao.deleteNote(id)
    }

    // Bulletproof pre-population check
    suspend fun ensurePrepopulated() = withContext(Dispatchers.IO) {
        if (bibleDao.getVerseCount() == 0) {
            bibleDao.insertVerses(BiblePreloadData.preloadedVerses)
        }
    }
}
