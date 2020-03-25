package me.syari.ss.core.particle

import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

sealed class CustomParticle(
    type: Particle,
    count: Int,
    speed: Double,
    offsetX: Double,
    offsetY: Double,
    offsetZ: Double
) {
    val builder = ParticleBuilder(type)
        .count(count)
        .offset(offsetX, offsetY, offsetZ)
        .extra(speed)

    fun spawn(location: Location) {
        builder.location(location).spawn()
    }

    fun spawn(entity: Entity) {
        spawn(entity.location)
    }

    class Normal(
        type: Particle,
        count: Int,
        speed: Double,
        offsetX: Double = 0.0,
        offsetY: Double = 0.0,
        offsetZ: Double = 0.0
    ) : CustomParticle(type, count, speed, offsetX, offsetY, offsetZ)

    class ItemCrack(
        material: Material,
        count: Int,
        speed: Double,
        offsetX: Double = 0.0,
        offsetY: Double = 0.0,
        offsetZ: Double = 0.0
    ) : CustomParticle(Particle.ITEM_CRACK, count, speed, offsetX, offsetY, offsetZ) {
        init {
            builder.data(ItemStack(material))
        }
    }

    class BlockCrack(
        material: Material,
        count: Int,
        speed: Double,
        offsetX: Double = 0.0,
        offsetY: Double = 0.0,
        offsetZ: Double = 0.0
    ) : CustomParticle(Particle.BLOCK_CRACK, count, speed, offsetX, offsetY, offsetZ) {
        init {
            builder.data(material.createBlockData())
        }
    }

    class BlockDust(
        material: Material,
        count: Int,
        speed: Double,
        offsetX: Double = 0.0,
        offsetY: Double = 0.0,
        offsetZ: Double = 0.0
    ) : CustomParticle(Particle.BLOCK_DUST, count, speed, offsetX, offsetY, offsetZ) {
        init {
            builder.data(material.createBlockData())
        }
    }

    class FallingDust(
        material: Material,
        count: Int,
        speed: Double,
        offsetX: Double = 0.0,
        offsetY: Double = 0.0,
        offsetZ: Double = 0.0
    ) : CustomParticle(Particle.FALLING_DUST, count, speed, offsetX, offsetY, offsetZ) {
        init {
            builder.data(material.createBlockData())
        }
    }

    class RedStone(
        red: Int,
        blue: Int,
        green: Int,
        count: Int,
        speed: Double,
        offsetX: Double = 0.0,
        offsetY: Double = 0.0,
        offsetZ: Double = 0.0
    ) : CustomParticle(Particle.REDSTONE, count, speed, offsetX, offsetY, offsetZ) {
        init {
            fun convertColor(value: Int): Int {
                return when {
                    value < 0 -> 0
                    255 < value -> 255
                    else -> value
                }
            }

            builder.color(convertColor(blue), convertColor(green), convertColor(red))
        }
    }
}