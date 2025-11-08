package com.banimasum.manager.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "tools")
data class Tool(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val toolCode: String,
    val toolName: String,
    val category: String,
    val description: String? = null,
    val quantity: Int = 1,
    val status: String = "Tersedia",
    val purchaseDate: Date? = null,
    val lastMaintenanceDate: Date? = null,
    val location: String? = null,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable {
    override fun toString(): String = "$toolName ($toolCode)"
}

enum class ToolCondition {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR,
    BROKEN
}