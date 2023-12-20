package com.helpful.stuSchedule.data.models

data class Period (
    val disciplineShortName: String,
    val disciplineFullName: String,
    val classroom: String,
    val timeStart: String,
    val timeEnd: String,
    val teachersNameFull: String,
    val type: Int
)