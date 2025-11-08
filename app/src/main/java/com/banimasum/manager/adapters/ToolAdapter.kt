package com.banimasum.manager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.banimasum.manager.databinding.ItemToolBinding
import com.banimasum.manager.models.Tool

class ToolAdapter(
    private val onEditClick: (Tool) -> Unit,
    private val onDeleteClick: (Tool) -> Unit
) : ListAdapter<Tool, ToolAdapter.ToolViewHolder>(ToolDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val binding = ItemToolBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ToolViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ToolViewHolder(
        private val binding: ItemToolBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tool: Tool) {
            with(binding) {
                tvToolName.text = tool.toolName
                tvToolCode.text = "Kode: ${tool.toolCode}"
                tvToolCategory.text = "Kategori: ${tool.category}"
                tvToolStatus.text = "Status: ${tool.status}"
                
                // Set status color based on status
                val statusColor = when (tool.status) {
                    "Tersedia" -> android.R.color.holo_green_dark
                    "Dipinjam" -> android.R.color.holo_orange_dark
                    "Rusak" -> android.R.color.holo_red_dark
                    "Perawatan" -> android.R.color.holo_blue_dark
                    "Hilang" -> android.R.color.darker_gray
                    else -> android.R.color.black
                }
                tvToolStatus.setTextColor(root.context.getColor(statusColor))
                
                btnEdit.setOnClickListener {
                    onEditClick(tool)
                }
                
                btnDelete.setOnClickListener {
                    onDeleteClick(tool)
                }
            }
        }
    }

    class ToolDiffCallback : DiffUtil.ItemCallback<Tool>() {
        override fun areItemsTheSame(oldItem: Tool, newItem: Tool): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tool, newItem: Tool): Boolean {
            return oldItem == newItem
        }
    }
}