package org.example.service

import org.example.domain.Properties

data class ExperimentData(
    val id: String,
    val subject: Properties,
    val times: List<Double>,
    val releasePercents: List<Double>,
    val c0: Double,
    val adsorptionPercents: List<Double>
)

class CalculationService {

    private var lastExperiment: ExperimentData? = null

    fun saveExperiment(
        id: String,
        subject: Properties,
        times: List<Double>,
        releasePercents: List<Double>,
        c0: Double,
        adsorptionPercents: List<Double>
    ) {
        lastExperiment = ExperimentData(
            id = id,
            subject = subject,
            times = times,
            releasePercents = releasePercents,
            c0 = c0,
            adsorptionPercents = adsorptionPercents
        )
        println("Experiment '$id' saved for kinetics calculation")
    }

    fun getLastExperiment(): ExperimentData? {
        return lastExperiment
    }

    fun clearExperiment() {
        lastExperiment = null
    }
}