package org.example.cli.handlers

import org.example.service.CalculationService

class HelpHandler : BaseHandler {
    override fun handle(
        params: List<String>,
        calculationService: CalculationService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        println("Available commands:")
        commandList.forEach { println(it.help()) }
        return true
    }

    override fun help(): String = "help - Show this help"
}