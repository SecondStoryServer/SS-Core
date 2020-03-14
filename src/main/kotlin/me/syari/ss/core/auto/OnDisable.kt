package me.syari.ss.core.auto

interface OnDisable {
    companion object {
        fun register(vararg init: OnDisable) {
            init.forEach {
                it.onDisable()
            }
        }
    }

    fun onDisable()
}