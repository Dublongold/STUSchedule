package com.helpful.dpuschedule.models.receivingData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Course(
    val course: Int
) : Parcelable