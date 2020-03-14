package me.syari.ss.core.scoreboard

import me.syari.ss.core.player.UUIDPlayer
import org.bukkit.scoreboard.DisplaySlot

data class ScoreBoardPlayer(val uuidPlayer: UUIDPlayer) {
    companion object {
        private val playerList = mutableMapOf<UUIDPlayer, ScoreBoardPlayer>()

        fun addBoard(uuidPlayer: UUIDPlayer, board: CustomScoreBoard) {
            playerList.getOrPut(uuidPlayer) { ScoreBoardPlayer(uuidPlayer) }.addBoard(board)
        }

        fun removeBoard(uuidPlayer: UUIDPlayer, board: CustomScoreBoard) {
            playerList[uuidPlayer]?.removeBoard(board)
        }

        fun clearBoard(uuidPlayer: UUIDPlayer) {
            playerList.remove(uuidPlayer)
        }
    }

    private val boardList = mutableSetOf<CustomScoreBoard>()

    var board: CustomScoreBoard? = null
        private set

    fun setBoard(board: CustomScoreBoard?) {
        board?.show(this) ?: uuidPlayer.player?.scoreboard?.clearSlot(DisplaySlot.SIDEBAR)
        this.board = board
    }

    private fun updateBoard() {
        val lastBoard = board
        val board = boardList.maxBy { it.priority.level }
        if (lastBoard != board) {
            setBoard(board)
        }
    }

    fun updateBoard(board: CustomScoreBoard) {
        if (this.board == board) {
            board.show(this)
        }
    }

    fun addBoard(board: CustomScoreBoard) {
        boardList.add(board)
        updateBoard()
    }

    fun removeBoard(board: CustomScoreBoard) {
        boardList.remove(board)
        if (boardList.isEmpty()) {
            clearBoard(uuidPlayer)
        } else {
            updateBoard()
        }
    }
}