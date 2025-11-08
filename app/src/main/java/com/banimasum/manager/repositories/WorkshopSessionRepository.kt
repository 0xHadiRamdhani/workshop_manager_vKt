package com.banimasum.manager.repositories

import com.banimasum.manager.database.WorkshopSessionDao
import com.banimasum.manager.models.SessionStatus
import com.banimasum.manager.models.WorkshopSession
import com.banimasum.manager.models.MonthlySessionStat
import com.banimasum.manager.models.StudentSessionStat
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.Calendar

class WorkshopSessionRepository(
    private val sessionDao: WorkshopSessionDao,
    private val toolRepository: ToolRepository,
    private val toolUsageRepository: ToolUsageRepository
) {
    
    val allSessions: Flow<List<WorkshopSession>> = sessionDao.getAllSessions()
    val upcomingSessions: Flow<List<WorkshopSession>> = sessionDao.getUpcomingSessions(Date())
    val activeSessions: Flow<List<WorkshopSession>> = sessionDao.getActiveSessions()
    val recentCompletedSessions: Flow<List<WorkshopSession>> = sessionDao.getRecentCompletedSessions()

    suspend fun insertSession(session: WorkshopSession): Long {
        return sessionDao.insertSession(session.copy(createdAt = Date(), updatedAt = Date()))
    }

    suspend fun updateSession(session: WorkshopSession) {
        sessionDao.updateSession(session.copy(updatedAt = Date()))
    }

    suspend fun deleteSession(session: WorkshopSession) {
        sessionDao.deleteSession(session)
    }

    suspend fun getSessionById(id: Long): WorkshopSession? {
        return sessionDao.getSessionById(id)
    }

    fun getSessionsByStudent(studentId: Long): Flow<List<WorkshopSession>> {
        return sessionDao.getSessionsByStudent(studentId)
    }

    fun getSessionsByStatus(status: SessionStatus): Flow<List<WorkshopSession>> {
        return sessionDao.getSessionsByStatus(status)
    }

    fun getSessionsByDate(date: Date): Flow<List<WorkshopSession>> {
        return sessionDao.getSessionsByDate(date)
    }

    fun getSessionsByDateRange(startDate: Date, endDate: Date): Flow<List<WorkshopSession>> {
        return sessionDao.getSessionsByDateRange(startDate, endDate)
    }

    fun searchSessions(query: String): Flow<List<WorkshopSession>> {
        return sessionDao.searchSessions(query)
    }

    suspend fun updateSessionStatus(id: Long, status: SessionStatus) {
        sessionDao.updateSessionStatus(id, status)
    }

    suspend fun completeSession(id: Long, endTime: String) {
        sessionDao.completeSession(id, endTime)
    }

    suspend fun getSessionCount(): Int {
        return sessionDao.getSessionCount()
    }

    suspend fun getSessionCountByStatus(status: SessionStatus): Int {
        return sessionDao.getSessionCountByStatus(status)
    }

    suspend fun getStudentSessionCount(studentId: Long): Int {
        return sessionDao.getStudentSessionCount(studentId)
    }

    suspend fun getSessionCountByDate(date: Date): Int {
        return sessionDao.getSessionCountByDate(date)
    }

    fun getMonthlySessionStats(): Flow<List<MonthlySessionStat>> {
        return sessionDao.getMonthlySessionStats()
    }

    fun getTopStudentsBySessionCount(): Flow<List<StudentSessionStat>> {
        return sessionDao.getTopStudentsBySessionCount()
    }

    suspend fun validateSession(session: WorkshopSession): ValidationResult {
        val errors = mutableListOf<String>()

        if (session.studentId == 0L) {
            errors.add("Siswa harus dipilih")
        }

        if (session.projectName.isBlank()) {
            errors.add("Nama proyek tidak boleh kosong")
        }

        if (session.startTime.isBlank()) {
            errors.add("Waktu mulai tidak boleh kosong")
        }

        if (session.instructorName.isBlank()) {
            errors.add("Nama instruktur tidak boleh kosong")
        }

        // Validate session date
        val today = Calendar.getInstance()
        val sessionDate = Calendar.getInstance().apply { time = session.sessionDate }
        
        if (sessionDate.before(today) && session.sessionStatus == SessionStatus.SCHEDULED) {
            errors.add("Tanggal sesi tidak boleh di masa lalu")
        }

        // Validate time format
        if (!isValidTimeFormat(session.startTime)) {
            errors.add("Format waktu mulai tidak valid (HH:mm)")
        }

        if (session.endTime != null && session.endTime.isNotBlank() && !isValidTimeFormat(session.endTime)) {
            errors.add("Format waktu selesai tidak valid (HH:mm)")
        }

        // Validate tools if provided
        if (session.toolsUsed.isNotBlank()) {
            try {
                val toolIds = session.toolsUsed.split(",").map { it.trim().toLong() }
                for (toolId in toolIds) {
                    val tool = toolRepository.getToolById(toolId)
                    if (tool == null) {
                        errors.add("Alat dengan ID $toolId tidak ditemukan")
                    } else if (!toolRepository.canToolBeBorrowed(toolId, 1)) {
                        errors.add("Alat ${tool.toolName} tidak tersedia")
                    }
                }
            } catch (e: NumberFormatException) {
                errors.add("Format ID alat tidak valid")
            }
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun isValidTimeFormat(time: String): Boolean {
        return time.matches(Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$"))
    }

    suspend fun startSession(sessionId: Long): Boolean {
        return try {
            val session = sessionDao.getSessionById(sessionId)
            if (session != null && session.sessionStatus == SessionStatus.SCHEDULED) {
                // Reserve tools if specified
                if (session.toolsUsed.isNotBlank()) {
                    val toolIds = session.toolsUsed.split(",").map { it.trim().toLong() }
                    for (toolId in toolIds) {
                        toolRepository.decrementAvailableQuantity(toolId, 1)
                    }
                }
                sessionDao.updateSessionStatus(sessionId, SessionStatus.IN_PROGRESS)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun endSession(sessionId: Long, endTime: String): Boolean {
        return try {
            val session = sessionDao.getSessionById(sessionId)
            if (session != null && session.sessionStatus == SessionStatus.IN_PROGRESS) {
                // Return tools if they were reserved
                if (session.toolsUsed.isNotBlank()) {
                    val toolIds = session.toolsUsed.split(",").map { it.trim().toLong() }
                    for (toolId in toolIds) {
                        toolRepository.incrementAvailableQuantity(toolId, 1)
                    }
                }
                sessionDao.completeSession(sessionId, endTime)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun cancelSession(sessionId: Long): Boolean {
        return try {
            val session = sessionDao.getSessionById(sessionId)
            if (session != null && session.sessionStatus in listOf(SessionStatus.SCHEDULED, SessionStatus.IN_PROGRESS)) {
                // Return tools if they were reserved
                if (session.sessionStatus == SessionStatus.IN_PROGRESS && session.toolsUsed.isNotBlank()) {
                    val toolIds = session.toolsUsed.split(",").map { it.trim().toLong() }
                    for (toolId in toolIds) {
                        toolRepository.incrementAvailableQuantity(toolId, 1)
                    }
                }
                sessionDao.updateSessionStatus(sessionId, SessionStatus.CANCELLED)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}