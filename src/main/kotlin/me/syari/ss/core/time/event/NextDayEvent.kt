package me.syari.ss.core.time.event

import java.time.DayOfWeek

class NextDayEvent(dayOfWeek: DayOfWeek) : NextHourEvent(dayOfWeek, 0)