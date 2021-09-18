/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-27.
 */

package pl.szczodrzynski.mcstart

class McServer(commandLine: String) {

    init {
        println("----\nRunning MC Server command line: $commandLine\n")
        commandLine.runCommand()
    }

    private fun String.runCommand() {
        ProcessBuilder(*split(" ").toTypedArray())
            .redirectInput(ProcessBuilder.Redirect.INHERIT)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
            .waitFor()
    }
}
