/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.tcp

import kotlinx.coroutines.*
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.log
import java.net.Socket

class ClientHandlerThread(
    private val config: Config,
    private val client: Socket,
    private val onServerClose: (username: String) -> Unit
) : CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO

    private var clientIsLegacy = false

    init {
        launch(Dispatchers.IO) {
            withTimeout(config.socketTimeout) {

                log("Socket opened - ${client.inetAddress.hostAddress}.")

                while (isActive && client.isConnected && !client.isClosed && !client.isInputShutdown) {
                    if (client.inputStream.available() > 0) {
                        readPacket()
                    }
                }
            }
            log("Socket closed.")
            client.close()
            cancel()
        }
    }

    private fun readPacket() {
        client.soTimeout = 150

        val packet = if (clientIsLegacy)
            PacketParser.readLegacy(client.inputStream)
        else
            PacketParser.read(client.inputStream)

        log("<-- $packet")

        if (packet.isLegacy) {
            clientIsLegacy = true
            PacketHandlerLegacy(config, client, packet, onServerClose)
        }
        else {
            PacketHandlerModern(config, client, packet, onServerClose)
        }
    }
}
