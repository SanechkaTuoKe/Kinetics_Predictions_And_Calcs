package org.example.cli.handlers

object ErrorHandler {
    fun handle(e: Exception) {
        println("Error: ${e.message}")
    }
}