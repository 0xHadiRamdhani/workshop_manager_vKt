package com.banimasum.manager.database

import androidx.room.*
import com.banimasum.manager.models.ToolUsage
import com.banimasum.manager.models.ToolUsageStat
import com.banimasum.manager.models.ToolConditionStat
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolUsageDao {
    @Query("SELECT * FROM tool_usage ORDER BY createdAt DESC")
    fun getAllToolUsage(): Flow<List<ToolUsage>>

    @Query("SELECT * FROM tool_usage WHERE sessionId = :sessionId ORDER BY createdAt DESC")
    fun getToolUsageBySession(sessionId: Long): Flow<List<ToolUsage>>

    @Query("SELECT * FROM tool_usage WHERE toolId = :toolId ORDER BY createdAt DESC")
    fun getToolUsageByTool(toolId: Long): Flow<List<ToolUsage>>

    @Query("""
        SELECT * FROM tool_usage 
        WHERE sessionId = :sessionId AND toolId = :toolId 
        ORDER BY createdAt DESC 
        LIMIT 1
    """)
    suspend fun getLatestToolUsage(sessionId: Long, toolId: Long): ToolUsage?

    @Query("""
        SELECT * FROM tool_usage 
        WHERE toolId = :toolId 
        AND usageEndTime IS NULL 
        ORDER BY createdAt DESC 
        LIMIT 1
    """)
    suspend fun getCurrentToolUsage(toolId: Long): ToolUsage?

    @Query("""
        SELECT * FROM tool_usage 
        WHERE sessionId IN (
            SELECT id FROM workshop_sessions 
            WHERE sessionDate >= :startDate AND sessionDate <= :endDate
        )
        ORDER BY createdAt DESC
    """)
    fun getToolUsageByDateRange(startDate: java.util.Date, endDate: java.util.Date): Flow<List<ToolUsage>>

    @Insert
    suspend fun insertToolUsage(toolUsage: ToolUsage): Long

    @Update
    suspend fun updateToolUsage(toolUsage: ToolUsage)

    @Delete
    suspend fun deleteToolUsage(toolUsage: ToolUsage)

    @Query("UPDATE tool_usage SET usageEndTime = :endTime, conditionAfter = :conditionAfter WHERE id = :id")
    suspend fun endToolUsage(id: Long, endTime: String, conditionAfter: com.banimasum.manager.models.ToolCondition)

    @Query("SELECT COUNT(*) FROM tool_usage WHERE sessionId = :sessionId")
    suspend fun getToolUsageCountBySession(sessionId: Long): Int

    @Query("SELECT COUNT(*) FROM tool_usage WHERE toolId = :toolId")
    suspend fun getToolUsageCountByTool(toolId: Long): Int

    @Query("""
        SELECT toolId, COUNT(*) as usageCount, SUM(quantityUsed) as totalQuantity
        FROM tool_usage 
        GROUP BY toolId 
        ORDER BY usageCount DESC
    """)
    fun getToolUsageStatistics(): Flow<List<ToolUsageStat>>

    @Query("""
        SELECT toolId, COUNT(*) as usageCount 
        FROM tool_usage 
        WHERE sessionId IN (
            SELECT id FROM workshop_sessions 
            WHERE sessionDate >= :startDate AND sessionDate <= :endDate
        )
        GROUP BY toolId 
        ORDER BY usageCount DESC
    """)
    fun getToolUsageStatisticsByDateRange(startDate: java.util.Date, endDate: java.util.Date): Flow<List<ToolUsageStat>>

    @Query("""
        SELECT toolId, AVG(
            CASE 
                WHEN conditionAfter = 'EXCELLENT' THEN 5
                WHEN conditionAfter = 'GOOD' THEN 4
                WHEN conditionAfter = 'FAIR' THEN 3
                WHEN conditionAfter = 'POOR' THEN 2
                WHEN conditionAfter = 'BROKEN' THEN 1
                ELSE 3
            END
        ) as avgCondition
        FROM tool_usage 
        WHERE conditionAfter IS NOT NULL
        GROUP BY toolId
    """)
    fun getToolConditionStatistics(): Flow<List<ToolConditionStat>>
}
