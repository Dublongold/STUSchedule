package com.helpful.stuSchedule.models.receivingData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val course: Int
) : Parcelable