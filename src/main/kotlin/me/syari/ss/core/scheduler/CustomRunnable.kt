package me.syari.ss.core.scheduler

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class CustomRunnable(private val plugin: JavaPlugin, private val run: CustomTask.() -> Unit) : CustomTask {
    private val isRunning
        get() = alreadyInit && !task.isCancelled

    private var alreadyInit = false

    lateinit var task: BukkitTask

    lateinit var runnable: BukkitRunnable

    override val isCanceled get() = alreadyInit && task.isCancelled

    // override val isAsync get() = alreadyInit && task.isSync

    override var repeatRemain = 0

    private var onEndRepeatTask: (() -> Unit)? = null

    private var onCancelTask: (() -> Unit)? = null

    fun run(async: Boolean = false): CustomTask? {
        return runLater(0, async)
    }

    fun runLater(delay: Long, async: Boolean = false): CustomTask? {
        return runTimer(-1, delay, async)
    }

    fun runTimer(period: Long, delay: Long = 0, async: Boolean = false): CustomTask? {
        return runRepeatTimes(period, -1, delay, async)
    }

    fun runRepeatTimes(period: Long, times: Int, delay: Long = 0, async: Boolean = false): CustomTask? {
        return if (isRunning) {
            null
        } else {
            if (0 < times) {
                repeatRemain = times
                runnable = object : BukkitRunnable() {
                    override fun run() {
                        run.invoke(this@CustomRunnable)
                        if (repeatRemain == 0) {
                            onEndRepeatTask?.invoke()
                            cancel()
                        } else {
                            repeatRemain--
                        }
                    }
                }
            } else {
                repeatRemain = -1
                runnable = object : BukkitRunnable() {
                    override fun run() {
                        run.invoke(this@CustomRunnable)
                    }
                }
            }
            this.task = if (async) {
                runnable.runTaskTimerAsynchronously(plugin, delay, period)
            } else {
                runnable.runTaskTimer(plugin, delay, period)
            }
            alreadyInit = true
            this
        }
    }

    override fun cancel(): Boolean {
        return if (isRunning) {
            task.cancel()
            onCancelTask?.invoke()
            if (repeatRemain == 0) {
                onEndRepeatTask?.invoke()
            }
            true
        } else {
            false
        }
    }

    override fun onEndRepeat(run: () -> Unit): CustomTask? {
        onEndRepeatTask = run
        return this
    }

    override fun onCancel(run: () -> Unit): CustomTask? {
        onCancelTask = run
        return this
    }
}