/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

class LegacyServerPing13 : Packet(
    packetId = 0xFE,
    isLegacy = true,
) {

    override fun serialize(): ByteArray {
        return byteArrayOf()
    }

    override fun toString(): String {
        return "LegacyServerPing13()"
    }
}
