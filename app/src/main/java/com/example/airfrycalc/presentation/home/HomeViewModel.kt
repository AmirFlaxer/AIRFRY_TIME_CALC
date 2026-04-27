package com.example.airfrycalc.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airfrycalc.domain.model.Ingredient
import com.example.airfrycalc.domain.usecase.CalculateSessionUseCase
import com.example.airfrycalc.domain.usecase.GetIngredientsUseCase
import com.example.airfrycalc.presentation.session.CurrentSessionHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getIngredients: GetIngredientsUseCase,
    private val calculateSession: CalculateSessionUseCase,
    private val sessionHolder: CurrentSessionHolder
) : ViewModel() {

    data class UiState(
        val allIngredients: List<Ingredient> = emptyList(),
        val selectedIds: Set<Long> = emptySet()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getIngredients().collect { list ->
                _uiState.update { it.copy(allIngredients = list) }
            }
        }
    }

    fun toggleIngredient(id: Long) {
        _uiState.update { state ->
            val updated = if (id in state.selectedIds) state.selectedIds - id
            else state.selectedIds + id
            state.copy(selectedIds = updated)
        }
    }

    fun startSession(): Boolean {
        val selected = _uiState.value.allIngredients
            .filter { it.id in _uiState.value.selectedIds }
        if (selected.isEmpty()) return false
        sessionHolder.session = calculateSession(selected)
        return true
    }
}
