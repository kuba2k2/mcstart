/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.*
import java.io.ByteArrayInputStream
import java.net.Socket

class ServerListNickname(
    client: Socket,
    packet: Packet,
    onPlayerJoin: (client: Socket, nickname: String) -> Unit,
    stream: ByteArrayInputStream = packet.data.inputStream()
) {

    val nickname = stream.readString()

    init {
        log("ServerListNickname(nickname = $nickname)")
        onPlayerJoin(client, nickname)
    }
}
