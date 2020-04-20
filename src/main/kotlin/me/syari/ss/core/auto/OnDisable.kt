package me.syari.ss.core.auto

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

    fun onDisable()
}