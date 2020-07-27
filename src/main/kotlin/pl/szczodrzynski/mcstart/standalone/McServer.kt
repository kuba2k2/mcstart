/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-27.
 */

package pl.szczodrzynski.mcstart.standalone

class McServer(commandLine: String) {

    init {
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
