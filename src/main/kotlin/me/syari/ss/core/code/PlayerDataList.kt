package me.syari.ss.core.code

import me.syari.ss.core.player.UUIDPlayer
import org.bukkit.OfflinePlayer

/**
 * プレイヤーのデータを保存します
 * @param T 保存するデータ型
 */
class PlayerDataList<T> {
    private val map = mutableMapOf<UUIDPlayer, T>()

    /**
     * プレイヤーのデータを取得します
     * @param uuidPlayer 指定プレイヤー
     * @param initialize デフォルト値
     * @return [T]
     */
    fun get(uuidPlayer: UUIDPlayer, initialize: () -> T): T {
        return map.getOrPut(uuidPlayer, initialize)
    }

    /**
     * プレイヤーのデータを取得します
     * @param player 指定プレイヤー
     * @param initialize デフォルト値
     * @return [T]
     */
    fun get(player: OfflinePlayer, initialize: () -> T): T {
        return get(UUIDPlayer(player), initialize)
    }
}