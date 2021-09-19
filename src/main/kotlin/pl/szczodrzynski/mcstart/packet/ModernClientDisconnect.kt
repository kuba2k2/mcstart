/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import com.github.mgrzeszczak.jsondsl.Json
import com.google.gson.JsonParser
import pl.szczodrzynski.mcstart.ext.convertFormat
import pl.szczodrzynski.mcstart.ext.writeString
import java.io.InputStream

class ModernClientDisconnect(
    val reason: String,
) : Packet(
    packetId = 0x00,
    isLegacy = false,
) {
    companion object {
        fun deserialize(input: InputStream) =
            ModernClientDisconnect(
                reason = JsonParser
                    .parseReader(input.reader())
                    .asJsonObject
                    .get("text")
                    .asString,
            )
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        val json = Json.obj {
            "text" to reason.convertFormat()
        }
        output.writeString(json.toString())

        return output.toByteArray()
    }

    override fun toString(): String {
        return "ModernClientDisconnect(reason='$reason')"
    }
}
