/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.ext

import java.io.InputStream

fun InputStream.readBytes(num: Int): ByteArray {
    val buf = ByteArray(num)
    val numRead = read(buf)
    return buf.copyOf(numRead)
}

fun InputStream.readString(): String {
    val length = readVarInt()
    return String(readBytes(length))
}

fun InputStream.readNumber(length: Int): Long {
    var number: Long = 0
    for (i in 0 until length) {
        number = number shl 8 or (read().toLong() and 255)
    }
    return number
}

fun InputStream.readVarInt(firstByte: Int? = null): Int {
    var numRead = 0
    var result = 0
    var read: Int
    do {
        read = if (numRead == 0 && firstByte != null)
            firstByte
        else
            read()
        val value: Int = read and 127
        result = result or (value shl 7 * numRead)
        numRead++
        if (numRead > 5) {
            throw RuntimeException("VarInt is too big")
        }
    } while (read and 128 != 0)

    return result
}

fun InputStream.readVarLong(): Int {
    var numRead = 0
    var result = 0L
    var read: Int
    do {
        read = read()
        val value: Long = (read and 127).toLong()
        result = result or (value shl 7 * numRead)
        numRead++
        if (numRead > 10) {
            throw RuntimeException("VarLong is too big")
        }
    } while (read and 128 != 0)

    return result.toInt()
}
