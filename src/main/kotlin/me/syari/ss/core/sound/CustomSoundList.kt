package me.syari.ss.core.sound

import me.syari.ss.core.scheduler.CustomScheduler.runListWithDelay
import me.syari.ss.core.scheduler.CustomTask
import org.bukkit.Location
import org.bukkit.entity.Entity

class CustomSoundList {
    private val listWithDelay = mutableMapOf<Long, MutableSet<CustomSound>>()
    private var accumulateDelay = 0L

    fun addSound(sound: CustomSound) {
        listWithDelay.getOrPut(accumulateDelay) { mutableSetOf() }.add(sound)
    }

    fun addDelay(delay: Long) {
        accumulateDelay += delay
    }

    private fun run(run: (CustomSound) -> Unit): Set<CustomTask> {
        return runListWithDelay(listWithDelay, run)
    }

    fun play(location: Location): Set<CustomTask> {
        return run { it.play(location) }
    }

    fun play(entity: Entity): Set<CustomTask> {
        return run { it.play(entity) }
    }

    fun getRequireTime(): Long {
        return accumulateDelay
    }
}