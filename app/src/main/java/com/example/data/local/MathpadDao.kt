package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MathpadDao {
    // Solutions
    @Query("SELECT * FROM saved_solutions ORDER BY timestamp DESC")
    fun getAllSolutions(): Flow<List<SavedSolution>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolution(solution: SavedSolution): Long

    @Query("DELETE FROM saved_solutions WHERE id = :id")
    suspend fun deleteSolutionById(id: Long)

    @Query("DELETE FROM saved_solutions")
    suspend fun clearAllSolutions()

    // Quizzes
    @Query("SELECT * FROM quiz_records ORDER BY timestamp DESC")
    fun getAllQuizRecords(): Flow<List<QuizRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizRecord(record: QuizRecord): Long

    @Query("DELETE FROM quiz_records")
    suspend fun clearAllQuizRecords()

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET solvedCount = solvedCount + 1 WHERE id = 1")
    suspend fun incrementSolvedCount()

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()
}
