/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.log
import pl.szczodrzynski.mcstart.ext.readNumber
import pl.szczodrzynski.mcstart.ext.readString
import pl.szczodrzynski.mcstart.ext.readVarInt
import java.io.ByteArrayInputStream

class ServerListHandshake(
    packet: Packet,
    stream: ByteArrayInputStream = packet.data.inputStream()
) {

    private val protocolVersion = stream.readVarInt()
    private val serverAddress = stream.readString()
    private val serverPort = stream.readNumber(2).toInt()
    private val nextState = stream.readVarInt()

    init {
        log("ServerListHandshake(protocolVersion = $protocolVersion, addr = $serverAddress:$serverPort, nextState = $nextState)")
    }
}
