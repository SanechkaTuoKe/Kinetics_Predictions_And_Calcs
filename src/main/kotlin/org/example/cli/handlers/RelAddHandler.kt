package org.example.cli.handlers

import org.example.service.CalculationService

class RelAddHandler: BaseHandler {
    override fun handle(
        params: List<String>,
        calculationService: CalculationService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun help(): String = "RelAdd for add and save your data"
}
