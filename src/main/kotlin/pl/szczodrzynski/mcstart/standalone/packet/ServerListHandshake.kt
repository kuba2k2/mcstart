/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.standalone.packet

import pl.szczodrzynski.mcstart.standalone.Packet
import pl.szczodrzynski.mcstart.standalone.ext.*
import java.io.ByteArrayInputStream

class ServerListHandshake(
    packet: Packet,
    stream: ByteArrayInputStream = packet.data.inputStream()
) {

    val protocolVersion = stream.readVarInt()
    val serverAddress = stream.readString()
    val serverPort = stream.readNumber(2).toInt()
    val nextState = stream.readVarInt()

    init {
        log("ServerListHandshake(protocolVersion = $protocolVersion, addr = $serverAddress:$serverPort)")
    }
}
