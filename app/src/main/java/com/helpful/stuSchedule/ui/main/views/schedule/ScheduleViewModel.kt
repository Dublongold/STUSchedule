package com.helpful.stuSchedule.ui.main.views.schedule

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.helpful.stuSchedule.data.models.Lesson
import com.helpful.stuSchedule.data.models.WordTime
import com.helpful.stuSchedule.data.network.AsuClient
import com.helpful.stuSchedule.data.network.WordTimeClient
import com.helpful.stuSchedule.tools.CancellationTokenManager
import com.helpful.stuSchedule.tools.RealTimeUpdatesChecker
import com.helpful.stuSchedule.tools.objects.TimeConverter
import com.helpful.stuSchedule.ui.main.recyclerViews.ScheduleAdapter
import com.helpful.stuSchedule.views.ModifiedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScheduleViewModel : ModifiedViewModel() {

    private val mutableDate = MutableStateFlow("...")
    private val mutableDataLoadedState = MutableStateFlow(
        DATA_LOADED_STATE_LOADING
    )
    private val mutableGroupId = MutableStateFlow(-1)
    private val mutableIsLessonToTimeLeft: MutableStateFlow<Pair<Int, Int>> =
        MutableStateFlow(SCHEDULE_ITEM_TYPE_NONE to -1)
    val mutableCurrentDateFormat = MutableStateFlow("")
    val realTimeCancellationToken =
        MutableStateFlow(CancellationTokenManager()).asStateFlow()

    lateinit var adapter: ScheduleAdapter

    private var times = 0

    val dataLoadedState: StateFlow<Int>
        get() = mutableDataLoadedState

    val date: StateFlow<String>
        get() = mutableDate

    val isLessonToTimeLeft: StateFlow<Pair<Int, Int>>
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
//=========== For debug changes in real time =======================================================
//            .also {
//                if (times < 10) {
//                    times++
//                    val beforeTime = it.datetime.substring(0, it.datetime.indexOf('T') + 1)
//                    val time = when (times) {
//                        1 -> "8:19:55"
//                        2 -> "9:39:55"
//                        3 -> "9:54:55"
//                        4 -> "11:14:55"
//                        5 -> "11:29:55"
//                        6 -> "12:49:55"
//                        7 -> "13:09:55"
//                        8 -> "14:29:55"
//                        9 -> "14:44:55"
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
//=========== End of debug code ====================================================================
            adapter.setWordTime(wordTime)
        }

        for (request in listOf(request1, request2)) {
            request.join()
        }
        if (lessons.size > 1 && lessons[0] != null) {
            for ((additionalIndex, i) in (1..<lessons.size).withIndex()) {
                lessons.add(i + additionalIndex, null)
            }
        }
        adapter.loadLessons(lessons)
        mutableDataLoadedState.value = DATA_LOADED_STATE_LOADED
        if(mutableDate.value == wordTime.date) {
            viewModelScope.launch {
                RealTimeUpdatesChecker().check(
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
        wordTime: WordTime,
        timeEnd: Int,
        scheduleItemType: Int,
    ) {
        val cancellationToken = realTimeCancellationToken.value.getToken()
        var seconds = wordTime.time.substring(
            wordTime.time.indexOfLast { it == ':' } + 1,
            wordTime.time.length
        ).toLongOrNull()
        val timeLeft = timeEnd - TimeConverter.convertToMinutes(wordTime.time)

        if (timeLeft <= 80 && timeEnd > 0) {
            mutableIsLessonToTimeLeft.update {
                scheduleItemType to timeEnd - TimeConverter.convertToMinutes(wordTime.time)
            }
        }
        else {
            mutableIsLessonToTimeLeft.update {
                scheduleItemType to -1
            }
        }
        if (mutableIsLessonToTimeLeft.value.second == -1) {
            realTimeCancellationToken.value.finishAndChange()
            return
        }

        Log.i(
            LOG_TAG, "[updateSchedule] Start work.\n" +
                "Cancellation token: $cancellationToken (isCancelled == " +
                "${cancellationToken.isCancelled()});\n" +
                "Params: seconds: $seconds, " +
                "timeLeft: ${mutableIsLessonToTimeLeft.value.second}, timeEnd: $timeEnd.")
        while (mutableIsLessonToTimeLeft.value.second > 0 && !cancellationToken.isCancelled()) {
            delay(1000L * (60L - (seconds ?: 0)))
            if (!cancellationToken.isCancelled()) {
                mutableIsLessonToTimeLeft.update {
                    it.first to it.second - 1
                }
            }
            seconds = 0L
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
        if(cancellationToken == realTimeCancellationToken.value.getToken()
            && cancellationToken.isInProcess()) {
            realTimeCancellationToken.value.finishAndChange()
        }
        Log.i(
            LOG_TAG, "[updateSchedule] End work.\n" +
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

        const val SCHEDULE_ITEM_TYPE_NONE = RealTimeUpdatesChecker.SCHEDULE_ITEM_TYPE_NONE
        const val SCHEDULE_ITEM_TYPE_LESSON = RealTimeUpdatesChecker.SCHEDULE_ITEM_TYPE_LESSON
        @Suppress("unused")
        const val SCHEDULE_ITEM_TYPE_BREAK = RealTimeUpdatesChecker.SCHEDULE_ITEM_TYPE_BREAK
        const val SCHEDULE_ITEM_TYPE_BEFORE = RealTimeUpdatesChecker.SCHEDULE_ITEM_TYPE_BEFORE
    }
}