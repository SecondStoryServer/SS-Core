package me.syari.ss.core.particle

import me.syari.ss.core.scheduler.CustomScheduler.runListWithDelay
import me.syari.ss.core.scheduler.CustomTask
import org.bukkit.Location
import org.bukkit.entity.Entity

class CustomParticleList {
    private val listWithDelay = mutableMapOf<Long, MutableSet<CustomParticle>>()
    private val taskList = mutableSetOf<CustomTask>()
    private var accumulateDelay = 0L

    fun addParticle(particle: CustomParticle) {
        listWithDelay.getOrPut(accumulateDelay) { mutableSetOf() }.add(particle)
    }

    fun addDelay(delay: Long) {
        accumulateDelay += delay
    }

    private fun run(run: (CustomParticle) -> Unit) {
        taskList.addAll(runListWithDelay(listWithDelay, run))
    }

    fun spawn(location: Location) {
        run { it.spawn(location) }
    }

    fun spawn(entity: Entity) {
        run { it.spawn(entity) }
    }

    fun cancel() {
        taskList.forEach { it.cancel() }
        taskList.clear()
    }
}