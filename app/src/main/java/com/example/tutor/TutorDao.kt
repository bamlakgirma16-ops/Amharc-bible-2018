package com.example.tutor

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TutorDao {
    @Query("SELECT * FROM users ORDER BY id DESC")
    fun getAllUsers(): Flow<List<TutorUser>>

    @Query("SELECT * FROM users WHERE role LIKE '%' || :rolePattern || '%' ORDER BY id DESC")
    fun getUsersByRole(rolePattern: String): Flow<List<TutorUser>>

    @Query("""
        SELECT * FROM users 
        WHERE full_name LIKE '%' || :query || '%' 
           OR phone LIKE '%' || :query || '%' 
           OR subjects LIKE '%' || :query || '%' 
           OR location LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    fun searchUsers(query: String): Flow<List<TutorUser>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): TutorUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: TutorUser): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<TutorUser>)

    @Update
    suspend fun updateUser(user: TutorUser)

    @Delete
    suspend fun deleteUser(user: TutorUser)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Long)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("DELETE FROM users")
    suspend fun clearAll()
}
