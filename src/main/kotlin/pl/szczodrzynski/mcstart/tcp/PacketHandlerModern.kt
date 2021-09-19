/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.tcp

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.log
import pl.szczodrzynski.mcstart.packet.*
import java.net.Socket

class PacketHandlerModern(
    private val config: Config,
    private val client: Socket,
    private val handler: ClientHandlerThread,
    private val packet: Packet,
    private val onServerClose: (username: String) -> Unit
) {

    init {
        handlePacket()
    }

    /*
        Modern Server List Ping
            1. C->S: Handshake(nextState = 1)
            2. C->S: Request()
            3. S->C: Response(JSON String)
            4. C->S: Ping(payload)
            5. S->C: Pong(payload)
            6. Connection closed

        Modern Login
            1. C->S: Handshake(nextState = 2)
            2. C->S: LoginStart(username)
            3. S->C: Disconnect(reason)
            4. Connection closed
     */

    private fun handlePacket() = when (packet) {
        // a normal handshake packet (containing 0x01 or 0x02 as the "next state")
        is ModernServerHandshake -> {
            // save client's protocol version for later
            handler.modernProtocolVersion = packet.protocolVersion
        }
        // the client requests the server info
        is ModernServerRequest -> {
            // use client's protocol version for matchProtocol
            ModernClientResponse.buildUsing(config, handler.modernProtocolVersion).write(client)
        }
        // just a ping
        is ModernServerPing -> {
            packet.write(client)
            client.close()
        }
        // the client wants to login
        // should kick the player already here
        is ModernServerLoginStart -> {
            val reason = handleHandshake(packet.username)
            ModernClientDisconnect(reason).write(client)
            client.close()
        }
        else -> log("Unknown packet received: $packet")
    }

    private fun handleHandshake(username: String): String {
        val isAllowed = config.whitelist.allows(username)

        var reason = if (isAllowed)
            config.startingText
        else
            config.disconnectText
        reason = reason.replace("\$USERNAME", username)

        if (isAllowed)
            onServerClose(username)
        return reason
    }
}
