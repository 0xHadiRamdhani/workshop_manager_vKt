package com.banimasum.manager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.banimasum.manager.database.WorkshopDatabase
import com.banimasum.manager.models.Tool
import com.banimasum.manager.repositories.ToolRepository
import com.banimasum.manager.repositories.ToolUsageRepository
import kotlinx.coroutines.launch

class ToolViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ToolRepository
    val allTools: LiveData<List<Tool>>
    
    private val _searchResults = MutableLiveData<List<Tool>>()
    val searchResults: LiveData<List<Tool>> = _searchResults
    
    init {
        val toolDao = WorkshopDatabase.getDatabase(application).toolDao()
        val toolUsageRepository = ToolUsageRepository(WorkshopDatabase.getDatabase(application).toolUsageDao())
        repository = ToolRepository(toolDao, toolUsageRepository)
        allTools = repository.allTools.asLiveData()
    }
    
    fun addTool(tool: Tool) = viewModelScope.launch {
        repository.insertTool(tool)
    }
    
    fun updateTool(tool: Tool) = viewModelScope.launch {
        repository.updateTool(tool)
    }
    
    fun deleteTool(tool: Tool) = viewModelScope.launch {
        repository.deleteTool(tool)
    }
    
    fun searchTools(query: String): LiveData<List<Tool>> {
        return repository.searchTools(query).asLiveData()
    }
    
    fun getToolById(id: Int): LiveData<Tool?> {
        return MutableLiveData<Tool?>().apply {
            viewModelScope.launch {
                value = repository.getToolById(id.toLong())
            }
        }
    }
    
    fun getToolByCode(toolCode: String): LiveData<Tool?> {
        return MutableLiveData<Tool?>().apply {
            viewModelScope.launch {
                value = repository.getToolByCode(toolCode)
            }
        }
    }
    
    val availableTools: LiveData<List<Tool>> = repository.availableTools.asLiveData()
    
    fun getToolsByCategory(category: String): LiveData<List<Tool>> {
        return repository.getToolsByCategory(category).asLiveData()
    }
    
    fun validateToolCode(toolCode: String): Boolean {
        return toolCode.isNotBlank() && toolCode.length >= 3
    }
    
    fun validateToolName(toolName: String): Boolean {
        return toolName.isNotBlank() && toolName.length >= 2
    }
    
    fun validateCategory(category: String): Boolean {
        return category.isNotBlank()
    }
    
    fun validateQuantity(quantity: Int): Boolean {
        return quantity > 0
    }
    
    fun validateStatus(status: String): Boolean {
        return status.isNotBlank()
    }
}