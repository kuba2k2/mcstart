/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.readString
import pl.szczodrzynski.mcstart.ext.writeString
import java.io.InputStream

class ModernServerLoginStart(
    val username: String,
) : Packet(
    packetId = 0x00,
    isLegacy = false,
) {
    companion object {
        fun deserialize(input: InputStream) =
            ModernServerLoginStart(
                username = input.readString(),
            )
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        output.writeString(username)

        return output.toByteArray()
    }

    override fun toString(): String {
        return "ModernServerLoginStart(username='$username')"
    }
}
