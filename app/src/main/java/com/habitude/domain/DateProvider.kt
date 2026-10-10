package com.habitude.domain

import java.time.LocalDate
import javax.inject.Inject

/** Abstracts "today" so ViewModels and workers can be tested with fixed dates. */
interface DateProvider {
    fun todayEpochDay(): Long
}

class SystemDateProvider @Inject constructor() : DateProvider {
    override fun todayEpochDay(): Long = LocalDate.now().toEpochDay()
}
