package com.banimasum.manager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.banimasum.manager.R
import com.banimasum.manager.viewmodels.DashboardViewModel
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    
    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        
        setupUI(view)
        observeViewModel()
        setupClickListeners(view)
    }

    private fun setupUI(view: View) {
        // Initialize UI components
    }

    private fun observeViewModel() {
        // Observe student count
        viewModel.studentCount.observe(viewLifecycleOwner) { count ->
            view?.findViewById<android.widget.TextView>(R.id.tv_student_count)?.text = count.toString()
        }

        // Observe tool count
        viewModel.toolCount.observe(viewLifecycleOwner) { count ->
            view?.findViewById<android.widget.TextView>(R.id.tv_tool_count)?.text = count.toString()
        }

        // Observe today's sessions
        viewModel.todaySessions.observe(viewLifecycleOwner) { sessions ->
            val sessionsText = if (sessions.isEmpty()) {
                "Tidak ada sesi untuk hari ini"
            } else {
                "${sessions.size} sesi hari ini"
            }
            view?.findViewById<android.widget.TextView>(R.id.tv_today_sessions)?.text = sessionsText
        }
    }

    private fun setupClickListeners(view: View) {
        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_add_student)?.setOnClickListener {
            // Navigate to add student
            navigateToAddStudent()
        }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_add_tool)?.setOnClickListener {
            // Navigate to add tool
            navigateToAddTool()
        }

        view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_new_session)?.setOnClickListener {
            // Navigate to create new session
            navigateToNewSession()
        }
    }

    private fun navigateToAddStudent() {
        // TODO: Implement navigation to add student fragment
    }

    private fun navigateToAddTool() {
        // TODO: Implement navigation to add tool fragment
    }

    private fun navigateToNewSession() {
        // TODO: Implement navigation to new session fragment
    }

    companion object {
        fun newInstance() = DashboardFragment()
    }
}