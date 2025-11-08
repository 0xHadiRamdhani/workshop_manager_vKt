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
import com.banimasum.manager.adapters.ToolAdapter
import com.banimasum.manager.databinding.FragmentToolListBinding
import com.banimasum.manager.models.Tool
import com.banimasum.manager.viewmodels.ToolViewModel
import kotlinx.coroutines.launch

class ToolListFragment : Fragment() {
    
    private var _binding: FragmentToolListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var toolViewModel: ToolViewModel
    private lateinit var toolAdapter: ToolAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToolListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeTools()
    }
    
    private fun setupViewModel() {
        toolViewModel = ViewModelProvider(this)[ToolViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        toolAdapter = ToolAdapter(
            onEditClick = { tool -> editTool(tool) },
            onDeleteClick = { tool -> deleteTool(tool) }
        )
        
        binding.rvTools.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = toolAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnAddTool.setOnClickListener {
            // Navigate to add tool form
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ToolFormFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        
        binding.btnSearch.setOnClickListener {
            searchTools()
        }
    }
    
    private fun observeTools() {
        toolViewModel.allTools.observe(viewLifecycleOwner) { tools ->
            if (tools.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                toolAdapter.submitList(tools)
            }
        }
    }
    
    private fun searchTools() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            toolViewModel.searchTools(query).observe(viewLifecycleOwner) { tools ->
                toolAdapter.submitList(tools)
                if (tools.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                }
            }
        } else {
            observeTools()
        }
    }
    
    private fun editTool(tool: Tool) {
        // Navigate to edit tool form
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ToolFormFragment.newInstance(tool))
            .addToBackStack(null)
            .commit()
    }
    
    private fun deleteTool(tool: Tool) {
        lifecycleScope.launch {
            try {
                toolViewModel.deleteTool(tool)
                Toast.makeText(
                    requireContext(),
                    "Alat berhasil dihapus",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal menghapus alat: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun showEmptyState() {
        binding.rvTools.visibility = View.GONE
        binding.llEmptyState.visibility = View.VISIBLE
    }
    
    private fun hideEmptyState() {
        binding.rvTools.visibility = View.VISIBLE
        binding.llEmptyState.visibility = View.GONE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        fun newInstance() = ToolListFragment()
    }
}