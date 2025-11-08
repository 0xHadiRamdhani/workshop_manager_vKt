package com.banimasum.manager.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.banimasum.manager.R
import com.banimasum.manager.databinding.FragmentWorkshopSessionFormBinding
import com.banimasum.manager.adapters.ToolSelectionAdapter
import com.banimasum.manager.models.WorkshopSession
import com.banimasum.manager.models.SessionStatus
import com.banimasum.manager.models.Student
import com.banimasum.manager.models.Tool
import com.banimasum.manager.viewmodels.StudentViewModel
import com.banimasum.manager.viewmodels.ToolViewModel
import com.banimasum.manager.viewmodels.WorkshopSessionViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class WorkshopSessionFormFragment : Fragment() {
    
    private var _binding: FragmentWorkshopSessionFormBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var studentViewModel: StudentViewModel
    private lateinit var toolViewModel: ToolViewModel
    private lateinit var sessionViewModel: WorkshopSessionViewModel
    
    private lateinit var toolSelectionAdapter: ToolSelectionAdapter
    private val selectedTools = mutableSetOf<Long>()
    private val students = mutableListOf<Student>()
    private val availableTools = mutableListOf<Tool>()
    
    private var currentSession: WorkshopSession? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkshopSessionFormBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get current session from arguments if editing
        currentSession = arguments?.getParcelable("session")
        
        setupViewModels()
        setupUI()
        setupClickListeners()
        loadData()
    }
    
    private fun setupViewModels() {
        studentViewModel = StudentViewModel(requireActivity().application)
        toolViewModel = ToolViewModel(requireActivity().application)
        sessionViewModel = WorkshopSessionViewModel(requireActivity().application)
    }
    
    private fun setupUI() {
        // Setup tool selection adapter
        toolSelectionAdapter = ToolSelectionAdapter(
            onToolSelected = { toolId, isSelected ->
                if (isSelected) {
                    selectedTools.add(toolId)
                } else {
                    selectedTools.remove(toolId)
                }
            }
        )
        
        binding.rvTools.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = toolSelectionAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveSession()
        }
        
        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }
        
        binding.etSessionDate.setOnClickListener {
            showDatePicker()
        }
        
        binding.etStartTime.setOnClickListener {
            showTimePicker(true)
        }
        
        binding.etEndTime.setOnClickListener {
            showTimePicker(false)
        }
    }
    
    private fun loadData() {
        // Load students
        studentViewModel.allStudents.observe(viewLifecycleOwner) { studentList ->
            students.clear()
            students.addAll(studentList)
            
            val studentNames = students.map { "${it.studentId} - ${it.name}" }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                studentNames
            )
            binding.actvStudent.setAdapter(adapter)
            
            // If editing, pre-select student
            if (currentSession != null) {
                val studentIndex = students.indexOfFirst { it.id == currentSession?.studentId }
                if (studentIndex != -1) {
                    binding.actvStudent.setText(studentNames[studentIndex], false)
                }
            }
        }
        
        // Load available tools
        toolViewModel.availableTools.observe(viewLifecycleOwner) { toolList ->
            availableTools.clear()
            availableTools.addAll(toolList)
            toolSelectionAdapter.submitList(toolList)
            
            // If editing, pre-select tools
            if (currentSession != null && currentSession!!.toolsUsed.isNotBlank()) {
                val toolIds = currentSession!!.toolsUsed.split(",").map { it.trim().toLong() }
                selectedTools.clear()
                selectedTools.addAll(toolIds)
                toolSelectionAdapter.setSelectedTools(selectedTools)
            }
        }
        
        // Populate form if editing
        if (currentSession != null) {
            populateForm()
        }
    }
    
    private fun populateForm() {
        currentSession?.let { session ->
            binding.etSessionName.setText(session.projectName)
            binding.etProjectName.setText(session.projectName)
            binding.etSessionDate.setText(dateFormat.format(session.sessionDate))
            binding.etStartTime.setText(session.startTime)
            binding.etEndTime.setText(session.endTime ?: "")
            binding.etInstructorName.setText(session.instructorName)
            binding.etDescription.setText(session.projectDescription ?: "")
            binding.etNotes.setText(session.notes ?: "")
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                binding.etSessionDate.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    
    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val time = String.format("%02d:%02d", hourOfDay, minute)
                if (isStartTime) {
                    binding.etStartTime.setText(time)
                } else {
                    binding.etEndTime.setText(time)
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }
    
    private fun saveSession() {
        val sessionName = binding.etSessionName.text.toString().trim()
        val projectName = binding.etProjectName.text.toString().trim()
        val sessionDateStr = binding.etSessionDate.text.toString().trim()
        val startTime = binding.etStartTime.text.toString().trim()
        val endTime = binding.etEndTime.text.toString().trim()
        val instructorName = binding.etInstructorName.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()
        val studentSelection = binding.actvStudent.text.toString().trim()
        
        // Validate inputs
        if (sessionName.isEmpty()) {
            binding.etSessionName.error = "Nama sesi tidak boleh kosong"
            return
        }
        
        if (projectName.isEmpty()) {
            binding.etProjectName.error = "Nama proyek tidak boleh kosong"
            return
        }
        
        if (studentSelection.isEmpty()) {
            binding.actvStudent.error = "Pilih siswa terlebih dahulu"
            return
        }
        
        if (sessionDateStr.isEmpty()) {
            binding.etSessionDate.error = "Tanggal sesi tidak boleh kosong"
            return
        }
        
        if (startTime.isEmpty()) {
            binding.etStartTime.error = "Waktu mulai tidak boleh kosong"
            return
        }
        
        if (instructorName.isEmpty()) {
            binding.etInstructorName.error = "Nama instruktur tidak boleh kosong"
            return
        }
        
        // Parse student ID from selection
        val studentId = try {
            val studentName = studentSelection.substringBefore(" - ")
            val student = students.find { it.studentId == studentName }
            student?.id ?: 0L
        } catch (e: Exception) {
            binding.actvStudent.error = "Pilih siswa yang valid"
            return
        }
        
        // Parse date
        val sessionDate = try {
            dateFormat.parse(sessionDateStr)
        } catch (e: Exception) {
            binding.etSessionDate.error = "Format tanggal tidak valid"
            return
        }
        
        // Create tools used string
        val toolsUsed = selectedTools.joinToString(",")
        
        // Create or update session
        val session = if (currentSession != null) {
            currentSession!!.copy(
                studentId = studentId,
                projectName = projectName,
                projectDescription = description.takeIf { it.isNotEmpty() },
                sessionDate = sessionDate,
                startTime = startTime,
                endTime = endTime.takeIf { it.isNotEmpty() },
                instructorName = instructorName,
                toolsUsed = toolsUsed,
                notes = notes.takeIf { it.isNotEmpty() },
                updatedAt = Date()
            )
        } else {
            WorkshopSession(
                studentId = studentId,
                projectName = projectName,
                projectDescription = description.takeIf { it.isNotEmpty() },
                sessionDate = sessionDate,
                startTime = startTime,
                endTime = endTime.takeIf { it.isNotEmpty() },
                instructorName = instructorName,
                toolsUsed = toolsUsed,
                notes = notes.takeIf { it.isNotEmpty() },
                sessionStatus = SessionStatus.SCHEDULED,
                createdAt = Date(),
                updatedAt = Date()
            )
        }
        
        lifecycleScope.launch {
            try {
                if (currentSession != null) {
                    sessionViewModel.updateSession(session)
                    Toast.makeText(
                        requireContext(),
                        "Sesi workshop berhasil diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    sessionViewModel.addSession(session)
                    Toast.makeText(
                        requireContext(),
                        "Sesi workshop baru berhasil ditambahkan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                
                requireActivity().onBackPressed()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal menyimpan sesi: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}