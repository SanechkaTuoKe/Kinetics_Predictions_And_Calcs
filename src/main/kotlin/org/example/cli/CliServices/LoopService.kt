package org.example.cli.CliServices

class LoopService {

    private val readerService = ReaderService()
    private val commandService = CommandService()

    fun loopOfCommands() {
        while (true) {
            val input = readerService.readCommand()

            val shouldContinue = commandService.execute(input)

            if (!shouldContinue) {
                println("Bye!")
                break
            }
        }
    }
}