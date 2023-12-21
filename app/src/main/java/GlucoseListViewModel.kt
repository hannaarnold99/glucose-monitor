package edu.arizona.cast.hannaarnold.glucosemonitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class GlucoseListViewModel : ViewModel() {
    private val glucoseRepository = GlucoseRepository.get()
    val _glucoses: MutableStateFlow<List<Glucose>> = MutableStateFlow(emptyList())
    val glucoses: StateFlow<List<Glucose>>
        get() = _glucoses.asStateFlow()

    init {
        viewModelScope.launch {
            glucoseRepository.getGlucoses().collect {
                _glucoses.value = it
            }

        }
    }
    suspend fun addGlucose(glucose: Glucose) {
        glucoseRepository.addGlucose(glucose)
    }

}

