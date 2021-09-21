/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.tcp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.log
import java.net.ServerSocket

class TcpServer(
    config: Config,
) : CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO
    private val server = ServerSocket(config.serverPort)
    private var scannerJob: Job? = null

    init {
        println("----")
        log("Server running on port ${server.localPort}")

        scannerJob = launch(Dispatchers.IO) {
            while (!server.isClosed) {
                val command = readLine()
                if (command.isNullOrBlank())
                    continue
                when (command.lowercase()) {
                    "start" -> onServerClose("CONSOLE")
                    "exit", "stop" -> Runtime.getRuntime().exit(0)
                    else -> println("Unknown command: $command")
                }
            }
        }

        while (!server.isClosed) {
            try {
                val client = server.accept()
                ClientHandlerThread(config, client, this::onServerClose)
            } catch (e: Exception) {
                if (e.message?.contains("accept failed") != true && e.message?.contains("Socket closed") != true)
                    log("!!! The server threw an Exception: $e")
            }
        }
        scannerJob?.cancel()
    }

    private fun onServerClose(username: String) {
        log("Server started by '$username', shutting down TCP listener")
        server.close()
        scannerJob?.cancel()
    }
}
