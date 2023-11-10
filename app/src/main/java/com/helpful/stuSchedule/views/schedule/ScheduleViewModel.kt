package com.helpful.stuSchedule.views.schedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.helpful.stuSchedule.helpful.CancellationTokenManager
import com.helpful.stuSchedule.helpful.RealTimeUpdatesChecker
import com.helpful.stuSchedule.helpful.objects.TimeConverter
import com.helpful.stuSchedule.models.Lesson
import com.helpful.stuSchedule.models.WordTime
import com.helpful.stuSchedule.network.AsuClient
import com.helpful.stuSchedule.network.WordTimeClient
import com.helpful.stuSchedule.recyclerViews.ScheduleAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScheduleViewModel : ViewModel() {

    private val mutableDate = MutableStateFlow("...")
    private val mutableDataLoadedState = MutableStateFlow(
        DATA_LOADED_STATE_LOADING
    )
    private val mutableGroupId = MutableStateFlow(-1)
    private val mutableIsLessonToTimeLeft =
        MutableStateFlow(true to -1)
    val realTimeCancellationToken =
        MutableStateFlow(CancellationTokenManager()).asStateFlow()

    private var times = 0

    val dataLoadedState: StateFlow<Int>
        get() = mutableDataLoadedState

    val date: StateFlow<String>
        get() = mutableDate

    val isLessonToTimeLeft: StateFlow<Pair<Boolean, Int>>
        get() = mutableIsLessonToTimeLeft

    private val groupId: StateFlow<Int>
        get() = mutableGroupId

    fun setGroupId(groupId: Int) {
        if (groupId >= 0) {
            this.mutableGroupId.value = groupId
        }
        else {
            Log.w(LOG_TAG, "[setGroupId] groupId is less than 0: $groupId.")
        }
    }

    fun setDataStateAsUpdates() {
        mutableDataLoadedState.value = DATA_LOADED_STATE_UPDATES
    }

    suspend fun setSchedule(
        adapter: ScheduleAdapter,
        weekday: Int,
        selectedDate: String?
    ): List<Lesson?> {
        val asuClient = AsuClient()
        val wordTimeClient = WordTimeClient()

        mutableDate.value = TimeConverter.takeDateOrReturnSelectedDate(weekday, selectedDate)

        val lessons: MutableList<Lesson?> = mutableListOf()
        var wordTime: WordTime = WordTime.DEFAULT

        val request1 = viewModelScope.launch {
            lessons.addAll(asuClient.getData(mutableDate.value, groupId.value))
        }
        val request2 = viewModelScope.launch {
            wordTime = wordTimeClient.getKyivTime()
            adapter.setWordTime(wordTime)
//            For debug changes in real time
//            .also {
//                if (times < 4) {
//                    times++
//                    val beforeTime = it.datetime.substring(0, it.datetime.indexOf('T') + 1)
//                    val time = when (times) {
//                        1 -> "12:49:55"
//                        2 -> "13:09:55"
//                        3 -> "14:29:55"
//                        4 -> "14:44:55"
//                        else -> it.datetime.substring(it.datetime.indexOf('T') + 1,
//                            it.datetime.indexOf('.'))
//                    }
//                    val afterTime = it.datetime.substring(
//                        it.datetime.indexOf('.'),
//                        it.datetime.length
//                    )
//                    it.datetime = beforeTime + time + afterTime
//                }
//            }
//            End of debug code
        }

        for (request in listOf(request1, request2)) {
            request.join()
        }
        if (lessons.size > 1 && lessons[0] != null) {
            for ((additionalIndex, i) in (1..<lessons.size).withIndex()) {
                lessons.add(i + additionalIndex, null)
            }
            adapter.loadLessons(lessons)
        }
        mutableDataLoadedState.value = DATA_LOADED_STATE_LOADED
        if(mutableDate.value == wordTime.date) {
            viewModelScope.launch {
                RealTimeUpdatesChecker().check(
                    adapter,
                    wordTime,
                    mutableDate.value,
                    lessons,
                    ::updateSchedule
                )
            }
        }
        return lessons
    }

    private suspend fun updateSchedule(
        adapter: ScheduleAdapter,
        wordTime: WordTime,
        timeEnd: Int,
        isLesson: Boolean,
        needCalculateEndTime: Boolean
    ) {
        val cancellationToken = realTimeCancellationToken.value.getToken()
        var seconds = wordTime.time.substring(
            wordTime.time.indexOfLast { it == ':' } + 1,
            wordTime.time.length
        ).toLongOrNull()

        if(needCalculateEndTime) {
            mutableIsLessonToTimeLeft.value = false to -1
        }

        mutableIsLessonToTimeLeft.update {
            isLesson to timeEnd - TimeConverter.convertToMinutes(wordTime.time)
        }
        Log.i(LOG_TAG, "[updateSchedule] Start work.\n" +
                "Cancellation token: $cancellationToken (isCancelled == " +
                "${cancellationToken.isCancelled()});\n" +
                "Params: seconds: $seconds, " +
                "timeLeft: ${mutableIsLessonToTimeLeft.value.second}, timeEnd: $timeEnd.")
        if (seconds != null && mutableIsLessonToTimeLeft.value.second <= 80) {
            while (mutableIsLessonToTimeLeft.value.second != 0 && !cancellationToken.isCancelled()) {
                delay(1000L * (60L - (seconds ?: 0)))
                if (needCalculateEndTime && !cancellationToken.isCancelled()) {
                    mutableIsLessonToTimeLeft.update {
                        it.first to it.second - 1
                    }
                }
                seconds = 0L
            }
        }
        if (mutableIsLessonToTimeLeft.value.second == 0 && !cancellationToken.isCancelled()) {
            mutableDataLoadedState.value = DATA_LOADED_STATE_UPDATES
            viewModelScope.launch {
                setSchedule(
                    adapter,
                    if (wordTime.dayOfWeek < 7) wordTime.dayOfWeek + 1 else 1,
                    mutableDate.value
                )
            }
        }
        if(cancellationToken == realTimeCancellationToken.value.getToken()) {
            realTimeCancellationToken.value.cancelAndChange()
        }
        else {
            cancellationToken.cancel()
            Log.i(LOG_TAG, "Cancellation token changed.")
        }
        Log.i(LOG_TAG, "[updateSchedule] End work.\n" +
                "Cancellation token: $cancellationToken (isCancelled == " +
                "${cancellationToken.isCancelled()});\n" +
                "Params: seconds: $seconds, " +
                "timeLeft: ${mutableIsLessonToTimeLeft.value.second}, timeEnd: $timeEnd.")
    }

    companion object {
        private const val LOG_TAG = "Schedule VM"

        private const val DATA_LOADED_STATE_LOADING = 0
        private const val DATA_LOADED_STATE_LOADED = 1
        private const val DATA_LOADED_STATE_UPDATES = 2
    }
}