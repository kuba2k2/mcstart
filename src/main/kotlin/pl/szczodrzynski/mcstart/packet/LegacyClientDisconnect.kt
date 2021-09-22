/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.convertFormat
import pl.szczodrzynski.mcstart.ext.nl2ws
import pl.szczodrzynski.mcstart.ext.readStringLegacy
import pl.szczodrzynski.mcstart.ext.writeStringLegacy
import java.io.InputStream

class LegacyClientDisconnect(
    val reason: String,
) : Packet(
    packetId = 0xFF,
    isLegacy = true,
) {
    companion object {
        fun deserialize(input: InputStream): LegacyClientDisconnect {
            val reason = input.readStringLegacy()
            return LegacyClientDisconnect(
                reason = reason,
            )
        }

        fun with(reason: String, stripNewline: Boolean = false) = LegacyClientDisconnect(
            if (stripNewline) reason.convertFormat().nl2ws()
            else reason.convertFormat()
        )
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        output.writeStringLegacy(reason)

        return output.toByteArray()
    }

    override fun toString(): String {
        return "LegacyClientDisconnect(reason='$reason')"
    }
}
