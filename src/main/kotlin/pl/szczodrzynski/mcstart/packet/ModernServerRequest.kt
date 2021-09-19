/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

class ModernServerRequest : Packet(
    packetId = 0x00,
    isLegacy = false
) {

    override fun serialize(): ByteArray {
        return byteArrayOf()
    }

    override fun toString(): String {
        return "ModernServerRequest()"
    }
}
