package com.example.airfrycalc.presentation.session

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.airfrycalc.presentation.theme.AirFryYellow
import com.example.airfrycalc.presentation.theme.StepComplete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    viewModel: SessionViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val session = uiState.session

    BackHandler {
        viewModel.pauseTimer()
        onBack()
    }

    if (session == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("שגיאה: לא נבחרו מרכיבים", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val totalSeconds = session.totalDurationMinutes * 60
    val remainingSeconds = maxOf(0, totalSeconds - uiState.elapsedSeconds)
    val currentStep = session.steps[uiState.currentStepIndex]
    val nextStep = session.steps.getOrNull(uiState.currentStepIndex + 1)
    val stepDurationMinutes = (nextStep?.addAtMinute ?: session.totalDurationMinutes) - currentStep.addAtMinute
    val countdownSeconds = if (nextStep != null) uiState.secondsUntilNextStep else remainingSeconds
    val cMin = countdownSeconds / 60
    val cSec = countdownSeconds % 60

    val alertBg by animateColorAsState(
        targetValue = if (uiState.stepAlertVisible) AirFryYellow.copy(alpha = 0.25f)
        else Color.Transparent,
        animationSpec = tween(400),
        label = "alertBg"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("בישול פעיל") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.pauseTimer()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "חזור")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(alertBg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isFinished) {
                FinishedCard(onBack = onBack)
            } else {
                // Instruction card
                val contentColor = if (uiState.stepAlertVisible) Color.Black
                    else MaterialTheme.colorScheme.onPrimaryContainer
                val actionLabel = if (uiState.elapsedSeconds == 0) "הכנס עכשיו לאייר פריי:"
                    else "מתבשל:"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.stepAlertVisible) AirFryYellow
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = actionLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = contentColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentStep.ingredient.name,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ל-$stepDurationMinutes דקות",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor.copy(alpha = 0.75f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Countdown to next event
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "%02d:%02d".format(cMin, cSec),
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (countdownSeconds <= 30) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = if (nextStep != null) "עד להוספת ${nextStep.ingredient.name}"
                            else "עד לסיום",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        val eMin = uiState.elapsedSeconds / 60
                        val eSec = uiState.elapsedSeconds % 60
                        Text(
                            text = "זמן כולל: %02d:%02d".format(eMin, eSec),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.45f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(8.dp))

            // Steps timeline
            Text(
                text = "לוח זמנים",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))

            session.steps.forEachIndexed { index, step ->
                val isDone = index < uiState.currentStepIndex || uiState.isFinished
                val isCurrent = index == uiState.currentStepIndex && !uiState.isFinished
                StepRow(
                    timeLabel = "%02d:%02d".format(step.addAtMinute, 0),
                    label = step.ingredient.name,
                    isDone = isDone,
                    isCurrent = isCurrent
                )
            }
            StepRow(
                timeLabel = "%02d:00".format(session.totalDurationMinutes),
                label = "סיום — האוכל מוכן!",
                isDone = uiState.isFinished,
                isCurrent = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Control buttons
            if (!uiState.isFinished) {
                if (uiState.waitingForUser) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AirFryYellow)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "הוסף עכשיו לאייר פריי:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = currentStep.ingredient.name,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "ל-$stepDurationMinutes דקות",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black.copy(alpha = 0.75f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { viewModel.confirmStep() },
                                modifier = Modifier.fillMaxWidth().height(64.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1A1A1A),
                                    contentColor = AirFryYellow
                                )
                            ) {
                                Text("הוספתי — המשך", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (uiState.isRunning) viewModel.pauseTimer()
                                else viewModel.startTimer()
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = when {
                                    uiState.isRunning -> "השהה"
                                    uiState.elapsedSeconds == 0 -> "התחל"
                                    else -> "המשך"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = { viewModel.resetTimer() },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("אפס", fontSize = 18.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StepRow(
    timeLabel: String,
    label: String,
    isDone: Boolean,
    isCurrent: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = when {
                isDone -> "✓"
                isCurrent -> "▶"
                else -> "○"
            },
            fontSize = 20.sp,
            color = when {
                isDone -> StepComplete
                isCurrent -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            }
        )
        Text(
            text = timeLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isDone -> StepComplete
                isCurrent -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            },
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.width(52.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isDone -> StepComplete
                isCurrent -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            }
        )
    }
}

@Composable
private fun FinishedCard(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B4332))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "האוכל מוכן! 🎉",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = StepComplete
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "תהנה מהארוחה!",
                    fontSize = 22.sp,
                    color = StepComplete.copy(alpha = 0.8f)
                )
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = StepComplete,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "חזור לתפריט הראשי",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
