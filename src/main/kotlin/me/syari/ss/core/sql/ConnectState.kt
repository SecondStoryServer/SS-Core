package me.syari.ss.core.sql

/**
 * データベース接続結果
 * @param message 日本語メッセージ
 */
enum class ConnectState(val message: String) {
    /**
     * 成功
     */
    Success("成功しました"),

    /**
     * 失敗
     */
    CatchException("失敗しました"),

    /**
     * 設定不足
     */
    NullError("必要な設定が足りていません");

    val isSuccess get() = this == Success

    companion object {
        /**
         * Boolean を ConnectState に変換します
         * @param bool canConnect(), use() の結果
         * @return [ConnectState]
         */
        fun get(bool: Boolean?): ConnectState {
            return when (bool) {
                true -> Success
                false -> CatchException
                null -> NullError
            }
        }

        /**
         * データベースに接続できるか確認します
         * @return [ConnectState]
         */
        fun Database?.checkConnect(): ConnectState {
            return ConnectState.get(this?.canConnect())
        }
    }
}