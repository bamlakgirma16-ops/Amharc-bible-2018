package com.example.bible

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bible_verses")
data class BibleVerse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookName: String, // e.g. "ኦሪት ዘፍጥረት" (Genesis)
    val bookNameEn: String, // e.g. "Genesis"
    val chapterNumber: Int,
    val verseNumber: Int,
    val amharicText: String,
    val englishText: String,
    val isBookmarked: Boolean = false,
    val highlightColor: String? = null // e.g. "yellow", "green", "pink" or null
)

@Entity(tableName = "study_notes")
data class StudyNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookName: String,
    val chapterNumber: Int,
    val verseNumber: Int,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)
