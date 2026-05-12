package org.example.python

import org.example.domain.Properties
import java.io.File

object PythonRunner {

    fun runReleaseCalculation(
        subject: Properties,
        adsData: List<Double>,
        relData: List<Double>
    ): Triple<Double, List<Double>, List<Double>>? {
        val subjectCode = when (subject) {
            Properties.CLX -> "CLX"
            Properties.FLC -> "FLC"
        }

        val output = executeScript(
            "conc_calc.py",
            listOf(
                subjectCode,
                adsData.joinToString(","),
                relData.joinToString(",")
            )
        ) ?: return null

        val lines = output.lineSequence().toList()
        if (lines.size < 3) return null

        val c0 = lines[0].substringAfter("C0:").toDoubleOrNull() ?: return null
        val ads = lines[1].substringAfter("ADS:").split(",").mapNotNull { it.toDoubleOrNull() }
        val rel = lines[2].substringAfter("REL:").split(",").mapNotNull { it.toDoubleOrNull() }

        return Triple(c0, ads, rel)
    }

    fun runKineticsModel(
        times: List<Double>,
        percents: List<Double>
    ): Quadruple<String, Double, Double, Double>? {
        val output = executeScript(
            "kin_calc.py",
            listOf(
                times.joinToString(","),
                percents.joinToString(",")
            )
        ) ?: return null

        val parts = output.trim().split(",")
        if (parts.size < 4) return null

        return Quadruple(
            parts[0],
            parts[1].toDoubleOrNull() ?: return null,
            parts[2].toDoubleOrNull() ?: return null,
            parts[3].toDoubleOrNull() ?: return null
        )
    }

    private fun executeScript(scriptName: String, args: List<String>): String? {
        return try {
            // Try multiple locations for the Python scripts
            val possiblePaths = listOf(
                "src/python-app/$scriptName",
                "python-app/$scriptName",
                "../python-app/$scriptName",
                "src/main/resources/scripts/$scriptName"
            )

            val scriptPath = possiblePaths
                .map { File(it) }
                .firstOrNull { it.exists() }
                ?.absolutePath

            if (scriptPath == null) {
                System.err.println("Python script not found: $scriptName")
                System.err.println("   Searched in: ${possiblePaths.joinToString(", ")}")
                return null
            }

            val command = listOf("python3", scriptPath) + args

            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            val result = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            if (exitCode == 0 && result.isNotBlank()) {
                result.trim()
            } else {
                System.err.println("Python script failed with exit code $exitCode")
                System.err.println("   Output: $result")
                null
            }
        } catch (e: Exception) {
            System.err.println(" Python execution error: ${e.message}")
            null
        }
    }
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)