package com.example.tutor

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TutorProfileDao {
    @Query("SELECT * FROM tutor_profiles ORDER BY rating DESC, hourly_rate ASC")
    fun getAllTutorProfiles(): Flow<List<TutorProfile>>

    @Query("SELECT * FROM tutor_profiles WHERE is_available = 1 ORDER BY rating DESC")
    fun getAvailableTutors(): Flow<List<TutorProfile>>

    @Query("""
        SELECT * FROM tutor_profiles 
        WHERE name LIKE '%' || :query || '%' 
           OR expertise LIKE '%' || :query || '%' 
           OR location LIKE '%' || :query || '%' 
           OR bio LIKE '%' || :query || '%'
        ORDER BY rating DESC
    """)
    fun searchTutors(query: String): Flow<List<TutorProfile>>

    @Query("SELECT * FROM tutor_profiles WHERE expertise LIKE '%' || :expertise || '%' ORDER BY rating DESC")
    fun getTutorsByExpertise(expertise: String): Flow<List<TutorProfile>>

    @Query("SELECT * FROM tutor_profiles WHERE hourly_rate <= :maxRate ORDER BY hourly_rate ASC")
    fun getTutorsByMaxRate(maxRate: Double): Flow<List<TutorProfile>>

    @Query("SELECT * FROM tutor_profiles WHERE id = :id LIMIT 1")
    suspend fun getTutorById(id: Long): TutorProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTutor(tutor: TutorProfile): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTutors(tutors: List<TutorProfile>)

    @Update
    suspend fun updateTutor(tutor: TutorProfile)

    @Delete
    suspend fun deleteTutor(tutor: TutorProfile)

    @Query("DELETE FROM tutor_profiles WHERE id = :id")
    suspend fun deleteTutorById(id: Long)

    @Query("SELECT COUNT(*) FROM tutor_profiles")
    suspend fun getTutorCount(): Int
}
