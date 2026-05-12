package org.example.validator

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
) {
    companion object {
        val SUCCESS = ValidationResult(true)
        fun error(message: String) = ValidationResult(false, listOf(message))
        fun errors(messages: List<String>) = ValidationResult(false, messages)
    }

    fun throwIfInvalid() {
        if (!isValid) throw IllegalArgumentException(errors.joinToString("; "))
    }
}