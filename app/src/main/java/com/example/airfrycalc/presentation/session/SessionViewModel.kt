package com.example.airfrycalc.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.airfrycalc.domain.model.CookingSession
import com.example.airfrycalc.notification.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionHolder: CurrentSessionHolder,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    data class UiState(
        val session: CookingSession? = null,
        val currentStepIndex: Int = 0,
        val elapsedSeconds: Int = 0,
        val isRunning: Boolean = false,
        val isFinished: Boolean = false,
        val secondsUntilNextStep: Int = 0,
        val stepAlertVisible: Boolean = false,
        val waitingForUser: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        val session = sessionHolder.session
        if (session != null) {
            _uiState.update { it.copy(session = session) }
            computeCountdown()
        }
    }

    fun confirmStep() {
        _uiState.update { it.copy(waitingForUser = false, stepAlertVisible = false) }
        startTimer()
    }

    fun startTimer() {
        if (_uiState.value.isRunning || _uiState.value.isFinished || _uiState.value.waitingForUser) return
        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.isRunning && !_uiState.value.isFinished) {
                delay(1_000L)
                tick()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                elapsedSeconds = 0,
                currentStepIndex = 0,
                isRunning = false,
                isFinished = false,
                stepAlertVisible = false,
                waitingForUser = false
            )
        }
        computeCountdown()
    }

    private fun tick() {
        val state = _uiState.value
        val session = state.session ?: return
        val newElapsed = state.elapsedSeconds + 1
        val elapsedMinutes = newElapsed / 60
        val prevMinutes = state.elapsedSeconds / 60

        val nextStep = session.steps.getOrNull(state.currentStepIndex + 1)
        val crossedStep = nextStep != null &&
            prevMinutes < nextStep.addAtMinute &&
            elapsedMinutes >= nextStep.addAtMinute

        val newStepIndex = if (crossedStep) state.currentStepIndex + 1 else state.currentStepIndex
        val totalSeconds = session.totalDurationMinutes * 60
        val isFinished = newElapsed >= totalSeconds

        _uiState.update {
            it.copy(
                elapsedSeconds = newElapsed,
                currentStepIndex = newStepIndex,
                isFinished = isFinished,
                isRunning = !isFinished
            )
        }
        computeCountdown()

        if (crossedStep && nextStep != null) {
            notificationHelper.sendAddIngredientAlert(nextStep.ingredient.name)
            timerJob?.cancel()
            _uiState.update { it.copy(isRunning = false, stepAlertVisible = true, waitingForUser = true) }
        }
        if (isFinished) {
            notificationHelper.sendDoneAlert()
        }
    }

    private fun computeCountdown() {
        val state = _uiState.value
        val session = state.session ?: return
        val nextStep = session.steps.getOrNull(state.currentStepIndex + 1)
        if (nextStep == null) {
            _uiState.update { it.copy(secondsUntilNextStep = 0) }
            return
        }
        val targetSeconds = nextStep.addAtMinute * 60
        val remaining = maxOf(0, targetSeconds - state.elapsedSeconds)
        _uiState.update { it.copy(secondsUntilNextStep = remaining) }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
