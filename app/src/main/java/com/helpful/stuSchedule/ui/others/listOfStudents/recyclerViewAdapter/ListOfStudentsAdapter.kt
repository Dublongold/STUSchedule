package com.helpful.stuSchedule.ui.others.listOfStudents.recyclerViewAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.helpful.stuSchedule.R
import com.helpful.stuSchedule.data.models.other.Student

class ListOfStudentsAdapter(
    private val language: Int
): RecyclerView.Adapter<ListOfStudentsAdapter.ListOfStudentsViewHolder>() {
    private var items: MutableList<Student?> = mutableListOf()

    fun updateItems(items: List<Student?>) {
        this.items.clear()
        this.items.addAll(items)
        this.items.sortBy {
            it?.firstName
        }
        notifyItemRangeChanged(0, items.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListOfStudentsViewHolder {
        return ListOfStudentsViewHolder (
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_student,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ListOfStudentsViewHolder, position: Int) {
        val item = items[position]
        if (item != null) {
            holder.itemView.run {
                val studentNumber = position + 1
                findViewById<TextView>(R.id.studentNumber).text = studentNumber.toString()

                findViewById<TextView>(R.id.studentFullName).text =
                    item.getFullName(language)
            }
            Log.i(LOG_TAG, "Student #${position}: $item.")
        }
        else {
            Log.i(LOG_TAG, "Student #${position}: null.")
        }
    }
    class ListOfStudentsViewHolder(view: View) : ViewHolder(view)

    companion object {
        private const val LOG_TAG = "List of students RCV adapter"
    }
}