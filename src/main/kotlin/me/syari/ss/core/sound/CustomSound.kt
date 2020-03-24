package me.syari.ss.core.sound

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.SoundCategory

class CustomSound(private val type: Sound, private val volume: Float, private val pitch: Float, private val category: SoundCategory) {
    fun play(location: Location){
        location.world.playSound(location, type, category, volume, pitch)
    }

    fun repeat(location: Location, number: Int){
        for(i in 0 until number){
            play(location)
        }
    }
}