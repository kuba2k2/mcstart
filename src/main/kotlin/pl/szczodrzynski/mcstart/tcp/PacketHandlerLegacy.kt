/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.tcp

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.packet.Packet
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

    enum class LegacyPacketType {
        LEGACY_1_6,
        LEGACY_1_5,
        LEGACY_1_3,
    }

    private fun handlePacket() {
        /*val inputStream = client.inputStream

        var type = LEGACY_1_3

        try {
            inputStream.read() // 0x01
            inputStream.read() // 0xFA
            type = LEGACY_1_5
            var length = inputStream.readNumber(2).toInt()
            inputStream.readBytes(length * 2)
            length = inputStream.readNumber(2).toInt()
            inputStream.readBytes(length)
            type = LEGACY_1_6
        } catch (e: SocketTimeoutException) {
            // read timeout, ignore
        }
        println("Legacy packet, type $type")*/
        client.close()
    }
}
