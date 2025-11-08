package com.banimasum.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.banimasum.manager.R
import com.banimasum.manager.databinding.FragmentStudentFormBinding
import com.banimasum.manager.models.Student
import com.banimasum.manager.viewmodels.StudentViewModel
import kotlinx.coroutines.launch

class StudentFormFragment : Fragment() {
    
    private var _binding: FragmentStudentFormBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var studentViewModel: StudentViewModel
    private var currentStudent: Student? = null
    
    private val classOptions = arrayOf(
        "X IPA 1", "X IPA 2", "X IPA 3", "X IPS 1", "X IPS 2",
        "XI IPA 1", "XI IPA 2", "XI IPA 3", "XI IPS 1", "XI IPS 2",
        "XII IPA 1", "XII IPA 2", "XII IPA 3", "XII IPS 1", "XII IPS 2"
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentFormBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupClassDropdown()
        setupClickListeners()
        
        // Check if we're editing an existing student
        currentStudent = arguments?.getParcelable(ARG_STUDENT)
        if (currentStudent != null) {
            populateForm()
        }
    }
    
    private fun setupViewModel() {
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
    }
    
    private fun setupClassDropdown() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            classOptions
        )
        binding.actvClass.setAdapter(adapter)
    }
    
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveStudent()
        }
        
        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    
    private fun populateForm() {
        currentStudent?.let { student ->
            binding.etStudentId.setText(student.studentId)
            binding.etFullName.setText(student.fullName)
            binding.actvClass.setText(student.className, false)
            binding.etEmail.setText(student.email ?: "")
            binding.etPhone.setText(student.phone ?: "")
            binding.etAddress.setText(student.address ?: "")
        }
    }
    
    private fun saveStudent() {
        val studentId = binding.etStudentId.text.toString().trim()
        val fullName = binding.etFullName.text.toString().trim()
        val className = binding.actvClass.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        
        // Validation
        if (studentId.isEmpty()) {
            binding.etStudentId.error = "NIS tidak boleh kosong"
            return
        }
        
        if (fullName.isEmpty()) {
            binding.etFullName.error = "Nama lengkap tidak boleh kosong"
            return
        }
        
        if (className.isEmpty()) {
            binding.actvClass.error = "Kelas tidak boleh kosong"
            return
        }
        
        // Create or update student
        val student = if (currentStudent != null) {
            currentStudent!!.copy(
                studentId = studentId,
                fullName = fullName,
                className = className,
                email = email.takeIf { it.isNotEmpty() },
                phone = phone.takeIf { it.isNotEmpty() },
                address = address.takeIf { it.isNotEmpty() },
                updatedAt = java.util.Date()
            )
        } else {
            Student(
                studentId = studentId,
                fullName = fullName,
                className = className,
                email = email.takeIf { it.isNotEmpty() },
                phone = phone.takeIf { it.isNotEmpty() },
                address = address.takeIf { it.isNotEmpty() },
                createdAt = java.util.Date(),
                updatedAt = java.util.Date()
            )
        }
        
        lifecycleScope.launch {
            try {
                if (currentStudent != null) {
                    studentViewModel.updateStudent(student)
                    Toast.makeText(
                        requireContext(),
                        "Data siswa berhasil diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    studentViewModel.addStudent(student)
                    Toast.makeText(
                        requireContext(),
                        "Siswa baru berhasil ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal menyimpan data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_STUDENT = "student"
        
        fun newInstance(): StudentFormFragment {
            return StudentFormFragment()
        }
        
        fun newInstance(student: Student): StudentFormFragment {
            return StudentFormFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_STUDENT, student)
                }
            }
        }
    }
}