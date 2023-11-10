package com.helpful.dpuschedule.models.receivingData

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
class StudentDataContainer(
    val structure: Structure?,
    val faculty: Faculty?,
    val course: Course?,
    val group: Group?,
) : Parcelable {
    companion object {
        const val BUNDLE_KEY = "studentDataContainer"
    }
}