/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.*
import java.io.InputStream

class LegacyServerPing16(
    val channel: String,
    val protocolVersion: Int,
    val serverAddress: String,
    val serverPort: Int,
) : Packet(
    packetId = 0xFE,
    isLegacy = true,
) {
    companion object {
        fun deserialize(input: InputStream): LegacyServerPing16 {
            input.readBytes(2) // skip 01 FA
            val channel = input.readStringLegacy()
            input.readBytes(2) // skip data length
            val protocolVersion = input.readNumber(1).toInt()
            val serverAddress = input.readStringLegacy()
            val serverPort = input.readNumber(4).toInt()
            return LegacyServerPing16(
                channel = channel,
                protocolVersion = protocolVersion,
                serverAddress = serverAddress,
                serverPort = serverPort,
            )
        }
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        output.writeBytes(0x01, 0xFA)
        output.writeStringLegacy(channel)
        output.writeNumber(2, 7 + serverAddress.length * 2L)
        output.writeNumber(1, protocolVersion.toLong())
        output.writeStringLegacy(serverAddress)
        output.writeNumber(4, serverPort.toLong())

        return output.toByteArray()
    }

    override fun toString(): String {
        return "LegacyServerPing16(channel='$channel', protocolVersion=$protocolVersion, serverAddress='$serverAddress', serverPort=$serverPort)"
    }
}
