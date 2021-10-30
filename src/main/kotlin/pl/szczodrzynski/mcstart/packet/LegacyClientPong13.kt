/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.*
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
            // reverse split with limit
            val items = data
                .split('§')
                .reversed()
                .joinToString("§")
                .split('§', limit = 3)
            val motdText = items[2]
                .split('§')
                .reversed()
                .joinToString("§")
            return LegacyClientPong13(
                motdText = motdText,
                playersOnline = items[1].toIntOrNull() ?: 0,
                playersMax = items[0].toIntOrNull() ?: 0,
            )
        }

        fun buildUsing(config: Config) =
            LegacyClientPong13(
                motdText = config.motdText.normalize().stripFormat().nl2ws(),
                playersOnline = config.playersOnline,
                playersMax = config.playersMax.coerceAtLeast(1),
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
