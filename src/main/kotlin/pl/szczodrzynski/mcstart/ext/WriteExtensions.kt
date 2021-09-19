/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.ext

import java.io.OutputStream

fun MutableList<Byte>.writeBytes(vararg bytes: Int) {
    addAll(bytes.map { it.toByte() })
}

fun MutableList<Byte>.writeString(string: String) {
    writeVarInt(string.toByteArray().size)
    string.toByteArray().forEach {
        add(it)
    }
}

fun MutableList<Byte>.writeStringLegacy(string: String) {
    writeNumber(2, string.length.toLong())
    string.toByteArray(Charsets.UTF_16BE).forEach {
        add(it)
    }
}

fun MutableList<Byte>.writeNumber(length: Int, number: Long) {
    for (i in length-1 downTo 0) {
        add((number ushr i*8 and 255).toByte())
    }
}

fun OutputStream.writeVarInt(number: Int) {
    var value = number
    do {
        var temp = value and 127
        // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
        value = value ushr 7
        if (value != 0) {
            temp = temp or 128
        }
        write(temp)
    } while (value != 0)
}
fun MutableList<Byte>.writeVarInt(number: Int) {
    var value = number
    do {
        var temp = value and 127
        // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
        value = value ushr 7
        if (value != 0) {
            temp = temp or 128
        }
        add(temp.toByte())
    } while (value != 0)
}

fun MutableList<Byte>.writeVarLong(number: Long) {
    var value = number
    do {
        var temp = (value and 127).toInt()
        // Note: >>> means that the sign bit is shifted with the rest of the number rather than being left alone
        value = value ushr 7
        if (value != 0L) {
            temp = temp or 128
        }
        add(temp.toByte())
    } while (value != 0L)
}
