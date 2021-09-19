/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.readStringLegacy
import pl.szczodrzynski.mcstart.ext.writeStringLegacy
import java.io.InputStream

class LegacyServerHandshake13(
    val username: String,
    val serverAddress: String,
    val serverPort: Int,
) : Packet(
    packetId = 0x02,
    isLegacy = true,
) {
    companion object {
        fun deserialize(input: InputStream): LegacyServerHandshake13 {
            val data = input.readStringLegacy()
            val username = data
                .substringBefore(';')
            val serverAddress = data
                .substringAfter(';')
                .substringBefore(':')
            val serverPort = data
                .substringAfter(':')
                .toIntOrNull() ?: 25565
            return LegacyServerHandshake13(
                username = username,
                serverAddress = serverAddress,
                serverPort = serverPort,
            )
        }
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        output.writeStringLegacy("$username;$serverAddress:$serverPort")

        return output.toByteArray()
    }

    override fun toString(): String {
        return "LegacyServerHandshake13(username='$username', serverAddress='$serverAddress', serverPort=$serverPort)"
    }
}
