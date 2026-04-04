package org.example.cli.handlers

import org.example.service.CalculationService

class KinCalcHandler: BaseHandler {
    override fun handle(
        params: List<String>,
        calculationService: CalculationService,
        commandList: Collection<BaseHandler>
    ): Boolean {
        return TODO("Provide the return value")
    }

    override fun help(): String = "KinCalc for calculate reaction order"
}