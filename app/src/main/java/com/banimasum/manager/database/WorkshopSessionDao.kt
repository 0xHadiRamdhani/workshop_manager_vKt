package com.banimasum.manager.database

import androidx.room.*
import com.banimasum.manager.models.SessionStatus
import com.banimasum.manager.models.WorkshopSession
import com.banimasum.manager.models.MonthlySessionStat
import com.banimasum.manager.models.StudentSessionStat
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WorkshopSessionDao {
    @Query("SELECT * FROM workshop_sessions ORDER BY sessionDate DESC, startTime DESC")
    fun getAllSessions(): Flow<List<WorkshopSession>>

    @Query("SELECT * FROM workshop_sessions WHERE sessionDate >= :startDate AND sessionDate <= :endDate ORDER BY sessionDate DESC, startTime DESC")
    fun getSessionsByDateRange(startDate: Date, endDate: Date): Flow<List<WorkshopSession>>

    @Query("SELECT * FROM workshop_sessions WHERE studentId = :studentId ORDER BY sessionDate DESC, startTime DESC")
    fun getSessionsByStudent(studentId: Long): Flow<List<WorkshopSession>>

    @Query("SELECT * FROM workshop_sessions WHERE sessionStatus = :status ORDER BY sessionDate DESC, startTime DESC")
    fun getSessionsByStatus(status: SessionStatus): Flow<List<WorkshopSession>>

    @Query("SELECT * FROM workshop_sessions WHERE sessionDate = :date ORDER BY startTime ASC")
    fun getSessionsByDate(date: Date): Flow<List<WorkshopSession>>

    @Query("SELECT * FROM workshop_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): WorkshopSession?

    @Query("SELECT * FROM workshop_sessions WHERE sessionStatus = 'SCHEDULED' AND sessionDate >= :today ORDER BY sessionDate ASC, startTime ASC")
    fun getUpcomingSessions(today: Date): Flow<List<WorkshopSession>>

    @Query("SELECT * FROM workshop_sessions WHERE sessionStatus = 'IN_PROGRESS' ORDER BY sessionDate DESC, startTime DESC")
    fun getActiveSessions(): Flow<List<WorkshopSession>>

    @Query("SELECT * FROM workshop_sessions WHERE sessionStatus = 'COMPLETED' ORDER BY sessionDate DESC, startTime DESC LIMIT :limit")
    fun getRecentCompletedSessions(limit: Int = 10): Flow<List<WorkshopSession>>

    @Query("""
        SELECT * FROM workshop_sessions 
        WHERE projectName LIKE '%' || :query || '%' 
        OR projectDescription LIKE '%' || :query || '%' 
        OR instructorName LIKE '%' || :query || '%'
        ORDER BY sessionDate DESC, startTime DESC
    """)
    fun searchSessions(query: String): Flow<List<WorkshopSession>>

    @Insert
    suspend fun insertSession(session: WorkshopSession): Long

    @Update
    suspend fun updateSession(session: WorkshopSession)

    @Delete
    suspend fun deleteSession(session: WorkshopSession)

    @Query("UPDATE workshop_sessions SET sessionStatus = :status WHERE id = :id")
    suspend fun updateSessionStatus(id: Long, status: SessionStatus)

    @Query("UPDATE workshop_sessions SET endTime = :endTime, sessionStatus = 'COMPLETED' WHERE id = :id")
    suspend fun completeSession(id: Long, endTime: String)

    @Query("SELECT COUNT(*) FROM workshop_sessions")
    suspend fun getSessionCount(): Int

    @Query("SELECT COUNT(*) FROM workshop_sessions WHERE sessionStatus = :status")
    suspend fun getSessionCountByStatus(status: SessionStatus): Int

    @Query("SELECT COUNT(*) FROM workshop_sessions WHERE studentId = :studentId")
    suspend fun getStudentSessionCount(studentId: Long): Int

    @Query("SELECT COUNT(*) FROM workshop_sessions WHERE sessionDate = :date")
    suspend fun getSessionCountByDate(date: Date): Int

    @Query("""
        SELECT strftime('%Y-%m', sessionDate/1000, 'unixepoch') as month, 
               COUNT(*) as count 
        FROM workshop_sessions 
        WHERE sessionStatus = 'COMPLETED' 
        GROUP BY strftime('%Y-%m', sessionDate/1000, 'unixepoch')
        ORDER BY month DESC
    """)
    fun getMonthlySessionStats(): Flow<List<MonthlySessionStat>>

    @Query("""
        SELECT studentId, COUNT(*) as sessionCount 
        FROM workshop_sessions 
        WHERE sessionStatus = 'COMPLETED' 
        GROUP BY studentId 
        ORDER BY sessionCount DESC
    """)
    fun getTopStudentsBySessionCount(): Flow<List<StudentSessionStat>>
}
