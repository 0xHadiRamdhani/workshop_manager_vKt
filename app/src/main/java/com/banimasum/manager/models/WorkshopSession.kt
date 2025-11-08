package com.banimasum.manager.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "workshop_sessions",
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkshopSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val sessionDate: Date,
    val startTime: String,
    val endTime: String?,
    val projectName: String,
    val projectDescription: String?,
    val toolsUsed: String, // JSON array of tool IDs
    val instructorName: String,
    val safetyBriefingCompleted: Boolean = true,
    val sessionStatus: SessionStatus = SessionStatus.SCHEDULED,
    val notes: String?,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    override fun toString(): String = "$projectName - $sessionDate"
}

enum class SessionStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_SHOW
}