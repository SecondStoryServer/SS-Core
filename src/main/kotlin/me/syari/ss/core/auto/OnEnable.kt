package me.syari.ss.core.auto

/**
 * プラグインが有効になった時に処理を行う
 */
interface OnEnable {
    companion object {
        /**
         * ```
         * override fun onEnable(){
         *      OnEnable.register(...)
         * }
         * ```
         * @param runClass onEnable() を実行するクラス
         * @see onEnable
         */
        fun register(vararg runClass: OnEnable) {
            runClass.forEach {
                it.onEnable()
            }
        }
    }

    /**
     * プラグインが有効になった時に実行される関数
     * @see register
     */
    fun onEnable()
}