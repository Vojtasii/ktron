package com.vojtasii.schedule

import kotlinx.datetime.Instant
import kotlin.time.Duration

data class Schedule(
    val start: Instant,
    val interval: Duration,
)
