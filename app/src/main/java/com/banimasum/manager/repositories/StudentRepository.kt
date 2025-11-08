package com.banimasum.manager.repositories

import com.banimasum.manager.database.StudentDao
import com.banimasum.manager.models.Student
import kotlinx.coroutines.flow.Flow
import java.util.Date

class StudentRepository(private val studentDao: StudentDao) {
    
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    val activeStudents: Flow<List<Student>> = studentDao.getActiveStudents()
    val allClasses: Flow<List<String>> = studentDao.getAllClasses()

    suspend fun insertStudent(student: Student): Long {
        return studentDao.insertStudent(student.copy(createdAt = Date(), updatedAt = Date()))
    }

    suspend fun updateStudent(student: Student) {
        studentDao.updateStudent(student.copy(updatedAt = Date()))
    }

    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
    }

    suspend fun getStudentById(id: Long): Student? {
        return studentDao.getStudentById(id)
    }

    suspend fun getStudentByStudentId(studentId: String): Student? {
        return studentDao.getStudentByStudentId(studentId)
    }

    fun searchStudents(query: String): Flow<List<Student>> {
        return studentDao.searchStudents(query)
    }

    fun getStudentsByClass(className: String): Flow<List<Student>> {
        return studentDao.getStudentsByClass(className)
    }

    suspend fun updateStudentStatus(id: Long, isActive: Boolean) {
        studentDao.updateStudentStatus(id, isActive)
    }

    suspend fun getStudentCount(): Int {
        return studentDao.getStudentCount()
    }

    suspend fun getActiveStudentCount(): Int {
        return studentDao.getActiveStudentCount()
    }

    suspend fun isStudentIdUnique(studentId: String, excludeId: Long = 0): Boolean {
        val existingStudent = studentDao.getStudentByStudentId(studentId)
        return existingStudent == null || existingStudent.id.toInt() == excludeId.toInt()
    }

    suspend fun validateStudent(student: Student): ValidationResult {
        val errors = mutableListOf<String>()

        if (student.fullName.isBlank()) {
            errors.add("Nama siswa tidak boleh kosong")
        }

        if (student.studentId.isBlank()) {
            errors.add("Nomor induk siswa tidak boleh kosong")
        } else if (!isStudentIdUnique(student.studentId, student.id.toLong())) {
            errors.add("Nomor induk siswa sudah digunakan")
        }

        if (student.className.isBlank()) {
            errors.add("Kelas tidak boleh kosong")
        }

        if (student.email != null && student.email.isNotBlank() && !isValidEmail(student.email)) {
            errors.add("Format email tidak valid")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)