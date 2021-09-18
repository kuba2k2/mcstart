/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart

import kotlinx.coroutines.*
import pl.szczodrzynski.mcstart.ClientHandlerThread.LegacyPacketType.*
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.*
import pl.szczodrzynski.mcstart.packet.*
import java.net.Socket
import java.net.SocketTimeoutException

class ClientHandlerThread(
    private val config: Config,
    private val client: Socket,
    private val onPlayerJoin: (client: Socket, nickname: String) -> Unit
) : CoroutineScope {

    enum class LegacyPacketType {
        LEGACY_1_6,
        LEGACY_1_5,
        LEGACY_1_3,
    }

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
        // TODO: 2021-09-18 consider using .mark() and .reset() for the first byte
        val firstByte = client.inputStream.read()
        if (firstByte == 0xFE) {
            handleLegacyPacket()
            return
        }
        var length = client.inputStream.readVarInt(firstByte)
        val packetId = client.inputStream.readVarInt()
        length -= varLength(packetId)
        val packet = Packet(
            packetId,
            length,
            client.inputStream.readBytes(length)
        )

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

    private fun handleLegacyPacket() {
        var type = LEGACY_1_3

        client.soTimeout = 150
        val inp = client.inputStream
        try {
            inp.read() // 0x01
            inp.read() // 0xFA
            type = LEGACY_1_5
            var length = inp.readNumber(2).toInt()
            inp.readBytes(length * 2)
            length = inp.readNumber(2).toInt()
            inp.readBytes(length)
            type = LEGACY_1_6
        } catch (e: SocketTimeoutException) {
            // read timeout, ignore
        }
        println("Legacy packet, type $type")
        client.close()
    }
}
