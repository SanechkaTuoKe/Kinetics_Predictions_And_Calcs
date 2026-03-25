package org.example.repository

import java.sql.Connection
import java.sql.DriverManager

object DatabaseRepository {

    private val connection: Connection by lazy {
        DriverManager.getConnection("jdbc:sqlite:kinetics.db")
    }

    fun initDatabase() {
        // Таблица экспериментальных данных
        val experimentalDataSql = """
            CREATE TABLE IF NOT EXISTS experimental_data (
                id TEXT,
                uv_time REAL,
                release_percent REAL,
                PRIMARY KEY(id, uv_time)
            );
        """.trimIndent()

        // Таблица результатов расчёта кинетики
        val kineticResultsSql = """
            CREATE TABLE IF NOT EXISTS kinetic_results (
                id TEXT PRIMARY KEY,
                react_order REAL,
                r_squared REAL,
                k1 REAL,
                t_half REAL
            );
        """.trimIndent()

        connection.createStatement().use { stmt ->
            stmt.execute(experimentalDataSql)
            stmt.execute(kineticResultsSql)
        }
    }
}