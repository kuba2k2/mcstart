/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-27.
 */

package pl.szczodrzynski.mcstart.mc

import kotlinx.coroutines.cancel
import pl.szczodrzynski.mcstart.Console
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.debug
import pl.szczodrzynski.mcstart.ext.getPid
import pl.szczodrzynski.mcstart.ext.log
import java.io.PrintStream

class McServer(
    private val config: Config,
    args: Array<String>
) {
    companion object {
        var INSTANCE: McServer? = null
    }

    private val serverPollThread: ServerPollThread?

    val process: Process
    private val processStdin: PrintStream

    init {
        INSTANCE = this
        println("----")
        log("Running MC Server command line: ${args.joinToString(" ")}")

        process = ProcessBuilder(*args)
            .redirectInput(ProcessBuilder.Redirect.PIPE)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        processStdin = PrintStream(process.outputStream)

        debug("Created PID ${process.getPid()}")

        serverPollThread = if (config.autoStop)
            ServerPollThread(config, this::stop)
        else null

        // redirect stdin to subprocess
        Console.subscribe(this) { input ->
            if (process.isAlive) {
                processStdin.println(input)
                processStdin.flush()
            }
        }

        // wait for the server to stop
        process?.waitFor()
        // cancel the polling thread
        serverPollThread?.cancel()
        // unlink the current instance
        INSTANCE = null
        // unsubscribe from input
        Console.unsubscribe(this)
    }

    fun stop() {
        // cancel the polling thread
        serverPollThread?.cancel()

        debug("Running ${config.shutdownCommand}")
        if (process.isAlive) {
            processStdin.println(config.shutdownCommand)
            processStdin.flush()
        }
    }

    fun terminate() = kill(forcibly = false)

    fun kill(forcibly: Boolean = true) {
        val pid = process.getPid()
        if (pid == -1L) {
            if (forcibly)
                process.destroyForcibly()
            else
                process.destroy()
            return
        }

        // cancel the polling thread
        serverPollThread?.cancel()

        val command = if (config.isLinux) {
            val sig = if (forcibly) "KILL" else "TERM"
            "kill -$sig $pid"
        } else {
            val flag = if (forcibly) "/f" else ""
            "taskkill $flag /pid $pid"
        }

        debug("Running $command")
        Runtime.getRuntime().exec(command)
    }
}
