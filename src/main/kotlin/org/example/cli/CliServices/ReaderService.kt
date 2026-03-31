package org.example.cli.CliServices

class ReaderService {

    fun readCommand(): List<String> {
        val input = readln()
        return input.trim().split(" ")
    }
}