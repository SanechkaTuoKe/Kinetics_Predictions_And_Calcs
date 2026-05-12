package org.example.cli.handlers

import org.example.service.CalculationService
import org.example.python.PythonRunner

class KinCalcManualHandler : BaseHandler {

    override fun handle(
        params: List<String>,
        calculationService: CalculationService,
        commandList: Collection<BaseHandler>
    ): Boolean {

        if (params.size < 2) {
            printUsage()
            return true
        }

        val times = params[0].split(",").mapNotNull { it.trim().toDoubleOrNull() }
        val percents = params[1].split(",").mapNotNull { it.trim().toDoubleOrNull() }

        if (times.isEmpty() || percents.isEmpty()) {
            println(" Invalid data format")
            printUsage()
            return true
        }

        if (times.size != percents.size) {
            println(" Mismatch: ${times.size} time points ≠ ${percents.size} percentages")
            return true
        }

        println("\n  Analyzing kinetics (manual input)...")
        println("   Time points : ${times.joinToString(", ")} min")
        println("   Release %%   : ${percents.joinToString(", ") { "%.2f".format(it) }}\n")

        val result = PythonRunner.runKineticsModel(times, percents)

        if (result != null) {
            val (model, k, t50, r2) = result


            println("  KINETICS ANALYSIS RESULTS")


            if (model != "none") {
                val modelName = when (model) {
                    "zero" -> "Zero-order"
                    "first" -> "First-order"
                    "second" -> "Second-order"
                    else -> model.uppercase()
                }

                println("  Best fit model : $modelName")
                println("  R²             : ${"%.4f".format(r2)}")
                println("  Rate const (k) : ${"%.6f".format(k)}")
                println("  Half-life (t½) : ${"%.2f".format(t50)} min")
            } else {
                println("  ⚠ No suitable model found (R² < 0.95)")
            }


        } else {
            println("Calculation failed\n")
        }

        return true
    }

    private fun printUsage() {
        println("\n Usage:")
        println("  kin-manual <TIME_POINTS> <RELEASE_PERCENTS>")
        println("\nExample:")
        println("  kin-manual 15,30,45,60 10.5,25.3,40.1,58.7\n")
    }

    override fun help(): String = "kin-manual <TIMES> <PERCENTS> - Manual kinetics calculation"
}