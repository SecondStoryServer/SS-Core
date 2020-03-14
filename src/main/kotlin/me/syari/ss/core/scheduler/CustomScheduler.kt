package me.syari.ss.core.scheduler

import org.bukkit.plugin.java.JavaPlugin

object CustomScheduler {
    fun schedule(plugin: JavaPlugin, run: CustomTask.() -> Unit): CustomRunnable {
        return CustomRunnable(plugin, run)
    }

    fun run(plugin: JavaPlugin, async: Boolean = false, run: CustomTask.() -> Unit): CustomTask? {
        return schedule(plugin, run).run(async)
    }

    fun runLater(plugin: JavaPlugin, delay: Long, async: Boolean = false, run: CustomTask.() -> Unit): CustomTask? {
        return schedule(plugin, run).runLater(delay, async)
    }

    fun runTimer(
        plugin: JavaPlugin,
        period: Long,
        delay: Long = 0,
        async: Boolean = false,
        run: CustomTask.() -> Unit
    ): CustomTask? {
        return schedule(plugin, run).runTimer(period, delay, async)
    }

    fun runRepeatTimes(
        plugin: JavaPlugin,
        period: Long,
        times: Int,
        delay: Long = 0,
        async: Boolean = false,
        run: CustomTask.() -> Unit
    ): CustomTask? {
        return schedule(plugin, run).runRepeatTimes(period, times, delay, async)
    }
}