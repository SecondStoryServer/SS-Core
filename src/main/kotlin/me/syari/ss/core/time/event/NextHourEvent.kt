package me.syari.ss.core.time.event

import java.time.DayOfWeek

open class NextHourEvent(dayOfWeek: DayOfWeek, hour: Int) : NextMinuteEvent(dayOfWeek, hour, 0)