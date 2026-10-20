package com.example.tutor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for TutorProfiles to store expertise, hourly rates, and bios
 * so users can browse available tutors.
 */
@Entity(tableName = "tutor_profiles")
data class TutorProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    val phone: String,

    val expertise: String, // Subjects and skills, e.g. "Mathematics, Physics, Calculus"

    @ColumnInfo(name = "hourly_rate")
    val hourlyRate: Double, // Hourly rate in ETB, e.g. 350.0

    val bio: String, // Detailed tutor biography and methodology

    val location: String = "Addis Ababa",

    val education: String = "Bachelor's Degree",

    @ColumnInfo(name = "experience_years")
    val experienceYears: Int = 3,

    val rating: Float = 4.9f,

    @ColumnInfo(name = "reviews_count")
    val reviewsCount: Int = 12,

    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean = true,

    @ColumnInfo(name = "teaching_mode")
    val teachingMode: String = "Home & Online",

    @ColumnInfo(name = "telegram_username")
    val telegramUsername: String = ""
) {
    val formattedRate: String
        get() = "${hourlyRate.toInt()} ETB/hr"
}
