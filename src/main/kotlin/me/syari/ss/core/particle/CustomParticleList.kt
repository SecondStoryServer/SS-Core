package me.syari.ss.core.particle

import me.syari.ss.core.scheduler.CustomScheduler.runListWithDelay
import me.syari.ss.core.scheduler.CustomTask
import org.bukkit.Location
import org.bukkit.entity.Entity

class CustomParticleList {
    private val listWithDelay = mutableMapOf<Long, MutableSet<CustomParticle>>()
    private var accumulateDelay = 0L

    fun addParticle(particle: CustomParticle) {
        listWithDelay.getOrPut(accumulateDelay) { mutableSetOf() }.add(particle)
    }

    fun addDelay(delay: Long) {
        accumulateDelay += delay
    }

    private fun run(run: (CustomParticle) -> Unit): Set<CustomTask> {
        return runListWithDelay(listWithDelay, run)
    }

    fun spawn(location: Location): Set<CustomTask> {
        return run { it.spawn(location) }
    }

    fun spawn(entity: Entity): Set<CustomTask> {
        return run { it.spawn(entity) }
    }

    fun getRequireTime(): Long {
        return accumulateDelay
    }
}