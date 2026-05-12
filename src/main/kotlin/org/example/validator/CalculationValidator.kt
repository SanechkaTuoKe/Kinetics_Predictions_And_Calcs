package org.example.validator

object CalculationValidator {

    private const val MIN_POINTS_FOR_FIT = 3
    private const val MIN_R2 = 0.0
    private const val MAX_R2 = 1.0

    fun validateKineticsInput(times: List<Double>, percents: List<Double>): ValidationResult {
        val errors = mutableListOf<String>()

        if (times.size < MIN_POINTS_FOR_FIT) {
            errors.add("Для аппроксимации нужно ≥ $MIN_POINTS_FOR_FIT точек, получено ${times.size}")
        }
        if (times.size != percents.size) {
            errors.add("Размер массивов не совпадает: times=${times.size}, percents=${percents.size}")
        }

        percents.forEachIndexed { i, p ->
            if (p !in 0.0..100.0) {
                errors.add("Percent[$i] = $p выходит за пределы [0, 100]")
            }
        }

        if (percents.distinct().size == 1 && percents.isNotEmpty()) {
            errors.add("Все значения release% одинаковы (${percents[0]}): невозможно подобрать модель")
        }

        return if (errors.isEmpty()) ValidationResult.SUCCESS else ValidationResult.errors(errors)
    }

    fun validateKineticsResult(
        model: String?,
        r2: Double?,
        rateConstant: Double?,
        halfTime: Double?
    ): ValidationResult {
        val errors = mutableListOf<String>()

        if (model == null) errors.add("Кинетическая модель не определена")
        r2?.let { if (it < MIN_R2 || it > MAX_R2) errors.add("R²=$it недопустимо") }
        rateConstant?.let { k -> if (k < 0) errors.add("Константа скорости k=$k < 0") }
        halfTime?.let { t -> if (t < 0) errors.add("Время полувысвобождения t50=$t < 0") }

        return if (errors.isEmpty()) ValidationResult.SUCCESS else ValidationResult.errors(errors)
    }

    fun validateRawInput(subject: String, adsStr: String, relStr: String): ValidationResult {
        val errors = mutableListOf<String>()
        if (subject.uppercase() !in setOf("CLX", "FLC")) errors.add("Неизвестный препарат: $subject")
        if (adsStr.isBlank()) errors.add("Данные адсорбции пустые")
        if (relStr.isBlank()) errors.add("Данные высвобождения пустые")
        return if (errors.isEmpty()) ValidationResult.SUCCESS else ValidationResult.errors(errors)
    }
}