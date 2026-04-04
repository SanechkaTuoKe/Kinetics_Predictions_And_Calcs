package org.example.repository

import java.sql.Connection
import java.sql.DriverManager

object DatabaseRepository {

    val connection: Connection by lazy {
        DriverManager.getConnection("jdbc:sqlite:kinetics.db")
    }

    fun initDatabase() {
        val sql = """
            CREATE TABLE IF NOT EXISTS experimental_data (
                id TEXT,
                conc TEXT,
                uv_time REAL,
                release_time REAL,
                release_percent REAL,
                PRIMARY KEY(id, release_time)
            );
        """.trimIndent()

        connection.createStatement().execute(sql)
    }

    fun importCsv() {
        val inputStream = object {}.javaClass.getResourceAsStream("/data.csv")
            ?: throw RuntimeException("CSV не найден")

        val lines = inputStream.bufferedReader().readLines().drop(1)

        val sql = """
            INSERT OR IGNORE INTO experimental_data 
            (id, conc, uv_time, release_time, release_percent)
            VALUES (?, ?, ?, ?, ?)
        """

        val stmt = connection.prepareStatement(sql)

        for (line in lines) {
            val parts = line.split(",")

            val id = parts[0]
            val conc = parts[1].replace("\"", "")
            val uvTime = parts[2].toDouble()
            val releaseTime = parts[3].toDouble()
            val releasePercent = parts[4].toDouble()

            stmt.setString(1, id)
            stmt.setString(2, conc)
            stmt.setDouble(3, uvTime)
            stmt.setDouble(4, releaseTime)
            stmt.setDouble(5, releasePercent)

            stmt.addBatch()
        }

        stmt.executeBatch()
        println("CSV импортирован в SQLite")
    }
}