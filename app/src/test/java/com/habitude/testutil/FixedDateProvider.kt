package com.habitude.testutil

import com.habitude.domain.DateProvider

class FixedDateProvider(var today: Long) : DateProvider {
    override fun todayEpochDay(): Long = today
}
