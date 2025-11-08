package com.banimasum.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.banimasum.manager.R
import com.banimasum.manager.adapters.StudentAdapter
import com.banimasum.manager.databinding.FragmentStudentListBinding
import com.banimasum.manager.models.Student
import com.banimasum.manager.viewmodels.StudentViewModel
import kotlinx.coroutines.launch

class StudentListFragment : Fragment() {
    
    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var studentAdapter: StudentAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeStudents()
    }
    
    private fun setupViewModel() {
        studentViewModel = ViewModelProvider(this)[StudentViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter(
            onEditClick = { student -> editStudent(student) },
            onDeleteClick = { student -> deleteStudent(student) }
        )
        
        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnAddStudent.setOnClickListener {
            // Navigate to add student form
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, StudentFormFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        
        binding.btnSearch.setOnClickListener {
            searchStudents()
        }
    }
    
    private fun observeStudents() {
        studentViewModel.allStudents.observe(viewLifecycleOwner) { students ->
            if (students.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                studentAdapter.submitList(students)
            }
        }
    }
    
    private fun searchStudents() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            studentViewModel.searchStudents(query).observe(viewLifecycleOwner) { students ->
                studentAdapter.submitList(students)
                if (students.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                }
            }
        } else {
            observeStudents()
        }
    }
    
    private fun editStudent(student: Student) {
        // Navigate to edit student form
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StudentFormFragment.newInstance(student))
            .addToBackStack(null)
            .commit()
    }
    
    private fun deleteStudent(student: Student) {
        lifecycleScope.launch {
            try {
                studentViewModel.deleteStudent(student)
                Toast.makeText(
                    requireContext(),
                    "Siswa berhasil dihapus",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal menghapus siswa: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun showEmptyState() {
        binding.rvStudents.visibility = View.GONE
        binding.llEmptyState.visibility = View.VISIBLE
    }
    
    private fun hideEmptyState() {
        binding.rvStudents.visibility = View.VISIBLE
        binding.llEmptyState.visibility = View.GONE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        fun newInstance() = StudentListFragment()
    }
}