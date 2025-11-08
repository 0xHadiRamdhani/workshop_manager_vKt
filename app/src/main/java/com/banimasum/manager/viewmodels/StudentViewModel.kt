package com.banimasum.manager.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.banimasum.manager.database.WorkshopDatabase
import com.banimasum.manager.models.Student
import com.banimasum.manager.repositories.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: StudentRepository
    val allStudents: LiveData<List<Student>>
    
    private val _searchResults = MutableLiveData<List<Student>>()
    val searchResults: LiveData<List<Student>> = _searchResults
    
    init {
        val studentDao = WorkshopDatabase.getDatabase(application).studentDao()
        repository = StudentRepository(studentDao)
        allStudents = repository.allStudents.asLiveData()
    }
    
    fun addStudent(student: Student) = viewModelScope.launch {
        repository.insertStudent(student)
    }
    
    fun updateStudent(student: Student) = viewModelScope.launch {
        repository.updateStudent(student)
    }
    
    fun deleteStudent(student: Student) = viewModelScope.launch {
        repository.deleteStudent(student)
    }
    
    fun searchStudents(query: String): LiveData<List<Student>> {
        return repository.searchStudents(query).asLiveData()
    }
    
    fun getStudentById(id: Int): LiveData<Student?> {
        return MutableLiveData<Student?>().apply {
            viewModelScope.launch {
                value = repository.getStudentById(id.toLong())
            }
        }
    }
    
    fun getStudentByStudentId(studentId: String): LiveData<Student?> {
        return MutableLiveData<Student?>().apply {
            viewModelScope.launch {
                value = repository.getStudentByStudentId(studentId)
            }
        }
    }
    
    fun validateStudentId(studentId: String): Boolean {
        return studentId.isNotBlank() && studentId.length >= 3
    }
    
    fun validateFullName(fullName: String): Boolean {
        return fullName.isNotBlank() && fullName.length >= 2
    }
    
    fun validateClassName(className: String): Boolean {
        return className.isNotBlank()
    }
    
    fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) return true // Email is optional
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun validatePhone(phone: String): Boolean {
        if (phone.isEmpty()) return true // Phone is optional
        return phone.matches(Regex("^\\+?[0-9]{10,15}$"))
    }
}