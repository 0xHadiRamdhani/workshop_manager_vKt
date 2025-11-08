package com.banimasum.manager.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "tool_usage",
    foreignKeys = [
        ForeignKey(
            entity = WorkshopSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tool::class,
            parentColumns = ["id"],
            childColumns = ["toolId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ToolUsage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val toolId: Long,
    val quantityUsed: Int = 1,
    val conditionBefore: ToolCondition,
    val conditionAfter: ToolCondition?,
    val usageStartTime: String,
    val usageEndTime: String?,
    val notes: String?,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)