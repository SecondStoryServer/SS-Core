package me.syari.ss.core.scoreboard

import org.bukkit.entity.Player

interface CustomScoreBoardEdit {
    fun space() = line("")

    fun line(text: String)

    fun line(text: Player.() -> String)
}