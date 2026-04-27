package com.example.airfrycalc.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airfrycalc.domain.model.Ingredient
import com.example.airfrycalc.domain.usecase.AddIngredientUseCase
import com.example.airfrycalc.domain.usecase.DeleteIngredientUseCase
import com.example.airfrycalc.domain.usecase.GetIngredientsUseCase
import com.example.airfrycalc.domain.usecase.UpdateIngredientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getIngredients: GetIngredientsUseCase,
    private val addIngredient: AddIngredientUseCase,
    private val updateIngredient: UpdateIngredientUseCase,
    private val deleteIngredient: DeleteIngredientUseCase
) : ViewModel() {

    data class UiState(
        val ingredients: List<Ingredient> = emptyList(),
        val dialogTarget: Ingredient? = null,  // null = הוספה, non-null = עריכה
        val showDialog: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getIngredients().collect { list ->
                _uiState.update { it.copy(ingredients = list) }
            }
        }
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showDialog = true, dialogTarget = null) }
    }

    fun showEditDialog(ingredient: Ingredient) {
        _uiState.update { it.copy(showDialog = true, dialogTarget = ingredient) }
    }

    fun hideDialog() {
        _uiState.update { it.copy(showDialog = false, dialogTarget = null) }
    }

    fun save(name: String, minutes: Int) {
        if (name.isBlank() || minutes < 1) return
        val target = _uiState.value.dialogTarget
        viewModelScope.launch {
            if (target == null) {
                addIngredient(Ingredient(name = name.trim(), cookTimeMinutes = minutes))
            } else {
                updateIngredient(target.copy(name = name.trim(), cookTimeMinutes = minutes))
            }
            hideDialog()
        }
    }

    fun delete(ingredient: Ingredient) {
        viewModelScope.launch { deleteIngredient(ingredient) }
    }
}
