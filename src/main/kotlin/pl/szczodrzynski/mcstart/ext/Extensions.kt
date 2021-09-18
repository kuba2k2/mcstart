/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.ext

fun log(msg: String) {
    println("[${Thread.currentThread().name}] $msg")
}

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
