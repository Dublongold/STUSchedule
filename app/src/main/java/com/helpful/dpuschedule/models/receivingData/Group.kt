package com.helpful.dpuschedule.models.receivingData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group (
    val id: Int,
    val name: String,
    val course: Int,
    val priority: Int,
    val educationForm: Int,
) : Parcelable