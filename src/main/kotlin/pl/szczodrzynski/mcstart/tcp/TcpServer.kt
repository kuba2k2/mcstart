/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.tcp

import pl.szczodrzynski.mcstart.Console
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.log
import java.net.ServerSocket

class TcpServer(
    config: Config,
) {

    private val server = ServerSocket(config.serverPort)
    private var serverClosed = false
    private val clients = mutableListOf<ClientHandlerThread>()

    init {
        println("----")
        log("Server running on port ${server.localPort}")

        // flush all available input
        System.`in`.skip(System.`in`.available().toLong())

        // handle commands on stdin
        Console.subscribe(this) { command ->
            if (command.isBlank())
                return@subscribe
            when (command.lowercase()) {
                "start" -> onServerClose("CONSOLE")
                "exit", "stop" -> Runtime.getRuntime().exit(0)
                else -> println("Unknown command: $command")
            }
        }

        // wait for the TCP server to close
        while (!server.isClosed) {
            try {
                val socket = server.accept()
                val client = ClientHandlerThread(
                    config = config,
                    client = socket,
                    onServerClose = this::onServerClose,
                    onClose = {
                        // remove the closed client
                        clients.remove(it)
                    },
                )
                // store the client for cancelling
                clients.add(client)
            } catch (e: Exception) {
                if (!serverClosed)
                    log("!!! The server threw an Exception: $e")
            }
        }

        // close all sockets
        cleanupJobs()
        // unsubscribe from input
        Console.unsubscribe(this)
    }

    private fun cleanupJobs() {
        // close all client sockets
        clients.onEach { it.close() }
        // stop the TCP server
        serverClosed = true
        server.close()
    }

    private fun onServerClose(username: String) {
        log("Server started by '$username', shutting down TCP listener")
        // close all sockets
        cleanupJobs()
    }
}
