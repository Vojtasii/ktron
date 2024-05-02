package com.vojtasii.repository

import com.vojtasii.job.ScheduledJob

interface JobRepository {
    suspend fun set(job: ScheduledJob)
    suspend fun remove(jobId: String)
    suspend fun getAll(): Collection<ScheduledJob>
}
