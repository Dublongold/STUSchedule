package com.helpful.stuSchedule.helpful

import android.util.Log
import com.helpful.stuSchedule.helpful.objects.ScheduleItemPassChecker
import com.helpful.stuSchedule.helpful.objects.TimeConverter
import com.helpful.stuSchedule.models.Lesson
import com.helpful.stuSchedule.models.WordTime
import com.helpful.stuSchedule.recyclerViews.ScheduleAdapter

class RealTimeUpdatesChecker {

    suspend fun check(
        adapter: ScheduleAdapter,
        wordTime: WordTime,
        selectedDate: String,
        lessons: List<Lesson?>,
        callback: suspend (ScheduleAdapter, WordTime, Int, Boolean?) -> Unit
    ) {
        val timeOfScheduleItemEnd = IntArray(1)
        val timeNow = TimeConverter.convertToMinutes(wordTime.time)
        val currentLesson: Lesson? = getCurrentLessonAndTime(
            lessons, timeNow, timeOfScheduleItemEnd
        )
        var lessonAfterBreak: Lesson? = null

        if (currentLesson == null) {
            lessonAfterBreak = getLessonAfterBreak(lessons, timeNow, timeOfScheduleItemEnd)
        }
        if (timeOfScheduleItemEnd[0] != 0) {
            Log.i(
                "Schedule VM",
                "${if (currentLesson != null) "currentLesson" else "lessonAfterBreak"}:" +
                        " ${currentLesson ?: lessonAfterBreak}. " +
                        "timeEnd: $timeOfScheduleItemEnd. " +
                        "timeNow: $timeNow"
            )
            callback(adapter, wordTime, timeOfScheduleItemEnd[0], currentLesson != null)
        } else {
            lessonsIsPass(adapter, lessons, selectedDate, wordTime, timeOfScheduleItemEnd, callback)
        }
    }

    private suspend fun lessonsIsPass(
        adapter: ScheduleAdapter,
        lessons: List<Lesson?>,
        selectedDate: String,
        wordTime: WordTime,
        timeOfScheduleItemEnd: IntArray,
        callback: suspend (ScheduleAdapter, WordTime, Int, Boolean?) -> Unit
    ) {
        val timeNow = TimeConverter.convertToMinutes(wordTime.time)
        if (selectedDate == wordTime.date) {

            val timeEnd = lessons.lastOrNull()?.periods?.firstOrNull()?.timeEnd
            timeOfScheduleItemEnd[0] =
                TimeConverter.convertToMinutes(timeEnd)

            Log.i(LOG_TAG, "[CASE 3] timeNow: $timeNow, timeEnd: $timeOfScheduleItemEnd")

            if (timeOfScheduleItemEnd[0] != 0
                && ScheduleItemPassChecker.lessonsIsPass(timeNow, timeOfScheduleItemEnd[0])
            ) {
                callback(adapter, wordTime,timeOfScheduleItemEnd[0] + 15, null)
            }
        } else {
            Log.i(
                LOG_TAG,
                "Different dates: " +
                        "mutableDate (${selectedDate}) to wordTime.date (${wordTime.date})"
            )
        }
    }

    private fun getCurrentLessonAndTime(
        lessons: List<Lesson?>,
        timeNow: Int,
        timeOfScheduleItemEnd: IntArray
    ): Lesson? {
        return lessons.firstOrNull {
            if (it != null) {
                val timeStart =
                    TimeConverter.convertToMinutes(it.periods.firstOrNull()?.timeStart)
                timeOfScheduleItemEnd[0] =
                    TimeConverter.convertToMinutes(it.periods.firstOrNull()?.timeEnd)
                if (timeNow in timeStart..<timeOfScheduleItemEnd[0]) {
                    true
                } else {
                    timeOfScheduleItemEnd[0] = 0
                    false
                }
            } else {
                false
            }
        }
    }

    private fun getLessonAfterBreak(
        lessons: List<Lesson?>,
        timeNow: Int,
        timeOfScheduleItemEnd: IntArray
    ): Lesson? {
        return lessons.firstOrNull {
            if (it != null) {
                timeOfScheduleItemEnd[0] =
                    TimeConverter.convertToMinutes(it.periods.firstOrNull()?.timeStart)
                if (timeOfScheduleItemEnd[0] > timeNow) {
                    true
                } else {
                    timeOfScheduleItemEnd[0] = 0
                    false
                }
            } else {
                false
            }
        }
    }
    companion object {
        private const val LOG_TAG = "Real time u...s checker"
    }
}