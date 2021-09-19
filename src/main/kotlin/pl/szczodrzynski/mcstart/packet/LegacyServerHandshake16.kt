/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.readNumber
import pl.szczodrzynski.mcstart.ext.readStringLegacy
import pl.szczodrzynski.mcstart.ext.writeNumber
import pl.szczodrzynski.mcstart.ext.writeStringLegacy
import java.io.InputStream

class LegacyServerHandshake16(
    val protocolVersion: Int,
    val username: String,
    val serverAddress: String,
    val serverPort: Int,
) : Packet(
    packetId = 0x02,
    isLegacy = true,
) {
    companion object {
        fun deserialize(input: InputStream): LegacyServerHandshake16 {
            val protocolVersion = input.readNumber(1).toInt()
            val username = input.readStringLegacy()
            val serverAddress = input.readStringLegacy()
            val serverPort = input.readNumber(4).toInt()
            return LegacyServerHandshake16(
                protocolVersion = protocolVersion,
                username = username,
                serverAddress = serverAddress,
                serverPort = serverPort,
            )
        }
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        output.writeNumber(1, protocolVersion.toLong())
        output.writeStringLegacy(username)
        output.writeStringLegacy(serverAddress)
        output.writeNumber(4, serverPort.toLong())

        return output.toByteArray()
    }

    override fun toString(): String {
        return "LegacyServerHandshake16(protocolVersion=$protocolVersion, username='$username', serverAddress='$serverAddress', serverPort=$serverPort)"
    }
}
