/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.toHex

class UnknownPacket(
    packetId: Int,
    isLegacy: Boolean,
    val data: ByteArray,
) : Packet(
    packetId = packetId,
    isLegacy = isLegacy,
) {

    override fun serialize(): ByteArray {
        return data
    }

    override fun toString(): String {
        return "UnknownPacket(packetId=0x${packetId.toString(16)}, isLegacy=$isLegacy, length=${data.size}, data='${data.toHex()}')"
    }
}
