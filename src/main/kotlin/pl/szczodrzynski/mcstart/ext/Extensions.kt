/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var DEBUG = false

fun log(msg: String) {
    println("[${Thread.currentThread().name}] $msg")
}

fun debug(msg: String) = if (DEBUG) log(msg) else Unit

fun varLength(number: Int): Int {
    var numWritten = 0
    var value = number
    do {
        value = value ushr 7
        numWritten++
    } while (value != 0)
    return numWritten
}

fun String.convertFormat(): String = replace('&', '§')

inline val Int.b get() = this.toByte()

fun CoroutineScope.startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, action: suspend () -> Unit) = launch {
    delay(delayMillis)
    if (repeatMillis > 0) {
        while (true) {
            action()
            delay(repeatMillis)
        }
    } else {
        action()
    }
}

fun ByteArray.toHex() = joinToString(" ") { "%02x".format(it) }
