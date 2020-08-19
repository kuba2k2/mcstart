/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.standalone

import kotlinx.coroutines.*
import pl.szczodrzynski.mcstart.standalone.ext.log
import pl.szczodrzynski.mcstart.standalone.packet.ServerListHandshake
import pl.szczodrzynski.mcstart.standalone.packet.ServerListNickname
import pl.szczodrzynski.mcstart.standalone.packet.ServerListPing
import pl.szczodrzynski.mcstart.standalone.packet.ServerListRequest
import java.net.Socket

class ClientHandlerThread(
        val config: StandaloneConfig,
        val client: Socket,
        val onPlayerJoin: (client: Socket, nickname: String) -> Unit
) : CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO

    init {
        launch(Dispatchers.IO) {
            withTimeout(config.socketTimeout) {

                log("Socket opened - ${client.inetAddress.hostAddress}.")
                val inputStream = client.inputStream

                while (isActive && client.isConnected && !client.isClosed && !client.isInputShutdown) {
                    if (inputStream.available() > 0) {
                        handlePacket()
                    }
                }
            }
            log("Socket closed.")
            client.close()
            cancel()
        }
    }

    private fun handlePacket() {
        val packet = Packet.readFromSocket(client)
        when (packet.packetId) {
            0x00 -> {
                if (packet.length > 1) {
                    if (packet.data.last() <= 0x02) {
                        // a normal handshake packet (containing 0x01 or 0x02 as the "next state")
                        ServerListHandshake(packet)
                    }
                    else {
                        // probably a packet with just the player's nickname
                        // should kick the player already here
                        ServerListNickname(client, packet, onPlayerJoin)
                    }
                }
                else {
                    ServerListRequest(config, client)
                }
            }
            0x01 -> {
                ServerListPing(client, packet)
            }
            else -> {
                log("Unknown packet received: 0x${packet.packetId.toString(16)} (${packet.length} B)")
            }
        }
    }
}
