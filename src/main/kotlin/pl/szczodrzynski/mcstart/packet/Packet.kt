/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

@file:Suppress("EXPERIMENTAL_API_USAGE")

package pl.szczodrzynski.mcstart.packet

import pl.szczodrzynski.mcstart.ext.debug
import pl.szczodrzynski.mcstart.ext.varLength
import pl.szczodrzynski.mcstart.ext.writeVarInt
import java.io.OutputStream
import java.net.Socket

abstract class Packet(
    val packetId: Int,
    val isLegacy: Boolean,
) {

    abstract fun serialize(): ByteArray

    fun write(client: Socket) = write(client.outputStream)

    private fun write(outputStream: OutputStream) {
        debug("--> $this")
        if (isLegacy) {
            writeLegacy(outputStream)
            return
        }
        val data = serialize()
        outputStream.writeVarInt(varLength(packetId) + data.size)
        outputStream.writeVarInt(packetId)
        outputStream.write(data)
    }

    private fun writeLegacy(outputStream: OutputStream) {
        val data = serialize()
        outputStream.write(packetId)
        outputStream.write(data)
    }
}
