package com.banimasum.manager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.banimasum.manager.database.WorkshopDatabase
import com.banimasum.manager.models.WorkshopSession
import com.banimasum.manager.repositories.StudentRepository
import com.banimasum.manager.repositories.ToolRepository
import com.banimasum.manager.repositories.WorkshopSessionRepository
import com.banimasum.manager.repositories.ToolUsageRepository
import kotlinx.coroutines.launch
import java.util.*

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = WorkshopDatabase.getDatabase(application)
    
    private val studentRepository = StudentRepository(database.studentDao())
    private val toolUsageRepository = ToolUsageRepository(database.toolUsageDao())
    private val toolRepository = ToolRepository(database.toolDao(), toolUsageRepository)
    private val sessionRepository = WorkshopSessionRepository(
        database.workshopSessionDao(),
        toolRepository,
        toolUsageRepository
    )
    
    private val _studentCount = MutableLiveData<Int>()
    val studentCount: LiveData<Int> = _studentCount
    
    private val _toolCount = MutableLiveData<Int>()
    val toolCount: LiveData<Int> = _toolCount
    
    private val _todaySessions = MutableLiveData<List<WorkshopSession>>()
    val todaySessions: LiveData<List<WorkshopSession>> = _todaySessions
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    init {
        loadDashboardData()
    }
    
    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Load student count
                val studentCount = studentRepository.getActiveStudentCount()
                _studentCount.value = studentCount
                
                // Load tool count
                val toolCount = toolRepository.getAvailableToolCount()
                _toolCount.value = toolCount
                
                // Load today's sessions
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                
                val tomorrow = Calendar.getInstance().apply {
                    time = today
                    add(Calendar.DAY_OF_MONTH, 1)
                }.time
                
                val sessions = sessionRepository.getSessionsByDateRange(today, tomorrow)
                sessions.collect { sessionList ->
                    _todaySessions.value = sessionList
                }
                
            } catch (e: Exception) {
                // Handle error
                _studentCount.value = 0
                _toolCount.value = 0
                _todaySessions.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshData() {
        loadDashboardData()
    }
}