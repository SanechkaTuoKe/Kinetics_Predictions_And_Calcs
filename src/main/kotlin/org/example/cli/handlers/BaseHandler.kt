package org.example.cli.handlers

import org.example.service.CalculationService

interface BaseHandler {
    fun handle(params: List<String>,
               calculationService:CalculationService,
               commandList: Collection<BaseHandler>): Boolean
    fun help(): String
}