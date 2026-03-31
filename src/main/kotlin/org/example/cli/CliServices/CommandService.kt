package org.example.cli.CliServices

import org.example.cli.handlers.*
import org.example.service.*

class CommandService {

    private val calculationService = CalculationService()


    private val commands: Map<String, BaseHandler> = mapOf(

        "help" to HelpHandler(),
        "exit" to ExitHandler()
    )

    fun execute(input: List<String>): Boolean {
        if (input.isEmpty()) return true

        val cmd = input[0]
        val params = input.drop(1)

        val handler = commands[cmd]
        if (handler == null) {
            println("Error: command not found")
            return true
        }

        return try {
            handler.handle(params, instrumentService, commands.values)
        } catch (e: Exception) {
            ErrorHandler.handle(e)
            true
        }
    }
}