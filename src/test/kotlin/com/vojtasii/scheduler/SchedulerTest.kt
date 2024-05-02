package com.vojtasii.scheduler

import com.vojtasii.job.ScheduledJobStatus
import com.vojtasii.job.ScheduledJob
import com.vojtasii.repository.InMemJobRepository
import com.vojtasii.schedule.Schedule
import com.vojtasii.utils.ManualClock
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.expect
import kotlin.time.Duration.Companion.milliseconds

internal class SchedulerTest {

    @Test
    fun `schedule a job`() = runBlocking {
        val clock = ManualClock()
        val scheduler = Scheduler(InMemJobRepository(), clock)
        var counter1 = 0
        val job1 = ScheduledJob("1", Schedule(clock.now(), 100.milliseconds)) {
            counter1++
            ScheduledJobStatus.Finished
        }
        var counter2 = 0
        val job2 = ScheduledJob("2", Schedule(clock.now() + 500.milliseconds, 100.milliseconds)) {
            counter2++
            ScheduledJobStatus.Finished
        }

        scheduler.set(job1)
        scheduler.start { status ->
            expect(ScheduledJobStatus.Finished) { status }
        }
        scheduler.set(job2)

        clock.delayBy(950.milliseconds)

        expect(10, "Job 1") { counter1 }
        expect(5, "Job 2") { counter2 }
    }
}
