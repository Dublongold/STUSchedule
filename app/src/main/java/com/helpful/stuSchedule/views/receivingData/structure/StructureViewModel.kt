package com.helpful.stuSchedule.views.receivingData.structure

import androidx.lifecycle.ViewModel
import com.helpful.stuSchedule.models.receivingData.Structure
import com.helpful.stuSchedule.network.AsuClient
import kotlinx.coroutines.flow.MutableStateFlow

class StructureViewModel: ViewModel() {
    private val client = AsuClient()

    private val mutableSelectedStructure =
        MutableStateFlow<Structure?>(null)
    private val structures =
        MutableStateFlow<List<Structure>?>(null)

    val selectedStructure
        get() = mutableSelectedStructure.value!!

    suspend fun getStructures(): List<Structure> {
        return client.getStructures().also {
            structures.value = it
        }
    }

    fun selectStructure(position: Int) {
        structures.value?.let {
            mutableSelectedStructure.value = it[position]
        }
    }
}