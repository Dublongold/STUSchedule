package com.helpful.stuSchedule.models.receivingData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Faculty (
    val id: Int,
    val shortName: String,
    val fullName: String,
) : Parcelable