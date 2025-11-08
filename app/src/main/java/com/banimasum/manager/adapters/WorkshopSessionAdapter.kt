package com.banimasum.manager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.banimasum.manager.databinding.ItemWorkshopSessionBinding
import com.banimasum.manager.models.WorkshopSession
import com.banimasum.manager.models.SessionStatus
import java.text.SimpleDateFormat
import java.util.*

class WorkshopSessionAdapter(
    private val onEditClick: (WorkshopSession) -> Unit,
    private val onDeleteClick: (WorkshopSession) -> Unit,
    private val onStartClick: (WorkshopSession) -> Unit,
    private val onEndClick: (WorkshopSession) -> Unit
) : ListAdapter<WorkshopSession, WorkshopSessionAdapter.SessionViewHolder>(SessionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val binding = ItemWorkshopSessionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SessionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SessionViewHolder(
        private val binding: ItemWorkshopSessionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(session: WorkshopSession) {
            binding.apply {
                tvProjectName.text = session.projectName
                tvStudentInfo.text = "Siswa ID: ${session.studentId}"
                tvProjectInfo.text = "Proyek: ${session.projectName}"
                
                // Format date
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                tvDate.text = "Tanggal: ${dateFormat.format(session.sessionDate)}"
                
                // Format time
                val endTime = session.endTime ?: "Selesai"
                tvTime.text = "Waktu: ${session.startTime} - $endTime"
                
                tvInstructor.text = "Instruktur: ${session.instructorName}"
                
                // Tools used count
                val toolsCount = if (session.toolsUsed.isBlank()) 0 else session.toolsUsed.split(",").size
                tvToolsUsed.text = "Alat: $toolsCount alat digunakan"
                
                // Set status chip
                chipStatus.text = getStatusText(session.sessionStatus)
                chipStatus.chipBackgroundColor = getStatusColor(session.sessionStatus)
                
                // Show/hide action buttons based on status
                when (session.sessionStatus) {
                    SessionStatus.SCHEDULED -> {
                        btnStart.visibility = android.view.View.VISIBLE
                        btnEnd.visibility = android.view.View.GONE
                        btnEdit.visibility = android.view.View.VISIBLE
                        btnDelete.visibility = android.view.View.VISIBLE
                    }
                    SessionStatus.IN_PROGRESS -> {
                        btnStart.visibility = android.view.View.GONE
                        btnEnd.visibility = android.view.View.VISIBLE
                        btnEdit.visibility = android.view.View.GONE
                        btnDelete.visibility = android.view.View.GONE
                    }
                    SessionStatus.COMPLETED, SessionStatus.CANCELLED, SessionStatus.NO_SHOW -> {
                        btnStart.visibility = android.view.View.GONE
                        btnEnd.visibility = android.view.View.GONE
                        btnEdit.visibility = android.view.View.VISIBLE
                        btnDelete.visibility = android.view.View.VISIBLE
                    }
                }
                
                // Click listeners
                btnEdit.setOnClickListener { onEditClick(session) }
                btnDelete.setOnClickListener { onDeleteClick(session) }
                btnStart.setOnClickListener { onStartClick(session) }
                btnEnd.setOnClickListener { onEndClick(session) }
            }
        }
        
        private fun getStatusText(status: SessionStatus): String {
            return when (status) {
                SessionStatus.SCHEDULED -> "Dijadwalkan"
                SessionStatus.IN_PROGRESS -> "Berlangsung"
                SessionStatus.COMPLETED -> "Selesai"
                SessionStatus.CANCELLED -> "Dibatalkan"
                SessionStatus.NO_SHOW -> "Tidak Hadir"
            }
        }
        
        private fun getStatusColor(status: SessionStatus): android.content.res.ColorStateList {
            val colorRes = when (status) {
                SessionStatus.SCHEDULED -> android.R.color.holo_blue_light
                SessionStatus.IN_PROGRESS -> android.R.color.holo_orange_light
                SessionStatus.COMPLETED -> android.R.color.holo_green_light
                SessionStatus.CANCELLED -> android.R.color.holo_red_light
                SessionStatus.NO_SHOW -> android.R.color.darker_gray
            }
            return android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor(
                    when (colorRes) {
                        android.R.color.holo_blue_light -> "#33B5E5"
                        android.R.color.holo_orange_light -> "#FFBB33"
                        android.R.color.holo_green_light -> "#99CC00"
                        android.R.color.holo_red_light -> "#FF4444"
                        else -> "#666666"
                    }
                )
            )
        }
    }
    
    class SessionDiffCallback : DiffUtil.ItemCallback<WorkshopSession>() {
        override fun areItemsTheSame(oldItem: WorkshopSession, newItem: WorkshopSession): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: WorkshopSession, newItem: WorkshopSession): Boolean {
            return oldItem == newItem
        }
    }
}