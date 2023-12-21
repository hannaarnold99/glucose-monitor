package edu.arizona.cast.hannaarnold.glucosemonitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class GlucoseViewModel(glucoseId: Date) : ViewModel() {
    private val glucoseRepository = GlucoseRepository.get()

    private val _glucose: MutableStateFlow<Glucose?> = MutableStateFlow(null)
    val glucose: StateFlow<Glucose?> = _glucose.asStateFlow()

    init {
        viewModelScope.launch {
            _glucose.value = glucoseRepository.getGlucose(glucoseId)
        }
    }

    fun updateGlucose(onUpdate: (Glucose) -> Glucose) {
        _glucose.update { oldGlucose ->
            oldGlucose?.let { onUpdate(it) }
        }
    }
    override fun onCleared() {
        super.onCleared()
            glucose.value?.let { glucoseRepository.updateGlucose(it) }
    }
}


class GlucoseViewModelFactory(
    private val glucoseId: Date
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GlucoseViewModel(glucoseId) as T
    }
}