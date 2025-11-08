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
import com.banimasum.manager.databinding.FragmentToolFormBinding
import com.banimasum.manager.models.Tool
import com.banimasum.manager.viewmodels.ToolViewModel
import kotlinx.coroutines.launch

class ToolFormFragment : Fragment() {
    
    private var _binding: FragmentToolFormBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var toolViewModel: ToolViewModel
    private var currentTool: Tool? = null
    
    private val categoryOptions = arrayOf(
        "Elektronik", "Mekanik", "Listrik", "Pneumatik", "Hidrolik",
        "Pengukuran", "Pemotong", "Penggiling", "Las", "Kayu",
        "Logam", "Plastik", "Lainnya"
    )
    
    private val statusOptions = arrayOf(
        "Tersedia", "Dipinjam", "Rusak", "Perawatan", "Hilang"
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToolFormBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupDropdowns()
        setupClickListeners()
        
        // Check if we're editing an existing tool
        currentTool = arguments?.getParcelable(ARG_TOOL)
        if (currentTool != null) {
            populateForm()
        }
    }
    
    private fun setupViewModel() {
        toolViewModel = ViewModelProvider(this)[ToolViewModel::class.java]
    }
    
    private fun setupDropdowns() {
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categoryOptions
        )
        binding.actvCategory.setAdapter(categoryAdapter)
        
        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statusOptions
        )
        binding.actvStatus.setAdapter(statusAdapter)
    }
    
    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveTool()
        }
        
        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    
    private fun populateForm() {
        currentTool?.let { tool ->
            binding.etToolCode.setText(tool.toolCode)
            binding.etToolName.setText(tool.toolName)
            binding.actvCategory.setText(tool.category, false)
            binding.etDescription.setText(tool.description ?: "")
            binding.etQuantity.setText(tool.quantity.toString())
            binding.actvStatus.setText(tool.status, false)
        }
    }
    
    private fun saveTool() {
        val toolCode = binding.etToolCode.text.toString().trim()
        val toolName = binding.etToolName.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val status = binding.actvStatus.text.toString().trim()
        
        // Validation
        if (toolCode.isEmpty()) {
            binding.etToolCode.error = "Kode alat tidak boleh kosong"
            return
        }
        
        if (toolName.isEmpty()) {
            binding.etToolName.error = "Nama alat tidak boleh kosong"
            return
        }
        
        if (category.isEmpty()) {
            binding.actvCategory.error = "Kategori tidak boleh kosong"
            return
        }
        
        if (quantityStr.isEmpty()) {
            binding.etQuantity.error = "Jumlah tidak boleh kosong"
            return
        }
        
        val quantity = try {
            quantityStr.toInt()
        } catch (e: NumberFormatException) {
            binding.etQuantity.error = "Jumlah harus berupa angka"
            return
        }
        
        if (quantity <= 0) {
            binding.etQuantity.error = "Jumlah harus lebih dari 0"
            return
        }
        
        if (status.isEmpty()) {
            binding.actvStatus.error = "Status tidak boleh kosong"
            return
        }
        
        // Create or update tool
        val tool = if (currentTool != null) {
            currentTool!!.copy(
                toolCode = toolCode,
                toolName = toolName,
                category = category,
                description = description.takeIf { it.isNotEmpty() },
                quantity = quantity,
                status = status,
                updatedAt = java.util.Date()
            )
        } else {
            Tool(
                toolCode = toolCode,
                toolName = toolName,
                category = category,
                description = description.takeIf { it.isNotEmpty() },
                quantity = quantity,
                status = status,
                createdAt = java.util.Date(),
                updatedAt = java.util.Date()
            )
        }
        
        lifecycleScope.launch {
            try {
                if (currentTool != null) {
                    toolViewModel.updateTool(tool)
                    Toast.makeText(
                        requireContext(),
                        "Data alat berhasil diperbarui",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    toolViewModel.addTool(tool)
                    Toast.makeText(
                        requireContext(),
                        "Alat baru berhasil ditambahkan",
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
        private const val ARG_TOOL = "tool"
        
        fun newInstance(): ToolFormFragment {
            return ToolFormFragment()
        }
        
        fun newInstance(tool: Tool): ToolFormFragment {
            return ToolFormFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_TOOL, tool)
                }
            }
        }
    }
}