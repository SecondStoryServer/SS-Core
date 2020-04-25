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
        var connection: Connection? = null
        return try {
            connection = getConnection()
            true
        } catch (ex: SQLException) {
            ex.printStackTrace()
            false
        } finally {
            connection?.close()
        }
    }

    /**
     * データベースを使用します
     * @param run データベースに対して実行する処理
     * @return [Boolean]
     */
    fun use(run: Statement.() -> Unit): Boolean {
        var connection: Connection? = null
        var statement: Statement? = null
        return try {
            connection = getConnection()
            statement = connection.createStatement()
            run.invoke(statement)
            true
        } catch (ex: SQLException) {
            false
        } finally {
            statement?.close()
            connection?.close()
        }
    }
}