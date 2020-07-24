/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.standalone.packet

import com.github.mgrzeszczak.jsondsl.Json
import pl.szczodrzynski.mcstart.standalone.Config
import pl.szczodrzynski.mcstart.standalone.Packet
import pl.szczodrzynski.mcstart.standalone.ext.*
import java.io.ByteArrayInputStream
import java.net.Socket

class ServerListNickname(
    config: Config,
    client: Socket,
    packet: Packet,
    stream: ByteArrayInputStream = packet.data.inputStream()
) {

    val nickname = stream.readString()

    init {
        log("ServerListNickname(nickname = $nickname)")
        val output = mutableListOf<Byte>()

        // construct a "disconnect" packet
        val json = Json.obj {
            "text" to config.disconnectText
        }
        output.writeString(json.toString())
        Packet.withData(0x00, output).write(client.outputStream)
        client.close()
    }
}
