package com.banimasum.manager.repositories

import com.banimasum.manager.database.ToolUsageDao
import com.banimasum.manager.models.ToolCondition
import com.banimasum.manager.models.ToolUsage
import com.banimasum.manager.models.ToolUsageStat
import com.banimasum.manager.models.ToolConditionStat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date

class ToolUsageRepository(private val toolUsageDao: ToolUsageDao) {
    
    val allToolUsage: Flow<List<ToolUsage>> = toolUsageDao.getAllToolUsage()

    suspend fun insertToolUsage(toolUsage: ToolUsage): Long {
        return toolUsageDao.insertToolUsage(toolUsage.copy(createdAt = Date(), updatedAt = Date()))
    }

    suspend fun updateToolUsage(toolUsage: ToolUsage) {
        toolUsageDao.updateToolUsage(toolUsage.copy(updatedAt = Date()))
    }

    suspend fun deleteToolUsage(toolUsage: ToolUsage) {
        toolUsageDao.deleteToolUsage(toolUsage)
    }

    fun getToolUsageBySession(sessionId: Long): Flow<List<ToolUsage>> {
        return toolUsageDao.getToolUsageBySession(sessionId)
    }

    fun getToolUsageByTool(toolId: Long): Flow<List<ToolUsage>> {
        return toolUsageDao.getToolUsageByTool(toolId)
    }

    suspend fun getLatestToolUsage(sessionId: Long, toolId: Long): ToolUsage? {
        return toolUsageDao.getLatestToolUsage(sessionId, toolId)
    }

    suspend fun getCurrentToolUsage(toolId: Long): ToolUsage? {
        return toolUsageDao.getCurrentToolUsage(toolId)
    }

    fun getToolUsageByDateRange(startDate: Date, endDate: Date): Flow<List<ToolUsage>> {
        return toolUsageDao.getToolUsageByDateRange(startDate, endDate)
    }

    suspend fun endToolUsage(id: Long, endTime: String, conditionAfter: ToolCondition) {
        toolUsageDao.endToolUsage(id, endTime, conditionAfter)
    }

    suspend fun getToolUsageCountBySession(sessionId: Long): Int {
        return toolUsageDao.getToolUsageCountBySession(sessionId)
    }

    suspend fun getToolUsageCountByTool(toolId: Long): Int {
        return toolUsageDao.getToolUsageCountByTool(toolId)
    }

    fun getToolUsageStatistics(): Flow<List<ToolUsageStat>> {
        return toolUsageDao.getToolUsageStatistics()
    }

    fun getToolUsageStatisticsByDateRange(startDate: Date, endDate: Date): Flow<List<ToolUsageStat>> {
        return toolUsageDao.getToolUsageStatisticsByDateRange(startDate, endDate)
    }

    fun getToolConditionStatistics(): Flow<List<ToolConditionStat>> {
        return toolUsageDao.getToolConditionStatistics()
    }

    suspend fun startToolUsage(sessionId: Long, toolId: Long, quantity: Int, conditionBefore: ToolCondition, startTime: String): Long {
        val toolUsage = ToolUsage(
            sessionId = sessionId,
            toolId = toolId,
            quantityUsed = quantity,
            conditionBefore = conditionBefore,
            conditionAfter = null,
            usageStartTime = startTime,
            usageEndTime = null,
            notes = null
        )
        return insertToolUsage(toolUsage)
    }

    suspend fun endToolUsageForSession(sessionId: Long, endTime: String): Boolean {
        return try {
            val toolUsages = toolUsageDao.getToolUsageBySession(sessionId).first()
            var success = true
            
            for (toolUsage in toolUsages) {
                if (toolUsage.usageEndTime == null) {
                    // Assume condition remains the same if not specified
                    endToolUsage(toolUsage.id, endTime, toolUsage.conditionBefore)
                }
            }
            success
        } catch (e: Exception) {
            false
        }
    }

    suspend fun validateToolUsage(toolUsage: ToolUsage): ValidationResult {
        val errors = mutableListOf<String>()

        if (toolUsage.sessionId == 0L) {
            errors.add("ID sesi tidak valid")
        }

        if (toolUsage.toolId == 0L) {
            errors.add("ID alat tidak valid")
        }

        if (toolUsage.quantityUsed < 1) {
            errors.add("Jumlah alat yang digunakan minimal 1")
        }

        if (toolUsage.usageStartTime.isBlank()) {
            errors.add("Waktu mulai penggunaan tidak boleh kosong")
        }

        if (!isValidTimeFormat(toolUsage.usageStartTime)) {
            errors.add("Format waktu mulai tidak valid (HH:mm)")
        }

        if (toolUsage.usageEndTime != null && toolUsage.usageEndTime.isNotBlank()) {
            if (!isValidTimeFormat(toolUsage.usageEndTime)) {
                errors.add("Format waktu selesai tidak valid (HH:mm)")
            }
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return time.matches(Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$"))
    }

    suspend fun isToolCurrentlyInUse(toolId: Long): Boolean {
        return getCurrentToolUsage(toolId) != null
    }

    suspend fun getToolUsageSummary(toolId: Long, startDate: Date, endDate: Date): ToolUsageSummary {
        val toolUsages = toolUsageDao.getToolUsageByTool(toolId).first()
        val filteredUsages = toolUsages.filter { usage ->
            // Filter by date range if needed
            true // Simplified for now
        }
        
        val totalUsage = filteredUsages.size
        val totalQuantity = filteredUsages.sumOf { usage -> usage.quantityUsed }
        val averageCondition = if (filteredUsages.isNotEmpty()) {
            filteredUsages.mapNotNull { usage -> usage.conditionAfter }.map { condition -> conditionToNumber(condition) }.average()
        } else {
            0.0
        }

        return ToolUsageSummary(
            toolId = toolId,
            totalUsage = totalUsage,
            totalQuantity = totalQuantity,
            averageCondition = averageCondition
        )
    }

    private fun conditionToNumber(condition: ToolCondition): Int {
        return when (condition) {
            ToolCondition.EXCELLENT -> 5
            ToolCondition.GOOD -> 4
            ToolCondition.FAIR -> 3
            ToolCondition.POOR -> 2
            ToolCondition.BROKEN -> 1
        }
    }
}

data class ToolUsageSummary(
    val toolId: Long,
    val totalUsage: Int,
    val totalQuantity: Int,
    val averageCondition: Double
)