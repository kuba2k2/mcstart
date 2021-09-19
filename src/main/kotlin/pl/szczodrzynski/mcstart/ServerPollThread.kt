/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-18.
 */

package pl.szczodrzynski.mcstart

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.*
import java.net.Socket

class ServerPollThread(
    private val config: Config,
    private val onShutdown: () -> Unit
) : CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO
    private var timerJob: Job? = null
    private var shutdownJob: Job? = null

    private val packet = byteArrayOf(0xFE.b)

    init {
        scheduleTimer()
    }

    private fun scheduleTimer() {
        shutdownJob?.cancel()
        shutdownJob = null

        timerJob = startCoroutineTimer(
            delayMillis = config.autoStopPollingDelay * 1000L,
            repeatMillis = config.autoStopPollingInterval * 1000L
        ) {
            log("Checking server status")
            if (!isServerEmpty()) {
                shutdownJob?.cancel()
                shutdownJob = null
                return@startCoroutineTimer
            }

            log("Server empty, scheduling shutdown in ${config.autoStopTimeout} seconds")
            scheduleShutdown()
        }
    }

    private fun scheduleShutdown() {
        timerJob?.cancel()
        timerJob = null
        shutdownJob = startCoroutineTimer(
            delayMillis = config.autoStopTimeout * 1000L
        ) {
            shutdown()
        }
    }

    private fun isServerEmpty(): Boolean {
        try {
            val socket = Socket(config.autoStopServer, config.serverPort)

            socket.outputStream.write(packet)

            while (socket.isConnected && !socket.isClosed && !socket.isInputShutdown) {
                val inputStream = socket.inputStream
                if (inputStream.available() > 0) {
                    val id = inputStream.readNumber(1).toInt()
                    val length = inputStream.readNumber(2).toInt()
                    val data = inputStream.readBytes(length * 2)
                    val dataString = String(data, Charsets.UTF_16BE)
                    val items = dataString
                        .reversed()
                        .split('§')
                        .map { it.reversed() }
                    val maxPlayers = items[0].toIntOrNull()
                    val currentPlayers = items[1].toIntOrNull()

                    socket.close()
                    if (currentPlayers == 0)
                        return true
                }
            }
        } catch (e: Exception) {
            log("Status check failed: $e")
        }
        return false
    }

    private fun shutdown() {
        if (!isServerEmpty()) {
            log("Shutdown called but server not empty, aborting.")
            scheduleTimer()
            return
        }

        log("Shutting down the server.")
        onShutdown()
        cancel()
    }
}
