/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.standalone.ext

import sun.misc.IOUtils.readNBytes
import java.io.InputStream

fun InputStream.readString(): String {
    val length = readVarInt()
    return String(readNBytes(this, length))
}

fun InputStream.readNumber(length: Int): Long {
    var number: Long = 0
    for (i in 0 until length) {
        number = number shl 8 or (read().toLong() and 255)
    }
    return number
}

fun InputStream.readVarInt(): Int {
    var numRead = 0
    var result = 0
    var read: Int
    do {
        read = read()
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
