package com.banimasum.manager.database

import androidx.room.*
import com.banimasum.manager.models.Tool
import com.banimasum.manager.models.ToolCondition
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Query("SELECT * FROM tools ORDER BY name ASC")
    fun getAllTools(): Flow<List<Tool>>

    @Query("SELECT * FROM tools WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveTools(): Flow<List<Tool>>

    @Query("SELECT * FROM tools WHERE availableQuantity > 0 AND isActive = 1 ORDER BY name ASC")
    fun getAvailableTools(): Flow<List<Tool>>

    @Query("SELECT * FROM tools WHERE id = :id")
    suspend fun getToolById(id: Long): Tool?

    @Query("SELECT * FROM tools WHERE toolCode = :toolCode")
    suspend fun getToolByCode(toolCode: String): Tool?

    @Query("SELECT * FROM tools WHERE name LIKE '%' || :query || '%' OR toolCode LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchTools(query: String): Flow<List<Tool>>

    @Query("SELECT * FROM tools WHERE category = :category ORDER BY name ASC")
    fun getToolsByCategory(category: String): Flow<List<Tool>>

    @Query("SELECT * FROM tools WHERE condition = :condition ORDER BY name ASC")
    fun getToolsByCondition(condition: ToolCondition): Flow<List<Tool>>

    @Insert
    suspend fun insertTool(tool: Tool): Long

    @Update
    suspend fun updateTool(tool: Tool)

    @Delete
    suspend fun deleteTool(tool: Tool)

    @Query("UPDATE tools SET availableQuantity = availableQuantity - :quantity WHERE id = :id")
    suspend fun decrementAvailableQuantity(id: Long, quantity: Int)

    @Query("UPDATE tools SET availableQuantity = availableQuantity + :quantity WHERE id = :id")
    suspend fun incrementAvailableQuantity(id: Long, quantity: Int)

    @Query("UPDATE tools SET isActive = :isActive WHERE id = :id")
    suspend fun updateToolStatus(id: Long, isActive: Boolean)

    @Query("SELECT COUNT(*) FROM tools")
    suspend fun getToolCount(): Int

    @Query("SELECT COUNT(*) FROM tools WHERE isActive = 1")
    suspend fun getActiveToolCount(): Int

    @Query("SELECT COUNT(*) FROM tools WHERE availableQuantity > 0 AND isActive = 1")
    suspend fun getAvailableToolCount(): Int

    @Query("SELECT DISTINCT category FROM tools WHERE isActive = 1 ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT SUM(quantity) FROM tools WHERE isActive = 1")
    suspend fun getTotalToolQuantity(): Int

    @Query("SELECT SUM(availableQuantity) FROM tools WHERE isActive = 1")
    suspend fun getTotalAvailableQuantity(): Int
}