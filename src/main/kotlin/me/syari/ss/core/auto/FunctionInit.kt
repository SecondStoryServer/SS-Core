package me.syari.ss.core.auto

interface FunctionInit {
    companion object {
        fun register(vararg init: FunctionInit) {
            init.forEach {
                it.init()
            }
        }
    }

    fun init()
}