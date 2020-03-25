package me.syari.ss.core.sound

import me.syari.ss.core.scheduler.CustomScheduler
import me.syari.ss.core.scheduler.CustomTask
import org.bukkit.Location
import org.bukkit.entity.Entity

class CustomSoundList {
    private val listWithDelay = mutableMapOf<Long, MutableSet<CustomSound>>()
    private val taskList = mutableSetOf<CustomTask>()
    private var accumulateDelay = 0L

    fun addSound(sound: CustomSound) {
        listWithDelay.getOrPut(accumulateDelay) { mutableSetOf() }.add(sound)
    }

    fun addDelay(delay: Long) {
        accumulateDelay += delay
    }

    private fun run(run: (CustomSound) -> Unit) {
        taskList.addAll(CustomScheduler.runListWithDelay(listWithDelay, run))
    }

    fun play(location: Location) {
        run { it.play(location) }
    }

    fun play(entity: Entity) {
        run { it.play(entity) }
    }

    fun cancel() {
        taskList.forEach { it.cancel() }
        taskList.clear()
    }
}