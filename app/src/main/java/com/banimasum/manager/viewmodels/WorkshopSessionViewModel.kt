package com.banimasum.manager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.banimasum.manager.database.WorkshopDatabase
import com.banimasum.manager.models.WorkshopSession
import com.banimasum.manager.models.SessionStatus
import com.banimasum.manager.repositories.WorkshopSessionRepository
import kotlinx.coroutines.launch
import java.util.Date

class WorkshopSessionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: WorkshopSessionRepository
    val allSessions: LiveData<List<WorkshopSession>>
    val scheduledSessions: LiveData<List<WorkshopSession>>
    val activeSessions: LiveData<List<WorkshopSession>>
    val completedSessions: LiveData<List<WorkshopSession>>
    
    init {
        val sessionDao = WorkshopDatabase.getDatabase(application).workshopSessionDao()
        val studentDao = WorkshopDatabase.getDatabase(application).studentDao()
        val toolDao = WorkshopDatabase.getDatabase(application).toolDao()
        val toolRepository = com.banimasum.manager.repositories.ToolRepository(toolDao)
        val toolUsageRepository = com.banimasum.manager.repositories.ToolUsageRepository(WorkshopDatabase.getDatabase(application).toolUsageDao())
        repository = WorkshopSessionRepository(sessionDao, toolRepository, toolUsageRepository)
        allSessions = repository.allSessions.asLiveData()
        scheduledSessions = repository.getSessionsByStatus(SessionStatus.SCHEDULED).asLiveData()
        activeSessions = repository.getSessionsByStatus(SessionStatus.IN_PROGRESS).asLiveData()
        completedSessions = repository.getSessionsByStatus(SessionStatus.COMPLETED).asLiveData()
    }
    
    fun addSession(session: WorkshopSession) = viewModelScope.launch {
        repository.insertSession(session)
    }
    
    fun updateSession(session: WorkshopSession) = viewModelScope.launch {
        repository.updateSession(session)
    }
    
    fun deleteSession(session: WorkshopSession) = viewModelScope.launch {
        repository.deleteSession(session)
    }
    
    fun searchSessions(query: String): LiveData<List<WorkshopSession>> {
        return repository.searchSessions(query).asLiveData()
    }
    
    fun getSessionsByDate(date: Date): LiveData<List<WorkshopSession>> {
        return repository.getSessionsByDate(date).asLiveData()
    }
    
    fun getSessionsByStudent(studentId: Long): LiveData<List<WorkshopSession>> {
        return repository.getSessionsByStudent(studentId).asLiveData()
    }
    
    fun startSession(sessionId: Long) = viewModelScope.launch {
        repository.startSession(sessionId)
    }
    
    fun endSession(sessionId: Long, endTime: String) = viewModelScope.launch {
        repository.endSession(sessionId, endTime)
    }
    
    fun cancelSession(sessionId: Long) = viewModelScope.launch {
        repository.cancelSession(sessionId)
    }
    
    fun getSessionById(id: Long): LiveData<WorkshopSession?> {
        return MutableLiveData<WorkshopSession?>().apply {
            viewModelScope.launch {
                value = repository.getSessionById(id)
            }
        }
    }
}