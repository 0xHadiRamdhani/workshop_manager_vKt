package com.banimasum.manager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.banimasum.manager.databinding.ItemToolSelectionBinding
import com.banimasum.manager.models.Tool

class ToolSelectionAdapter(
    private val onToolSelected: (Long, Boolean) -> Unit
) : ListAdapter<Tool, ToolSelectionAdapter.ToolSelectionViewHolder>(ToolSelectionDiffCallback()) {

    private val selectedTools = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolSelectionViewHolder {
        val binding = ItemToolSelectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ToolSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToolSelectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setSelectedTools(selectedToolIds: Set<Long>) {
        selectedTools.clear()
        selectedTools.addAll(selectedToolIds)
        notifyDataSetChanged()
    }

    inner class ToolSelectionViewHolder(
        private val binding: ItemToolSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tool: Tool) {
            with(binding) {
                tvToolName.text = tool.toolName
                tvToolCode.text = tool.toolCode
                tvToolCategory.text = tool.category
                
                // Set checkbox state
                checkbox.isChecked = selectedTools.contains(tool.id)
                
                // Handle checkbox changes
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        selectedTools.add(tool.id)
                    } else {
                        selectedTools.remove(tool.id)
                    }
                    onToolSelected(tool.id, isChecked)
                }
                
                // Handle item click
                root.setOnClickListener {
                    checkbox.isChecked = !checkbox.isChecked
                }
            }
        }
    }
    
    class ToolSelectionDiffCallback : DiffUtil.ItemCallback<Tool>() {
        override fun areItemsTheSame(oldItem: Tool, newItem: Tool): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Tool, newItem: Tool): Boolean {
            return oldItem == newItem
        }
    }
}