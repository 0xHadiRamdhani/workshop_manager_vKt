package com.banimasum.manager.models

data class ToolUsageStat(
    val toolId: Long,
    val usageCount: Int,
    val totalQuantity: Int
)

data class ToolConditionStat(
    val toolId: Long,
    val avgCondition: Double
)

data class MonthlySessionStat(
    val month: String,
    val count: Int
)

data class StudentSessionStat(
    val studentId: Long,
    val sessionCount: Int
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)

data class SessionStatistics(
    val totalSessions: Int,
    val activeSessions: Int,
    val completedSessions: Int,
    val cancelledSessions: Int,
    val upcomingSessions: Int
)