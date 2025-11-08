package com.banimasum.manager.repositories

import com.banimasum.manager.database.ToolDao
import com.banimasum.manager.models.Tool
import com.banimasum.manager.models.ToolCondition
import com.banimasum.manager.models.ToolUsageStat
import com.banimasum.manager.models.ToolConditionStat
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ToolRepository(
    private val toolDao: ToolDao,
    private val toolUsageRepository: ToolUsageRepository
) {
    
    val allTools: Flow<List<Tool>> = toolDao.getAllTools()
    val activeTools: Flow<List<Tool>> = toolDao.getActiveTools()
    val availableTools: Flow<List<Tool>> = toolDao.getAvailableTools()
    val allCategories: Flow<List<String>> = toolDao.getAllCategories()

    suspend fun insertTool(tool: Tool): Long {
        return toolDao.insertTool(tool.copy(createdAt = Date(), updatedAt = Date()))
    }

    suspend fun updateTool(tool: Tool) {
        toolDao.updateTool(tool.copy(updatedAt = Date()))
    }

    suspend fun deleteTool(tool: Tool) {
        toolDao.deleteTool(tool)
    }

    suspend fun getToolById(id: Long): Tool? {
        return toolDao.getToolById(id)
    }

    suspend fun getToolByCode(toolCode: String): Tool? {
        return toolDao.getToolByCode(toolCode)
    }

    fun searchTools(query: String): Flow<List<Tool>> {
        return toolDao.searchTools(query)
    }

    fun getToolsByCategory(category: String): Flow<List<Tool>> {
        return toolDao.getToolsByCategory(category)
    }

    fun getToolsByCondition(condition: ToolCondition): Flow<List<Tool>> {
        return toolDao.getToolsByCondition(condition)
    }

    suspend fun updateToolStatus(id: Long, isActive: Boolean) {
        toolDao.updateToolStatus(id, isActive)
    }

    suspend fun decrementAvailableQuantity(id: Long, quantity: Int) {
        toolDao.decrementAvailableQuantity(id, quantity)
    }

    suspend fun incrementAvailableQuantity(id: Long, quantity: Int) {
        toolDao.incrementAvailableQuantity(id, quantity)
    }

    suspend fun getToolCount(): Int {
        return toolDao.getToolCount()
    }

    suspend fun getActiveToolCount(): Int {
        return toolDao.getActiveToolCount()
    }

    suspend fun getAvailableToolCount(): Int {
        return toolDao.getAvailableToolCount()
    }

    suspend fun getTotalToolQuantity(): Int {
        return toolDao.getTotalToolQuantity()
    }

    suspend fun getTotalAvailableQuantity(): Int {
        return toolDao.getTotalAvailableQuantity()
    }

    suspend fun isToolCodeUnique(toolCode: String, excludeId: Long = 0): Boolean {
        val existingTool = toolDao.getToolByCode(toolCode)
        return existingTool == null || existingTool.id?.toLong() == excludeId
    }

    suspend fun canToolBeBorrowed(toolId: Long, quantity: Int): Boolean {
        val tool = toolDao.getToolById(toolId)
        return tool != null && tool.quantity >= quantity && tool.status == "Tersedia"
    }

    suspend fun validateTool(tool: Tool): ValidationResult {
        val errors = mutableListOf<String>()

        if (tool.toolName.isBlank()) {
            errors.add("Nama alat tidak boleh kosong")
        }

        if (tool.toolCode.isBlank()) {
            errors.add("Kode alat tidak boleh kosong")
        } else if (!isToolCodeUnique(tool.toolCode, tool.id?.toLong() ?: 0)) {
            errors.add("Kode alat sudah digunakan")
        }

        if (tool.category.isBlank()) {
            errors.add("Kategori alat tidak boleh kosong")
        }

        if (tool.quantity < 1) {
            errors.add("Jumlah alat minimal 1")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun getToolUsageStatistics(): Flow<List<ToolUsageStat>> {
        return toolUsageRepository.getToolUsageStatistics()
    }

    fun getToolConditionStatistics(): Flow<List<ToolConditionStat>> {
        return toolUsageRepository.getToolConditionStatistics()
    }
}