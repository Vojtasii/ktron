package com.vojtasii.utils

import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class ManualClock(
    private var time: Instant = Instant.fromEpochMilliseconds(0),
    private val step: Duration = 50.milliseconds,
) : Clock {

    override fun now(): Instant {
        return time
    }

    suspend fun delayBy(duration: Duration) {
        val goal = time + duration
        while (time < goal) {
            time += step
            delay(step)
        }
    }
}
