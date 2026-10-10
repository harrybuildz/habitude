package com.habitude.testutil

import com.habitude.work.ReminderScheduler

class FakeReminderScheduler : ReminderScheduler {

    val scheduledHours = mutableListOf<Int>()
    var cancelCount = 0
        private set

    override fun scheduleDaily(hour: Int) {
        scheduledHours += hour
    }

    override fun cancel() {
        cancelCount++
    }
}
