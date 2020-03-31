package me.syari.ss.core.scheduler

import me.syari.ss.core.Main.Companion.corePlugin
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

    fun <T> runListWithDelay(
        listWithDelay: Map<Long, Set<T>>,
        run: (T) -> Unit
    ): Set<CustomTask> {
        return mutableSetOf<CustomTask>().also { taskList ->
            listWithDelay.forEach { (delay, value) ->
                runLater(corePlugin, delay, true) {
                    run(corePlugin, false) {
                        value.forEach {
                            run.invoke(it)
                        }
                    }
                    taskList.remove(this)
                }?.let { taskList.add(it) }
            }
        }
    }
}