package me.syari.ss.core.time

import me.syari.ss.core.Main.Companion.corePlugin
import me.syari.ss.core.auto.Event
import me.syari.ss.core.auto.OnEnable
import me.syari.ss.core.scheduler.CustomScheduler.runLater
import me.syari.ss.core.time.event.NextDayEvent
import me.syari.ss.core.time.event.NextHourEvent
import me.syari.ss.core.time.event.NextMinuteEvent
import org.bukkit.event.EventHandler
import java.time.DayOfWeek
import java.time.LocalDateTime

object TimeScheduler : OnEnable, Event {
    private val everyWeekScheduler = mutableMapOf<ScheduleTimeEveryWeek, MutableSet<() -> Unit>>()
    private val everyDayScheduler = mutableMapOf<ScheduleTimeEveryDay, MutableSet<() -> Unit>>()
    private val everyHourScheduler = mutableMapOf<ScheduleTimeEveryHour, MutableSet<() -> Unit>>()

    fun scheduleEveryWeekAt(dayOfWeek: DayOfWeek, hour: Int, minute: Int, run: () -> Unit) {
        everyWeekScheduler.getOrPut(ScheduleTimeEveryWeek.create(dayOfWeek, hour, minute)) { mutableSetOf() }.add(run)
    }

    fun scheduleEveryDayAt(hour: Int, minute: Int, run: () -> Unit) {
        everyDayScheduler.getOrPut(ScheduleTimeEveryDay.create(hour, minute)) { mutableSetOf() }.add(run)
    }

    fun scheduleEveryHourAt(minute: Int, run: () -> Unit) {
        everyHourScheduler.getOrPut(ScheduleTimeEveryHour.create(minute)) { mutableSetOf() }.add(run)
    }

    fun getFormatTime(hour: Int, minute: Int): String {
        return "${String.format("%2d", hour)}:${String.format("%2d", minute)}"
    }

    override fun onEnable() {
        val now = LocalDateTime.now()
        runLater(corePlugin, (60 - now.second).toLong()) {
            nextMinute(ScheduleTimeEveryWeek.create(now.dayOfWeek, now.hour, now.minute + 1))
        }
    }

    private fun nextMinute(time: ScheduleTimeEveryWeek) {
        if (time.minute == 0) {
            if (time.hour == 0) {
                NextDayEvent(time.dayOfWeek)
            } else {
                NextHourEvent(time.dayOfWeek, time.hour)
            }
        } else {
            NextMinuteEvent(time.dayOfWeek, time.hour, time.minute)
        }.callEvent()
        runLater(corePlugin, 60 * 20) {
            nextMinute(time.getNextMinute())
        }
    }

    @EventHandler
    fun on(e: NextMinuteEvent) {
        val everyWeek = e.scheduleTime
        everyWeekScheduler[everyWeek]?.forEach { it.invoke() }
        val everyDay = everyWeek.everyDay
        everyDayScheduler[everyDay]?.forEach { it.invoke() }
        val everyHour = everyDay.everyHour
        everyHourScheduler[everyHour]?.forEach { it.invoke() }
    }
}