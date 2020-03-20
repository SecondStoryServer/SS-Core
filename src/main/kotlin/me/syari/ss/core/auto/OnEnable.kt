package me.syari.ss.core.auto

interface OnEnable {
    companion object {
        fun register(vararg init: OnEnable) {
            init.forEach {
                it.onEnable()
            }
        }
    }

    fun onEnable()
}