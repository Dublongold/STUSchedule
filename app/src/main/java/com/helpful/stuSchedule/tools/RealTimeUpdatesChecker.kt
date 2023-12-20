package com.helpful.stuSchedule.tools

import android.util.Log
import com.helpful.stuSchedule.data.models.Lesson
import com.helpful.stuSchedule.data.models.WordTime
import com.helpful.stuSchedule.tools.objects.ScheduleItemPassChecker
import com.helpful.stuSchedule.tools.objects.TimeConverter

class RealTimeUpdatesChecker {

    suspend fun check(
        wordTime: WordTime,
        selectedDate: String,
        lessons: List<Lesson?>,
        callback: suspend (WordTime, Int, Int) -> Unit
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
                LOG_TAG,
                "${if (currentLesson != null) "currentLesson" else "lessonAfterBreak"}:" +
                        " ${currentLesson ?: lessonAfterBreak}. " +
                        "timeEnd: $timeOfScheduleItemEnd. " +
                        "timeNow: $timeNow"
            )
            callback(wordTime, timeOfScheduleItemEnd[0], if (currentLesson != null) {
                SCHEDULE_ITEM_TYPE_LESSON
            } else if (timeOfScheduleItemEnd[0] == TimeConverter.convertToMinutes(
                    lessons[0]?.periods?.firstOrNull()?.timeStart
            )) {
                SCHEDULE_ITEM_TYPE_BEFORE
            } else {
                SCHEDULE_ITEM_TYPE_BREAK
            }
            )
        } else {
            lessonsIsPass(lessons, selectedDate, wordTime, timeOfScheduleItemEnd, callback)
        }
    }

    private suspend fun lessonsIsPass(
        lessons: List<Lesson?>,
        selectedDate: String,
        wordTime: WordTime,
        timeOfScheduleItemEnd: IntArray,
        callback: suspend (WordTime, Int, Int) -> Unit
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
                callback(wordTime,timeOfScheduleItemEnd[0] + 15, SCHEDULE_ITEM_TYPE_NONE)
            }
            else {
                callback(wordTime, 0, SCHEDULE_ITEM_TYPE_NONE)
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

        const val SCHEDULE_ITEM_TYPE_NONE = 0
        const val SCHEDULE_ITEM_TYPE_LESSON = 1
        const val SCHEDULE_ITEM_TYPE_BREAK = 2
        const val SCHEDULE_ITEM_TYPE_BEFORE = 3
    }
}