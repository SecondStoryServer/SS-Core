package me.syari.ss.core.scoreboard

import org.bukkit.plugin.java.JavaPlugin

object CreateScoreBoard {
    fun createScoreBoard(
        plugin: JavaPlugin,
        title: String,
        priority: ScoreBoardPriority,
        run: CustomScoreBoardEdit.() -> Unit
    ): CustomScoreBoard {
        return CustomScoreBoard(plugin, title, priority).apply(run)
    }
}