package me.syari.ss.core.sql

import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

/**
 * データベース接続クラス
 */
interface Database {
    /**
     * 接続します
     * @return [Connection]
     */
    fun getConnection(): Connection

    /**
     * 接続テストを行います
     * @return [Boolean]
     */
    fun canConnect(): Boolean {
        return try {
            getConnection().use { }
            true
        } catch (ex: SQLException) {
            ex.printStackTrace()
            false
        }
    }

    /**
     * データベースを使用します
     * @param run データベースに対して実行する処理
     * @return [Boolean]
     */
    fun use(run: Statement.() -> Unit): Boolean {
        return try {
            getConnection().use { connection ->
                connection.createStatement().use(run)
            }
            true
        } catch (ex: SQLException) {
            false
        }
    }
}