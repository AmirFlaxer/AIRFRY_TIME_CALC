package com.example.airfrycalc.domain.model

data class CookingSession(
    val steps: List<ScheduledStep>,
    val totalDurationMinutes: Int
)
