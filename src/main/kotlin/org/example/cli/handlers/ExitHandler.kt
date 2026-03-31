package org.example.cli.handlers

import org.example.service.CalculationService

class ExitHandler : BaseHandler {
    override fun handle(
        params: List<String>,
        calculationService: CalculationService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        println("Exiting...")
        return false
    }

    override fun help(): String = "exit - Exit the program"
}