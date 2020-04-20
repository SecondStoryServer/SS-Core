package me.syari.ss.core.auto

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