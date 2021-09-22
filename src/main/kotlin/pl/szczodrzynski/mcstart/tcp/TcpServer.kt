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

    init {
        println("----")
        log("Server running on port ${server.localPort}")

        // handle commands on stdin
        Console.subscribe(this) { command ->
            if (command.isBlank())
                return@subscribe
            when (command.lowercase()) {
                "start" -> onServerClose("CONSOLE")
                "exit", "stop" -> Runtime.getRuntime().exit(0)
                "help" -> println("""
                    Available commands:
                        start - run the MC server manually
                        exit - quit MC start
                        stop - same as exit
                """.trimIndent())
                else -> println("Unknown command: $command")
            }
        }

        // wait for the TCP server to close
        while (!server.isClosed) {
            try {
                val socket = server.accept()
                ClientHandlerThread(
                    config = config,
                    client = socket,
                    onServerClose = this::onServerClose,
                )
            } catch (e: Exception) {
                if (!serverClosed)
                    log("!!! The server threw an Exception: $e")
            }
        }

        // stop the server
        stopServer()
        // unsubscribe from input
        Console.unsubscribe(this)
    }

    private fun stopServer() {
        // stop the TCP server
        serverClosed = true
        server.close()
    }

    private fun onServerClose(username: String) {
        log("Server started by '$username', shutting down TCP listener")
        // stop the server
        stopServer()
    }
}
