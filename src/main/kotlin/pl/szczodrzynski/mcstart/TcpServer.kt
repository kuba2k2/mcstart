/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-27.
 */

package pl.szczodrzynski.mcstart

import com.github.mgrzeszczak.jsondsl.Json
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.config.Whitelist
import pl.szczodrzynski.mcstart.ext.convertFormat
import pl.szczodrzynski.mcstart.ext.log
import pl.szczodrzynski.mcstart.ext.writeString
import pl.szczodrzynski.mcstart.packet.Packet
import java.net.ServerSocket
import java.net.Socket

class TcpServer(
    private val config: Config,
    private val whitelist: Whitelist
) {

    private val server = ServerSocket(config.serverPort)

    init {
        println("----\nServer running on port ${server.localPort}")

        while (!server.isClosed) {
            try {
                val client = server.accept()
                ClientHandlerThread(config, client, this::onPlayerJoin)
            } catch (e: Exception) {
                log("!!! The server threw an Exception: $e")
            }
        }
    }

    private fun onPlayerJoin(client: Socket, nickname: String) {
        val output = mutableListOf<Byte>()

        val kickText = if (whitelist.allows(nickname)) {
            // should allow the player to start the server
            server.close()
            config.startingText
        }
        else {
            // player is not whitelisted
            config.disconnectText
        }

        // construct a "disconnect" packet
        val json = Json.obj {
            "text" to kickText
                .replace("\$USERNAME", nickname)
                .convertFormat()
        }
        output.writeString(json.toString())
        Packet.withData(0x00, output).write(client.outputStream)
        client.close()
    }
}
