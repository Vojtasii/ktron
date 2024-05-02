package com.vojtasii.scheduler

import com.vojtasii.job.ScheduledJobStatus
import com.vojtasii.job.ScheduledJob
import com.vojtasii.repository.JobRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration

class Scheduler(
    private val repository: JobRepository,
    private val clock: Clock = Clock.System,
) {

    private val scheduleScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val jobs = mutableMapOf<String, Job>()
    private val channel = Channel<ScheduledJobStatus>()
    private var isStarted: Boolean = false

    suspend fun start(onReceive: suspend (ScheduledJobStatus) -> Unit) {
        stop()
        isStarted = true
        repository.getAll().forEach { job ->
            jobs[job.id] = newJob(job)
        }
        scheduleScope.launch {
            while (true) {
                val result = channel.receiveCatching()
                if (result.isSuccess) {
                    onReceive(result.getOrThrow())
                } else if (result.exceptionOrNull() is CancellationException) {
                    onReceive(ScheduledJobStatus.Cancelled)
                } else {
                    onReceive(ScheduledJobStatus.Failed)
                }
            }
        }
    }

    fun stop() {
        scheduleScope.coroutineContext.cancelChildren(
            CancellationException("Stopping scheduler"),
        )
        isStarted = false
    }

    suspend fun set(job: ScheduledJob) {
        repository.set(job)
        if (isStarted) {
            jobs[job.id]?.cancel()
            jobs[job.id] = newJob(job)
        }
    }

    suspend fun remove(jobId: String) {
        repository.remove(jobId)
        jobs[jobId]?.cancel()
    }

    private fun newJob(job: ScheduledJob): Job {
        return scheduleScope.launch {
            val wait = job.schedule.start - clock.now()
            if (wait > Duration.ZERO) {
                println("jobId=${job.id} waiting $wait")
                delay(wait)
            }
            while (true) {
                channel.send(job.action.execute())
                delay(job.schedule.interval)
            }
        }
    }
}
