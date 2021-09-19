/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.tcp

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.log
import pl.szczodrzynski.mcstart.packet.*
import java.net.Socket

class PacketHandlerLegacy(
    private val config: Config,
    private val client: Socket,
    private val packet: Packet,
    private val onServerClose: (username: String) -> Unit
) {

    init {
        handlePacket()
    }

    /*
        Legacy Server List Ping
            1. C->S: Ping(FE 01 FA ...)     MC 1.6
            1. C->S: Ping(FE 01)            MC 1.4-1.5
            1. C->S: Ping(FE)               MC <=1.3
            2. S->C: Disconnect(glue 00h)   MC 1.4-1.6  implemented as Pong16
            2. S->C: Disconnect(glue §)     MC <=1.3    implemented as Pong13

        Legacy Login
            1. C->S: Handshake(protocol, username, host, port)  MC 1.4-1.6
            1. C->S: Handshake(username;host:port)              MC <=1.3
            2. S->C: Disconnect(reason)
     */

    private fun handlePacket() = when (packet) {
        is LegacyServerPing16 -> {
            // use client's protocol version for matchProtocol
            LegacyClientPong16.buildUsing(config, packet.protocolVersion).write(client)
            client.close()
        }
        is LegacyServerPing15 -> {
            // use protocol 60 (1.5) for matchProtocol
            LegacyClientPong16.buildUsing(config, 60).write(client)
            client.close()
        }
        is LegacyServerPing13 -> {
            // no protocol version in the response
            LegacyClientPong13.buildUsing(config).write(client)
            client.close()
        }
        is LegacyServerHandshake16 -> {
            val reason = handleHandshake(packet.username)
            LegacyClientDisconnect(reason).write(client)
            client.close()
        }
        is LegacyServerHandshake13 -> {
            val reason = handleHandshake(packet.username)
            LegacyClientDisconnect(reason).write(client)
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
