package com.vojtasii.job

sealed class ScheduledJobStatus {
    data object Finished : ScheduledJobStatus()
    data object Failed : ScheduledJobStatus()
    data object Cancelled : ScheduledJobStatus()
}
