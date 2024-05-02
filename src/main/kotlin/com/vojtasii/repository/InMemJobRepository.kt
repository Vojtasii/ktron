package com.vojtasii.repository

import com.vojtasii.job.ScheduledJob

class InMemJobRepository : JobRepository {
    private val jobs = mutableMapOf<String, ScheduledJob>()

    override suspend fun set(job: ScheduledJob) {
        jobs[job.id] = job
    }

    override suspend fun remove(jobId: String) {
        jobs.remove(jobId)
    }

    override suspend fun getAll(): Collection<ScheduledJob> {
        return jobs.values
    }
}
