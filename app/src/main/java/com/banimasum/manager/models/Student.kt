package com.banimasum.manager.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val studentId: String,
    val fullName: String,
    val className: String,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) : Parcelable {
    override fun toString(): String = "$fullName ($studentId)"
}