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
import com.banimasum.manager.adapters.WorkshopSessionAdapter
import com.banimasum.manager.databinding.FragmentWorkshopSessionListBinding
import com.banimasum.manager.models.WorkshopSession
import com.banimasum.manager.viewmodels.WorkshopSessionViewModel
import kotlinx.coroutines.launch

class WorkshopSessionListFragment : Fragment() {
    
    private var _binding: FragmentWorkshopSessionListBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var sessionViewModel: WorkshopSessionViewModel
    private lateinit var sessionAdapter: WorkshopSessionAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkshopSessionListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        setupChipGroup()
        observeSessions()
    }
    
    private fun setupViewModel() {
        sessionViewModel = ViewModelProvider(this)[WorkshopSessionViewModel::class.java]
    }
    
    private fun setupRecyclerView() {
        sessionAdapter = WorkshopSessionAdapter(
            onEditClick = { session -> editSession(session) },
            onDeleteClick = { session -> deleteSession(session) },
            onStartClick = { session -> startSession(session) },
            onEndClick = { session -> endSession(session) }
        )
        
        binding.rvSessions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = sessionAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnAddSession.setOnClickListener {
            // Navigate to add session form
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WorkshopSessionFormFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        
        binding.btnSearch.setOnClickListener {
            searchSessions()
        }
    }
    
    private fun setupChipGroup() {
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipAll -> observeSessions()
                R.id.chipScheduled -> observeScheduledSessions()
                R.id.chipActive -> observeActiveSessions()
                R.id.chipCompleted -> observeCompletedSessions()
            }
        }
    }
    
    private fun observeSessions() {
        sessionViewModel.allSessions.observe(viewLifecycleOwner) { sessions ->
            if (sessions.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                sessionAdapter.submitList(sessions)
            }
        }
    }
    
    private fun observeScheduledSessions() {
        sessionViewModel.scheduledSessions.observe(viewLifecycleOwner) { sessions ->
            if (sessions.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                sessionAdapter.submitList(sessions)
            }
        }
    }
    
    private fun observeActiveSessions() {
        sessionViewModel.activeSessions.observe(viewLifecycleOwner) { sessions ->
            if (sessions.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                sessionAdapter.submitList(sessions)
            }
        }
    }
    
    private fun observeCompletedSessions() {
        sessionViewModel.completedSessions.observe(viewLifecycleOwner) { sessions ->
            if (sessions.isEmpty()) {
                showEmptyState()
            } else {
                hideEmptyState()
                sessionAdapter.submitList(sessions)
            }
        }
    }
    
    private fun searchSessions() {
        val query = binding.etSearch.text.toString().trim()
        if (query.isNotEmpty()) {
            sessionViewModel.searchSessions(query).observe(viewLifecycleOwner) { sessions ->
                sessionAdapter.submitList(sessions)
                if (sessions.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                }
            }
        } else {
            observeSessions()
        }
    }
    
    private fun editSession(session: WorkshopSession) {
        // Navigate to edit session form
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, WorkshopSessionFormFragment.newInstance(session))
            .addToBackStack(null)
            .commit()
    }
    
    private fun deleteSession(session: WorkshopSession) {
        lifecycleScope.launch {
            try {
                sessionViewModel.deleteSession(session)
                Toast.makeText(
                    requireContext(),
                    "Sesi workshop berhasil dihapus",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal menghapus sesi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun startSession(session: WorkshopSession) {
        lifecycleScope.launch {
            try {
                sessionViewModel.startSession(session.id ?: 0)
                Toast.makeText(
                    requireContext(),
                    "Sesi workshop dimulai",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal memulai sesi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun endSession(session: WorkshopSession) {
        // Get current time as end time
        val endTime = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())
        
        lifecycleScope.launch {
            try {
                sessionViewModel.endSession(session.id ?: 0, endTime)
                Toast.makeText(
                    requireContext(),
                    "Sesi workshop selesai",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Gagal mengakhiri sesi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun showEmptyState() {
        binding.rvSessions.visibility = View.GONE
        binding.llEmptyState.visibility = View.VISIBLE
    }
    
    private fun hideEmptyState() {
        binding.rvSessions.visibility = View.VISIBLE
        binding.llEmptyState.visibility = View.GONE
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        fun newInstance() = WorkshopSessionListFragment()
    }
}