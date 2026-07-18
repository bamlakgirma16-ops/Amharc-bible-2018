package com.example.bible

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BibleDao {
    @Query("SELECT * FROM bible_verses WHERE bookName = :bookName AND chapterNumber = :chapter ORDER BY verseNumber ASC")
    fun getVerses(bookName: String, chapter: Int): Flow<List<BibleVerse>>

    @Query("SELECT * FROM bible_verses WHERE isBookmarked = 1")
    fun getBookmarkedVerses(): Flow<List<BibleVerse>>

    @Query("SELECT * FROM bible_verses WHERE amharicText LIKE :query OR englishText LIKE :query OR bookName LIKE :query OR bookNameEn LIKE :query")
    fun searchVerses(query: String): Flow<List<BibleVerse>>

    @Query("UPDATE bible_verses SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Int, isBookmarked: Boolean)

    @Query("UPDATE bible_verses SET highlightColor = :color WHERE id = :id")
    suspend fun updateHighlight(id: Int, color: String?)

    @Query("SELECT COUNT(*) FROM bible_verses")
    suspend fun getVerseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<BibleVerse>)

    // Study Notes operations
    @Query("SELECT * FROM study_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<StudyNote>>

    @Query("SELECT * FROM study_notes WHERE bookName = :bookName AND chapterNumber = :chapter AND verseNumber = :verse")
    fun getNotesForVerse(bookName: String, chapter: Int, verse: Int): Flow<List<StudyNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNote)

    @Query("DELETE FROM study_notes WHERE id = :id")
    suspend fun deleteNote(id: Int)
}
