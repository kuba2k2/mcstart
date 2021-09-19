/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.readNumber
import pl.szczodrzynski.mcstart.ext.writeNumber
import java.io.InputStream

data class ModernServerPing(
    val payload: Long,
) : Packet(
    packetId = 0x01,
    isLegacy = false,
) {
    companion object {
        fun deserialize(input: InputStream) =
            ModernServerPing(
                payload = input.readNumber(8),
            )
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        output.writeNumber(8, payload)

        return output.toByteArray()
    }

    override fun toString(): String {
        return "ModernServerPing(payload=$payload)"
    }
}
