package com.vojtasii.job

fun interface ScheduledAction {
    suspend fun execute(): ScheduledJobStatus
}
