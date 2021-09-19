/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.convertFormat
import pl.szczodrzynski.mcstart.ext.readStringLegacy
import pl.szczodrzynski.mcstart.ext.writeStringLegacy
import java.io.InputStream

class LegacyClientPong13(
    val motdText: String,
    val playersOnline: Int,
    val playersMax: Int,
) : Packet(
    packetId = 0xFF,
    isLegacy = true,
) {
    companion object {
        fun deserialize(input: InputStream): LegacyClientPong13 {
            val data = input.readStringLegacy()
            val items = data.split('§')
            return LegacyClientPong13(
                motdText = items[0],
                playersOnline = items[1].toIntOrNull() ?: 0,
                playersMax = items[2].toIntOrNull() ?: 0,
            )
        }

        fun buildUsing(config: Config) =
            LegacyClientPong13(
                motdText = config.motdText.convertFormat(),
                playersOnline = config.playersOnline,
                playersMax = config.playersMax,
            )
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        val items = listOf(
            motdText,
            playersOnline.toString(),
            playersMax.toString(),
        )
        val data = items.joinToString("§")
        output.writeStringLegacy(data)

        return output.toByteArray()
    }

    override fun toString(): String {
        return "LegacyClientPong13(motdText='$motdText', playersOnline=$playersOnline, playersMax=$playersMax)"
    }
}
