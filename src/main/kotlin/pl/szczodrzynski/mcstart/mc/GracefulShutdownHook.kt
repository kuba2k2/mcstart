/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-21.
 */

package pl.szczodrzynski.mcstart.mc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.debug
import pl.szczodrzynski.mcstart.ext.log
import pl.szczodrzynski.mcstart.ext.startCoroutineTimer

class GracefulShutdownHook(
    private val config: Config,
) : Thread(), CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO

    init {
        Runtime.getRuntime().addShutdownHook(this)
        if (config.debug) // avoid querying the lazies
            debug("GracefulShutdownHook added; isLinux = ${config.isLinux}, isDocker = ${config.isDocker}, isMcServerRunner = ${config.isMcServerRunner}")
    }

    override fun run() {
        log("Shutdown hook called")
        // a static instance is used, because apparently class properties
        // may not be available during shutdown (?)
        val server = McServer.INSTANCE ?: return

        if (!server.process.isAlive)
            return

        val (shouldStop, shouldKill) = if (config.gracefulShutdown) {
            debug("Shutting down gracefully, isMcServerRunner = ${config.isMcServerRunner}")
            if (config.isMcServerRunner) {
                server.terminate()
                true to false
            } else {
                server.stop()
                false to false
            }
        } else {
            debug("Terminating child process")
            server.terminate()
            false to true
        }

        if (config.gracefulShutdownTimeout > 0)
            scheduleTerminate(shouldStop, shouldKill)

        server.process.waitFor()
        log("Process finished")
    }

    private fun scheduleTerminate(shouldStop: Boolean, shouldKill: Boolean) {
        startCoroutineTimer(delayMillis = config.gracefulShutdownTimeout * 1000L) {
            val server = McServer.INSTANCE ?: return@startCoroutineTimer
            if (!server.process.isAlive)
                return@startCoroutineTimer
            // still alive?
            val type = when {
                shouldStop -> "'stop'"
                shouldKill -> "SIGKILL"
                else -> "SIGTERM"
            }
            debug("Still Alive after ${config.gracefulShutdownTimeout}s, trying $type")
            if (shouldStop) {
                // "stop" when mc-server-runner fails
                server.stop()
                scheduleTerminate(shouldStop = false, shouldKill = true) // kill, tried SIGTERM already
            } else if (shouldKill) {
                // SIGKILL, giving up here
                server.kill()
            } else {
                // just a SIGTERM
                server.terminate()
                scheduleTerminate(shouldStop = false, shouldKill = true)
            }
        }
    }
}
