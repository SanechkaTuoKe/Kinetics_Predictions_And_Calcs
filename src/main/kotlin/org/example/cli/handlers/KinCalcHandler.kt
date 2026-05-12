package org.example.cli.handlers

import org.example.service.CalculationService
import org.example.python.PythonRunner

class KinCalcHandler : BaseHandler {

    override fun handle(
        params: List<String>,
        calculationService: CalculationService,
        commandList: Collection<BaseHandler>
    ): Boolean {

        val experiment = calculationService.getLastExperiment()

        if (experiment == null) {
            println("No experiment data found")
            println("   First use 'calc' to calculate release data")
            println("   Or use 'kin-manual <times> <percents>' for manual input\n")
            return true
        }

        println("\n⚙️  Analyzing kinetics for experiment '${experiment.id}'...")
        println("   Substance      : ${experiment.subject}")
        println("   Time points    : ${experiment.times.joinToString(", ")} min")
        println("   Release %%      : ${experiment.releasePercents.joinToString(", ") { "%.2f".format(it) }}\n")

        val result = PythonRunner.runKineticsModel(
            experiment.times,
            experiment.releasePercents
        )

        if (result != null) {
            val (model, k, t50, r2) = result


            println("  KINETICS ANALYSIS RESULTS")

            println("  Experiment     : ${experiment.id}")
            println("  Substance      : ${experiment.subject}")


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
                println("   No suitable model found (R² < 0.95)")
                println("  Data may not follow standard kinetic models")
            }


        } else {
            println(" Kinetics calculation failed\n")
        }

        return true
    }

    override fun help(): String = "kin-calc - Calculate kinetics for last experiment"
}