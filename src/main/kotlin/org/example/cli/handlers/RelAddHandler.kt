package org.example.cli.handlers

import org.example.domain.Properties
import org.example.service.CalculationService
import org.example.python.PythonRunner

class RelAddHandler : BaseHandler {

    override fun handle(
        params: List<String>,
        calculationService: CalculationService,
        commandList: Collection<BaseHandler>
    ): Boolean {

        if (params.isEmpty()) {
            printUsage()
            return true
        }

        try {
            // Parse parameters
            val subjectName = params.getOrNull(0)?.uppercase() ?: run {
                println(" Missing substance parameter")
                printUsage()
                return true
            }

            val subject = when (subjectName) {
                "CLX" -> Properties.CLX
                "FLC" -> Properties.FLC
                else -> {
                    println(" Unknown substance: '$subjectName'. Use CLX or FLC.")
                    return true
                }
            }

            val concMg = params.getOrNull(1)?.toDoubleOrNull() ?: run {
                println("Invalid or missing concentration (mg/ml)")
                printUsage()
                return true
            }

            val uvTime = params.getOrNull(2)?.toDoubleOrNull() ?: run {
                println("Invalid or missing UV time (min)")
                printUsage()
                return true
            }

            val dilution = params.getOrNull(3)?.toDoubleOrNull() ?: run {
                println("Invalid or missing dilution factor")
                printUsage()
                return true
            }

            val adsOD = params.getOrNull(4)?.toDoubleOrNull() ?: run {
                println("Invalid or missing adsorption OD")
                printUsage()
                return true
            }

            val releaseODsStr = params.getOrNull(5) ?: run {
                println("Missing release OD values")
                printUsage()
                return true
            }

            val releaseODs = releaseODsStr.split(",").mapNotNull { it.trim().toDoubleOrNull() }
            if (releaseODs.isEmpty()) {
                println(" No valid release OD values found")
                return true
            }

            // Optional: time points (if not provided, generate defaults)
            val timePoints = if (params.size > 6) {
                params[6].split(",").mapNotNull { it.trim().toDoubleOrNull() }
            } else {
                // Generate default time points based on number of OD values
                (1..releaseODs.size).map { it * 15.0 }
            }

            if (timePoints.size != releaseODs.size) {
                println(" Number of time points (${timePoints.size}) must match number of OD values (${releaseODs.size})")
                return true
            }

            println("\n⚙  Calculating release kinetics...")
            println("   Substance   : $subjectName")
            println("   Conc.       : $concMg mg/ml")
            println("   UV time     : $uvTime min")
            println("   Dilution    : $dilution")
            println("   Ads OD      : $adsOD")
            println("   Release ODs : ${releaseODs.joinToString(", ")}")
            println("   Time points : ${timePoints.joinToString(", ")} min\n")

            // For now, create dummy data to pass to Python
            val adsData = listOf(adsOD)
            val relData = releaseODs

            val result = PythonRunner.runReleaseCalculation(subject, adsData, relData)

            if (result != null) {
                val (c0, adsPercents, relPercents) = result

                println("✓ CALCULATION RESULTS:")
                println("")
                println("  C₀ (reference)     : %.6f μmol/ml".format(c0))
                println("  Adsorption %%       : ${adsPercents.joinToString(", ") { "%.2f".format(it) }}")
                println("  Release %% (cumul.) : ${relPercents.joinToString(", ") { "%.2f".format(it) }}")
                println(" \n")

                // Save to service for kinetic calculation
                val expId = "exp_${System.currentTimeMillis()}"
                calculationService.saveExperiment(
                    id = expId,
                    subject = subject,
                    times = timePoints,
                    releasePercents = relPercents,
                    c0 = c0,
                    adsorptionPercents = adsPercents
                )

                println(" Use 'kin-calc' to calculate kinetics for this experiment\n")
            } else {
                println(" Calculation failed - check input data")
            }

        } catch (e: Exception) {
            println(" Error: ${e.message}")
            printUsage()
        }

        return true
    }

    private fun printUsage() {
        println("\n Usage:")
        println("  calc <SUBSTANCE> <CONC_MG> <UV_TIME> <DILUTION> <ADS_OD> <RELEASE_ODS> [TIME_POINTS]")
        println("\nParameters:")
        println("  SUBSTANCE     : CLX or FLC")
        println("  CONC_MG       : Initial concentration (mg/ml)")
        println("  UV_TIME       : UV treatment time (min)")
        println("  DILUTION      : Dilution factor")
        println("  ADS_OD        : Adsorption optical density")
        println("  RELEASE_ODS   : Comma-separated release OD values (e.g., 0.541,0.404,0.303)")
        println("  TIME_POINTS   : Optional comma-separated time points (e.g., 15,30,45)")
        println("\nExample:")
        println("  calc CLX 0.1 60 1.0 0.187 0.541,0.404,0.303,0.256 15,30,45,60\n")
    }

    override fun help(): String = "calc <SUBSTANCE> <CONC> <UV_TIME> <DIL> <ADS_OD> <REL_ODS> [TIMES] - Calculate release kinetics"
}