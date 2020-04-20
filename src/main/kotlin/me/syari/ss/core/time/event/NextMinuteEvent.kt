package me.syari.ss.core.time.event

import me.syari.ss.core.event.CustomEvent
import me.syari.ss.core.time.ScheduleTimeEveryWeek
import me.syari.ss.core.time.TimeScheduler.getFormatTime
import java.time.DayOfWeek

/**
 * 分が変わった時(XX:XX)に発生するイベントです
 */
open class NextMinuteEvent(val dayOfWeek: DayOfWeek, val hour: Int, val minute: Int) : CustomEvent() {
    val scheduleTime by lazy { ScheduleTimeEveryWeek(dayOfWeek, hour, minute) }

    val formatTime by lazy { getFormatTime(hour, minute) }
}