/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-27.
 */

package pl.szczodrzynski.mcstart

import kotlinx.coroutines.*
import pl.szczodrzynski.mcstart.config.Config

class McServer(
    private val config: Config,
    args: Array<String>
) : CoroutineScope {
    companion object {
        var process: Process? = null
    }

    override val coroutineContext = Job() + Dispatchers.IO
    private var scannerJob: Job? = null

    init {
        println("----\nRunning MC Server command line: ${args.joinToString(" ")}\n")

        val process = ProcessBuilder(*args)
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        McServer.process = process

        val serverPollThread = if (config.autoStop)
            ServerPollThread(config, this::onShutdown)
        else null

        // flush all available input
        System.`in`.skip(System.`in`.available().toLong())
        // run a job to redirect stdin to subprocess stdin
        scannerJob = launch(Dispatchers.IO) {
            while (process.isAlive) {
                val char = System.`in`.read()
                if (process.isAlive) {
                    process.outputStream.write(char)
                    process.outputStream.flush()
                }
            }
        }

        // wait for the server to stop
        process?.waitFor()
        // cancel the redirecting job
        scannerJob?.cancel()
        // cancel the polling thread
        serverPollThread?.cancel()
    }

    private fun onShutdown() {
        scannerJob?.cancel()
        val process = McServer.process ?: return

        val command = "${config.autoStopCommand}\r\n".toByteArray()
        if (process.isAlive) {
            process.outputStream.write(command)
            process.outputStream.flush()
        }
    }
}
