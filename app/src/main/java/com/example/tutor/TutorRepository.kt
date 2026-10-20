package com.example.tutor

import kotlinx.coroutines.flow.Flow

class TutorRepository(private val dao: TutorDao) {

    val allUsers: Flow<List<TutorUser>> = dao.getAllUsers()

    val tutors: Flow<List<TutorUser>> = dao.getUsersByRole("አስጠኚ")

    val studentRequests: Flow<List<TutorUser>> = dao.getUsersByRole("ተማሪ")

    fun searchUsers(query: String): Flow<List<TutorUser>> = dao.searchUsers(query)

    suspend fun insertUser(user: TutorUser): Long {
        return dao.insertUser(user)
    }

    suspend fun deleteUser(user: TutorUser) {
        dao.deleteUser(user)
    }

    suspend fun deleteUserById(id: Long) {
        dao.deleteUserById(id)
    }

    suspend fun getUserCount(): Int {
        return dao.getUserCount()
    }

    fun formatAdminMarkdown(users: List<TutorUser>): String {
        if (users.isEmpty()) {
            return "እስካሁን የተመዘገበ ተጠቃሚ የለም።"
        }
        val sb = StringBuilder("📋 *የተመዘገቡ ተጠቃሚዎች ዝርዝር (Admin Panel):*\n\n")
        users.forEachIndexed { index, user ->
            sb.append("${index + 1}. *ስም:* ${user.fullName}\n   *ስልክ:* ${user.phone}\n   *ሚና:* ${user.role}\n\n")
        }
        return sb.toString().trimEnd()
    }
}
