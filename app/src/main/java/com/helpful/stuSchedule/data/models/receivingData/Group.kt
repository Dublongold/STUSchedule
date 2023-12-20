package com.helpful.stuSchedule.data.models.receivingData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group (
    val id: Int,
    val name: String,
) : Parcelable