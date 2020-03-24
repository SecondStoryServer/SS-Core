package me.syari.ss.core.sound

import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.SoundCategory

object CreateSound {
    fun sound(type: Sound, volume: Float, pitch: Float, category: SoundCategory = SoundCategory.MASTER): CustomSound {
        return CustomSound(type, volume, pitch, category)
    }

    fun playSound(location: Location, type: Sound, volume: Float, pitch: Float, category: SoundCategory = SoundCategory.MASTER){
        sound(type, volume, pitch, category).play(location)
    }
}