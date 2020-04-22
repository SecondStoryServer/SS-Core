package me.syari.ss.core.auto

/**
 * プラグインが無効になった時に処理を行う
 * @see onDisable
 */
interface OnDisable {
    companion object {
        /**
         * ```
         * override fun onDisable(){
         *      OnDisable.register(...)
         * }
         * ```
         * @param runClass onDisable() を実行するクラス
         * @see onDisable
         */
        fun register(vararg runClass: OnDisable) {
            runClass.forEach {
                it.onDisable()
            }
        }
    }

    /**
     * プラグインが無効になった時に実行される関数
     */
    fun onDisable()
}