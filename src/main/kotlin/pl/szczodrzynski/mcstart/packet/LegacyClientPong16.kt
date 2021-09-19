/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.convertFormat
import pl.szczodrzynski.mcstart.ext.readStringLegacy
import pl.szczodrzynski.mcstart.ext.writeStringLegacy
import java.io.InputStream

class LegacyClientPong16(
    val protocolVersion: Int,
    val versionName: String,
    val motdText: String,
    val playersOnline: Int,
    val playersMax: Int,
) : Packet(
    packetId = 0xFF,
    isLegacy = true,
) {
    companion object {
        fun deserialize(input: InputStream): LegacyClientPong16 {
            val data = input.readStringLegacy()
            val items = data.split(Char(0))
            return LegacyClientPong16(
                protocolVersion = items[1].toIntOrNull() ?: 0,
                versionName = items[2],
                motdText = items[3],
                playersOnline = items[4].toIntOrNull() ?: 0,
                playersMax = items[5].toIntOrNull() ?: 0,
            )
        }

        fun buildUsing(config: Config) =
            LegacyClientPong16(
                protocolVersion = config.versionProtocol,
                versionName = config.versionName.convertFormat(),
                motdText = config.motdText.convertFormat(),
                playersOnline = config.playersOnline,
                playersMax = config.playersMax,
            )
    }

    override fun serialize(): ByteArray {
        val output = mutableListOf<Byte>()

        val items = listOf(
            "§1",
            protocolVersion.toString(),
            versionName,
            motdText,
            playersOnline.toString(),
            playersMax.toString(),
        )
        val data = items.joinToString(String(byteArrayOf(0)))
        output.writeStringLegacy(data)

        return output.toByteArray()
    }

    override fun toString(): String {
        return "LegacyClientPong16(protocolVersion=$protocolVersion, versionName='$versionName', motdText='$motdText', playersOnline=$playersOnline, playersMax=$playersMax)"
    }
}
