package com.helpful.stuSchedule.data.models.other

import com.helpful.stuSchedule.settings.SettingsOfUser

data class Student (
    val id: Long,
    val firstName: String,
    val secondName: String,
    val lastName: String,
) : Comparable<Student> {
    override fun compareTo(other: Student): Int {
        return compareValuesBy (
            this, other,
            { it.lastName },
            { it.firstName },
            { it.secondName },
            { it.id }
        )
    }
    fun getFullName(language: Int = SettingsOfUser.SELECTED_LANGUAGE_ENGLISH): String {
        return if (language == SettingsOfUser.SELECTED_LANGUAGE_ENGLISH) {
            "$firstName ${if (secondName.isEmpty()) "" else "$secondName "}$lastName"
        }
        else {
            "$lastName $firstName $secondName"
        }
    }
}