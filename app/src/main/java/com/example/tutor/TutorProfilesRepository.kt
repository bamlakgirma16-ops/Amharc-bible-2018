package com.example.tutor

import kotlinx.coroutines.flow.Flow

class TutorProfilesRepository(private val dao: TutorProfileDao) {

    val allTutors: Flow<List<TutorProfile>> = dao.getAllTutorProfiles()

    val availableTutors: Flow<List<TutorProfile>> = dao.getAvailableTutors()

    fun searchTutors(query: String): Flow<List<TutorProfile>> = dao.searchTutors(query)

    fun getTutorsByExpertise(expertise: String): Flow<List<TutorProfile>> =
        dao.getTutorsByExpertise(expertise)

    fun getTutorsByMaxRate(maxRate: Double): Flow<List<TutorProfile>> =
        dao.getTutorsByMaxRate(maxRate)

    suspend fun getTutorById(id: Long): TutorProfile? = dao.getTutorById(id)

    suspend fun insertTutor(tutor: TutorProfile): Long = dao.insertTutor(tutor)

    suspend fun insertTutors(tutors: List<TutorProfile>) = dao.insertTutors(tutors)

    suspend fun updateTutor(tutor: TutorProfile) = dao.updateTutor(tutor)

    suspend fun deleteTutor(tutor: TutorProfile) = dao.deleteTutor(tutor)

    suspend fun deleteTutorById(id: Long) = dao.deleteTutorById(id)

    suspend fun toggleAvailability(tutor: TutorProfile) {
        val updated = tutor.copy(isAvailable = !tutor.isAvailable)
        dao.updateTutor(updated)
    }

    suspend fun getTutorCount(): Int = dao.getTutorCount()
}
