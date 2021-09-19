/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.tcp

import pl.szczodrzynski.mcstart.ext.readBytes
import pl.szczodrzynski.mcstart.ext.readVarInt
import pl.szczodrzynski.mcstart.ext.varLength
import pl.szczodrzynski.mcstart.packet.*
import java.io.InputStream
import java.net.SocketTimeoutException

object PacketParser {

    fun read(inputStream: InputStream): Packet {
        val firstByte = inputStream.read()
        // 0xFE - pre 1.7 Server List Ping
        // 0x02 - pre 1.7 Handshake (should be safe, as there's
        // no modern login packet of length that small)
        if (firstByte == 0xFE || firstByte == 0x02) {
            return readLegacy(inputStream, firstByte)
        }
        var length = inputStream.readVarInt(firstByte)
        val packetId = inputStream.readVarInt()
        length -= varLength(packetId)
        return buildPacket(
            packetId = packetId,
            isLegacy = false,
            data = inputStream.readBytes(length)
        )
    }

    fun readLegacy(inputStream: InputStream, firstByte: Int? = null): Packet {
        val packetId = firstByte ?: inputStream.read()
        var i = 0
        val buf = ByteArray(1024)
        try {
            while (i < 1024) {
                buf[i] = inputStream.read().toByte()
                i++ // not as buf[i++] not to increment on an exception
            }
        } catch (e: SocketTimeoutException) {
            // read timeout, ignore
        }
        return buildPacket(
            packetId = packetId,
            isLegacy = true,
            data = buf.copyOf(i)
        )
    }

    private fun buildPacket(packetId: Int, isLegacy: Boolean, data: ByteArray): Packet {
        val input = data.inputStream()
        return when (packetId) {
            0x00 -> when {
                data.size > 1 -> when {
                    data.first() == '{'.code.toByte()
                            && data.last() == '}'.code.toByte() -> ModernClientResponse.deserialize(input)
                    data.last() <= 0x02 -> ModernServerHandshake.deserialize(input)
                    else -> ModernServerLoginStart.deserialize(input)
                }
                else -> ModernServerRequest()
            }
            0x01 -> ModernServerPing.deserialize(input)
            0x02 -> when {
                // we're assuming the username and host string
                // to be shorter than 64 chars (128 bytes)
                data.first() == 0.toByte() -> LegacyServerHandshake13.deserialize(input)
                else -> LegacyServerHandshake16.deserialize(input)
            }
            0xFE -> when {
                data.size == 1 -> LegacyServerPing15()
                data.isEmpty() -> LegacyServerPing13()
                else -> LegacyServerPing16.deserialize(input)
            }
            0xFF -> when {
                data.copyOfRange(2, 6).toString(Charsets.UTF_16BE) == "§1" -> LegacyClientPong16.deserialize(input)
                data.contains('§'.code.toByte()) -> LegacyClientPong13.deserialize(input)
                else -> LegacyClientDisconnect.deserialize(input)
            }
            else -> UnknownPacket(packetId, isLegacy, data)
        }
    }
}
