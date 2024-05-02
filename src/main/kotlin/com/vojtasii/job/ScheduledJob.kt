package com.vojtasii.job

import com.vojtasii.schedule.Schedule

data class ScheduledJob(
    val id: String,
    val schedule: Schedule,
    val action: ScheduledAction,
)
