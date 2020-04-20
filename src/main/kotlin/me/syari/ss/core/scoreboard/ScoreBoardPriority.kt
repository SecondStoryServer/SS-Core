package me.syari.ss.core.scoreboard

enum class ScoreBoardPriority(val level: Int) {
    VeryHigh(2),
    High(1),
    Normal(0),
    Low(-1),
    VeryLow(-2)
}
