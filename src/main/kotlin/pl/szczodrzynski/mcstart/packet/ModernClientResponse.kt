/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import com.github.mgrzeszczak.jsondsl.Json
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import pl.szczodrzynski.mcstart.Version
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.convertFormat
import pl.szczodrzynski.mcstart.ext.writeString
import java.io.InputStream

class ModernClientResponse(
    val data: JsonObject,
) : Packet(
    packetId = 0x00,
    isLegacy = false,
) {
    companion object {
        fun deserialize(input: InputStream) =
            ModernClientResponse(
                data = JsonParser
                    .parseReader(input.reader())
                    .asJsonObject,
            )

        fun buildUsing(config: Config, protocol: Int? = null) =
            ModernClientResponse(
                data = Json.obj {
                    "version" to obj {
                        "name" to config.versionName.convertFormat()
                        "protocol" to if (config.matchProtocolModern)
                            protocol ?: config.versionProtocol
                        else
                            config.versionProtocol
                    }
                    "players" to obj {
                        "max" to config.playersMax
                        "online" to config.playersOnline
                        "sample" to array()
                    }
                    "description" to obj {
                        "text" to config.motdText.convertFormat()
                        "mcstart" to Version.CODE // to make MCStart detection easier (e.g. with mcstatus python lib)
                    }
                }
            )
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        output.writeString(data.toString())

        return output.toByteArray()
    }

    override fun toString(): String {
        return "ModernClientResponse(data=$data)"
    }
}
