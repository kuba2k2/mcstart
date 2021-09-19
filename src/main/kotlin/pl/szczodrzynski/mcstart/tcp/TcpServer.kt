/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.tcp

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.log
import java.net.ServerSocket

class TcpServer(
    config: Config,
) {

    private val server = ServerSocket(config.serverPort)

    init {
        println("----")
        log("Server running on port ${server.localPort}")

        while (!server.isClosed) {
            try {
                val client = server.accept()
                ClientHandlerThread(config, client, this::onServerClose)
            } catch (e: Exception) {
                log("!!! The server threw an Exception: $e")
            }
        }
    }

    private fun onServerClose(username: String) {
        log("Server started by '$username', shutting down TCP listener")
        server.close()
    }
}
