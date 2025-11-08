package com.banimasum.manager.database

import androidx.room.*
import com.banimasum.manager.models.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): Student?

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentByStudentId(studentId: String): Student?

    @Query("SELECT * FROM students WHERE name LIKE '%' || :query || '%' OR studentId LIKE '%' || :query || '%'")
    fun searchStudents(query: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE className = :className ORDER BY name ASC")
    fun getStudentsByClass(className: String): Flow<List<Student>>

    @Insert
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("UPDATE students SET isActive = :isActive WHERE id = :id")
    suspend fun updateStudentStatus(id: Long, isActive: Boolean)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    @Query("SELECT COUNT(*) FROM students WHERE isActive = 1")
    suspend fun getActiveStudentCount(): Int

    @Query("SELECT DISTINCT className FROM students WHERE isActive = 1 ORDER BY className ASC")
    fun getAllClasses(): Flow<List<String>>
}