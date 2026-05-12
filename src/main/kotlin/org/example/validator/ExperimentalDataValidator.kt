package org.example.validator

import org.example.domain.Properties


object ExperimentalDataValidator {

    private const val MIN_OD = 0.0
    private const val MAX_OD = 3.0          // Предел линейности спектрофотометра
    private const val MIN_TIME = 0.0
    private const val MAX_TIME = 1440.0     // 24 часа
    private const val MIN_PERCENT = 0.0
    private const val MAX_PERCENT = 100.0

    fun validateOpticalDensity(values: List<Double>): ValidationResult {
        val errors = values.mapIndexedNotNull { i, v ->
            when {
                v < MIN_OD -> "OD[$i] = $v < $MIN_OD"
                v > MAX_OD -> "OD[$i] = $v > $MAX_OD (超出 линейного диапазона)"
                else -> null
            }
        }
        return if (errors.isEmpty()) ValidationResult.SUCCESS else ValidationResult.errors(errors)
    }

    fun validateTimePoints(times: List<Double>): ValidationResult {
        val errors = mutableListOf<String>()
        times.forEachIndexed { i, t ->
            if (t < MIN_TIME) errors.add("Time[$i] = $t < $MIN_TIME")
            if (t > MAX_TIME) errors.add("Time[$i] = $t > $MAX_TIME мин")
        }
        if (times.size > 1) {
            for (i in 1 until times.size) {
                if (times[i] <= times[i - 1]) {
                    errors.add("Время должно строго возрастать: ${times[i-1]} ≥ ${times[i]}")
                    break
                }
            }
        }
        return if (errors.isEmpty()) ValidationResult.SUCCESS else ValidationResult.errors(errors)
    }

    fun validatePercentages(percents: List<Double>): ValidationResult {
        val errors = percents.mapIndexedNotNull { i, p ->
            when {
                p < MIN_PERCENT -> "Percent[$i] = $p < $MIN_PERCENT"
                p > MAX_PERCENT -> "Percent[$i] = $p > $MAX_PERCENT%"
                else -> null
            }
        }
        return if (errors.isEmpty()) ValidationResult.SUCCESS else ValidationResult.errors(errors)
    }

    fun validateExperiment(
        subject: Properties,
        adsorptionOD: List<Double>,
        releaseOD: List<Double>,
        timePoints: List<Double>
    ): ValidationResult {
        val errors = mutableListOf<String>()
        validateOpticalDensity(adsorptionOD).errors.let { errors.addAll(it) }
        validateOpticalDensity(releaseOD).errors.let { errors.addAll(it) }
        validateTimePoints(timePoints).errors.let { errors.addAll(it) }

        if (releaseOD.size != timePoints.size) {
            errors.add("Несоответствие длин: release (${releaseOD.size}) ≠ time (${timePoints.size})")
        }

        return if (errors.isEmpty()) ValidationResult.SUCCESS else ValidationResult.errors(errors)
    }
}