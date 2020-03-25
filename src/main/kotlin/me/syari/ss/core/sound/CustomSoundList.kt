package me.syari.ss.core.sound

import me.syari.ss.core.Main.Companion.corePlugin
import me.syari.ss.core.scheduler.CustomScheduler.run
import me.syari.ss.core.scheduler.CustomScheduler.runLater
import me.syari.ss.core.scheduler.CustomTask
import org.bukkit.Location
import org.bukkit.entity.Entity

class CustomSoundList {
    private val listWithDelay = mutableMapOf<Long, MutableSet<CustomSound>>()
    private val taskList = mutableSetOf<CustomTask>()
    private var accumulateDelay = 0L

    fun addSound(sound: CustomSound): CustomSoundList {
        listWithDelay.getOrPut(accumulateDelay){ mutableSetOf() }.add(sound)
        return this
    }

    fun addDelay(delay: Long){
        accumulateDelay += delay
    }

    private fun run(run: (CustomSound) -> Unit){
        listWithDelay.forEach { (delay, sound) ->
            runLater(corePlugin, delay, true){
                run(corePlugin, false){
                    sound.forEach {
                        run.invoke(it)
                    }
                }
                taskList.remove(this)
            }?.let { taskList.add(it) }
        }
    }

    fun play(location: Location){
        run { it.play(location) }
    }

    fun play(entity: Entity){
        run { it.play(entity) }
    }

    fun cancel(){
        taskList.forEach { it.cancel() }
        taskList.clear()
    }
}