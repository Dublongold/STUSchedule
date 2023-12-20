package com.helpful.stuSchedule.ui.others.bellSchedule.recyclerViewAdapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.data.models.other.BellScheduleItem
import com.helpful.stuSchedule.tools.objects.TimeConverter

class BellScheduleAdapter: RecyclerView.Adapter<BellScheduleAdapter.BellScheduleViewHolder>() {
    private var items: MutableList<BellScheduleItem?> = mutableListOf()

    fun updateItems(items: List<BellScheduleItem?>) {
        this.items.clear()
        this.items.addAll(items)
        notifyItemRangeChanged(0, items.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BellScheduleViewHolder {
        val layoutId = if (viewType == 0) {
            R.layout.item_bell_schedule_break
        } else {
            R.layout.item_bell_schedule_item
        }
        return BellScheduleViewHolder (
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        )
    }
    override fun getItemViewType(position: Int): Int {
        return if (items[position] == null) 0 else 1
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BellScheduleViewHolder, position: Int) {
        val item = items[position]
        if (item != null) {
            holder.itemView.run {
                findViewById<TextView>(R.id.bellScheduleItemNumber).text = item.number.toString()

                findViewById<TextView>(R.id.bellScheduleItemStartTime).text =
                    resources.getString(R.string.bell_schedule_item_start_time, item.timeStart)

                findViewById<TextView>(R.id.bellScheduleItemEndTime).text =
                    resources.getString(R.string.bell_schedule_item_end_time, item.timeEnd)
            }
            Log.i(LOG_TAG, "Bell schedule item #${position}: $item.")
        }
        else {
            if (position > 0 && (position + 1) <= items.lastIndex) {
                val itemBefore = items[position - 1]
                val itemAfter = items[position + 1]
                if (itemBefore != null && itemAfter != null) {
                    val endTimeOfItemBefore = TimeConverter.convertToMinutes(itemBefore.timeEnd)
                    val startTimeOfItemAfter = TimeConverter.convertToMinutes(itemAfter.timeStart)
                    val breakTime = startTimeOfItemAfter - endTimeOfItemBefore
                    holder.itemView.run {
                        @SuppressLint("SetTextI18n")
                        findViewById<TextView>(R.id.bellScheduleItemNumber).text =
                            "${itemBefore.number}/${itemAfter.number}"
                        findViewById<TextView>(R.id.studentFullName).text =
                            resources.getString(R.string.bell_schedule_item_break, breakTime)
                    }
                }
            }
            Log.i(LOG_TAG, "Bell schedule item #${position}: null.")
        }
    }
    class BellScheduleViewHolder(view: View) : ViewHolder(view)

    companion object {
        private const val LOG_TAG = "Bell schedule RCV adapter"
    }
}