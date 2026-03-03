package com.example.campuskit.ui.academic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuskit.domain.academic.model.Resource
import com.example.campuskit.domain.academic.repository.AcademicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class AcademicDetailViewModel @Inject constructor(
    private val academicRepository: AcademicRepository,
) : ViewModel() {

    private val _subjectCode = MutableStateFlow("")

    val resources: StateFlow<List<Resource>> = _subjectCode.flatMapLatest { code ->
        if (code.isBlank()) flowOf(emptyList())
        else academicRepository.getResources(code)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList(),
    )

    fun loadResources(subjectCode: String) {
        _subjectCode.value = subjectCode
    }
}
