package com.helpful.stuSchedule.data.models.receivingData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Structure (
    val id: Int,
    val shortName: String,
    val fullName: String,
) : Parcelable