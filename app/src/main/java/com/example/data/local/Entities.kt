package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_solutions")
data class SavedSolution(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val rawSolution: String,
    val topic: String,
    val timestamp: Long = System.currentTimeMillis(),
    val gradeLevel: String = "High school"
)

@Entity(tableName = "quiz_records")
data class QuizRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val topic: String,
    val difficulty: String,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val name: String,
    val grade: String = "High school",
    val memberSince: Long = System.currentTimeMillis(),
    val solvedCount: Int = 0,
    val language: String = ""
)
