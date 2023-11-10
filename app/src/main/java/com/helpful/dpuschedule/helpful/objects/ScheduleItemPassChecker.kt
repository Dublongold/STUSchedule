package com.helpful.dpuschedule.helpful.objects

import com.helpful.dpuschedule.models.WordTime

object ScheduleItemPassChecker {
    fun lessonIsPass(wordTime: WordTime, timeEnd: Int, selectedDate: String?): Boolean {
        val timeNow = TimeConverter.convertToMinutes(wordTime.time)
        return selectedDate?.let {
            it != wordTime.date || it == wordTime.date && timeNow >= timeEnd
        } ?: false
    }

    fun breakIsPass(wordTime: WordTime, timeEnd: Int, selectedDate: String?): Boolean {
        val timeNow = TimeConverter.convertToMinutes(wordTime.time)
        return selectedDate?.let {
            it != wordTime.date || it == wordTime.date && timeNow >= timeEnd
        } ?: false
    }

    fun lessonsIsPass(timeNow: Int, timeEnd: Int): Boolean {
        return timeNow - timeEnd < 15
    }
}