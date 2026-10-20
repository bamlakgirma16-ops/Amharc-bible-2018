package com.example.tutor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Matches the SQLite table 'users' created by the AnnA Tutor Telegram Bot:
 * CREATE TABLE IF NOT EXISTS users (
 *     id INTEGER PRIMARY KEY AUTOINCREMENT,
 *     telegram_id INTEGER,
 *     full_name TEXT,
 *     phone TEXT,
 *     role TEXT
 * )
 */
@Entity(tableName = "users")
data class TutorUser(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "telegram_id")
    val telegramId: Long = 0,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    val phone: String,

    val role: String, // "አስጠኚ (Tutor)" or "ተማሪ/ወላጅ (Student/Parent)"

    val subjects: String = "",

    @ColumnInfo(name = "grade_level")
    val gradeLevel: String = "",

    val location: String = "Addis Ababa",

    val bio: String = "",

    val rate: String = "",

    val mode: String = "Home & Online",

    val rating: Float = 4.8f,

    @ColumnInfo(name = "reviews_count")
    val reviewsCount: Int = 8,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    val isTutor: Boolean
        get() = role.contains("አስጠኚ") || role.contains("Tutor", ignoreCase = true)
}

enum class AppLanguage(val label: String, val code: String) {
    AMHARIC("አማርኛ", "am"),
    ENGLISH("English", "en")
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val quickReplies: List<String> = emptyList(),
    val isSystemStatus: Boolean = false
)

enum class MessageSender {
    BOT,
    USER
}

enum class BotStep {
    START,
    ROLE_SELECTION,
    NAME_INPUT,
    PHONE_INPUT,
    SUBJECT_INPUT,
    COMPLETED
}
