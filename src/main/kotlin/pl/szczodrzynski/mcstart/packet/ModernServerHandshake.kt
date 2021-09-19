/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.*
import java.io.InputStream

class ModernServerHandshake(
    val protocolVersion: Int,
    val serverAddress: String,
    val serverPort: Int,
    val nextState: Int,
) : Packet(
    packetId = 0x00,
    isLegacy = false,
) {
    companion object {
        fun deserialize(input: InputStream) =
            ModernServerHandshake(
                protocolVersion = input.readVarInt(),
                serverAddress = input.readString(),
                serverPort = input.readNumber(2).toInt(),
                nextState = input.readVarInt(),
            )
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        output.writeVarInt(protocolVersion)
        output.writeString(serverAddress)
        output.writeNumber(2, serverPort.toLong())
        output.writeVarInt(nextState)

        return output.toByteArray()
    }

    override fun toString(): String {
        return "ModernServerHandshake(protocolVersion=$protocolVersion, addr='$serverAddress:$serverPort', nextState=$nextState)"
    }
}
