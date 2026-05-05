package org.example

import org.example.cli.CliServices.LoopService
import org.example.repository.DatabaseRepository


fun main(){
    println("type help for help")
    LoopService().loopOfCommands()
    DatabaseRepository.initDatabase()
    DatabaseRepository.importCsv()
}