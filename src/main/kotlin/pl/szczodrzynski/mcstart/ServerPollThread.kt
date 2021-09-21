/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-18.
 */

package pl.szczodrzynski.mcstart

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.debug
import pl.szczodrzynski.mcstart.ext.log
import pl.szczodrzynski.mcstart.ext.startCoroutineTimer
import pl.szczodrzynski.mcstart.packet.LegacyClientPong13
import pl.szczodrzynski.mcstart.packet.LegacyClientPong16
import pl.szczodrzynski.mcstart.packet.LegacyServerPing13
import pl.szczodrzynski.mcstart.packet.ModernClientResponse
import pl.szczodrzynski.mcstart.tcp.PacketParser
import java.net.Socket

class ServerPollThread(
    private val config: Config,
    private val onShutdown: () -> Unit
) : CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO
    private var timerJob: Job? = null
    private var shutdownJob: Job? = null

    private val packet = LegacyServerPing13()

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
            debug("Checking server status")
            if (!isServerEmpty()) {
                shutdownJob?.cancel()
                shutdownJob = null
                return@startCoroutineTimer
            }

            debug("Server empty, scheduling shutdown in ${config.autoStopTimeout} seconds")
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
            val socket = Socket(config.autoStopServer, config.autoStopPort)

            packet.write(socket)

            while (socket.isConnected && !socket.isClosed && !socket.isInputShutdown) {
                val inputStream = socket.inputStream
                if (inputStream.available() > 0) {
                    val response = PacketParser.readLegacy(inputStream)
                    debug("<-- $response")
                    val currentPlayers = when (response) {
                        is ModernClientResponse -> response.data
                            .get("players")
                            .asJsonObject
                            .get("online")
                            .asInt
                        is LegacyClientPong16 -> response.playersOnline
                        is LegacyClientPong13 -> response.playersOnline
                        // do not stop server on an error
                        else -> 1
                    }
                    return currentPlayers == 0
                }
            }
        } catch (e: Exception) {
            debug("Status check failed: $e")
        }
        return false
    }

    private fun shutdown() {
        if (!isServerEmpty()) {
            debug("Shutdown called but server not empty, aborting.")
            scheduleTimer()
            return
        }

        log("Shutting down the server.")
        onShutdown()
        cancel()
    }
}
