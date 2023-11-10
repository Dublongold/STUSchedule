package com.helpful.stuSchedule.models

data class Period (
    val disciplineShortName: String,
    val classroom: String,
    val timeStart: String,
    val timeEnd: String,
    val teachersNameFull: String,
    val type: Int
)