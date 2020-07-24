/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.standalone.packet

import pl.szczodrzynski.mcstart.standalone.Packet
import pl.szczodrzynski.mcstart.standalone.ext.*
import java.io.ByteArrayInputStream
import java.net.Socket

class ServerListPing(
    client: Socket,
    packet: Packet,
    stream: ByteArrayInputStream = packet.data.inputStream()
) {

    val payload = stream.readNumber(8)

    init {
        log("ServerListPing(payload = $payload)")
        val output = mutableListOf<Byte>()

        output.writeNumber(8, payload)
        Packet.withData(0x01, output).write(client.outputStream)
        client.close()
    }
}
