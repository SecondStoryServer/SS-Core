package me.syari.ss.core.sql

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

data class MySQL(val host: String, val port: Int, val database: String, val user: String, val password: String) {
    companion object {
        fun create(host: String?, port: Int?, database: String?, user: String?, password: String?): MySQL? {
            return if (host != null && port != null && database != null && user != null && password != null) {
                MySQL(host, port, database, user, password)
            } else {
                null
            }
        }
    }

    /**
     * 接続テストを行います
     * @return [Boolean]
     */
    fun canConnect(): Boolean {
        var connection: Connection? = null
        return try {
            connection = DriverManager.getConnection("jdbc:mysql://$host:$port/$database", user, password)
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
            connection = DriverManager.getConnection("jdbc:mysql://$host:$port/$database", user, password)
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