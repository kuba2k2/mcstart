/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.text.Normalizer
import java.util.*

var DEBUG = false

fun log(msg: String, label: String = "") {
    println("[${Thread.currentThread().name}$label] $msg")
}

fun debug(msg: String) = if (DEBUG) log(msg, "/D") else Unit

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
fun String.unconvertFormat(): String = replace('§', '&')

fun String.stripFormat(): String = this
    .unconvertFormat()
    .replace("&[0-9A-z]".toRegex(), "")

fun String.nl2ws() = replace('\n', ' ')

fun String.normalize(): String {
    val text = this
        .unconvertFormat()
        .replace("ł", "l")
        .replace("Ł", "L")
    return Normalizer.normalize(text, Normalizer.Form.NFKD)
        .replace("[^\\p{ASCII}]".toRegex(), "")
        .convertFormat()
}

inline val Int.b get() = this.toByte()

fun CoroutineScope.startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, action: suspend () -> Unit) =
    launch {
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

fun Any.readField(name: String) = try {
    val f = javaClass.getDeclaredField(name)
    f.isAccessible = true
    val v = f.get(this)
    f.isAccessible = false
    v
} catch (e: Exception) {
    null
}

fun Any.callSuperMethod(name: String) = try {
    javaClass.superclass.getMethod(name).invoke(this)
} catch (e: Exception) {
    null
}

@Synchronized
fun Process.getPid(): Long {
    val pid = callSuperMethod(name = "pid")     /* Java 9+ */
        ?: readField(name = "pid")              /* Java <=8, Unix */
    return (pid as? Long) ?: (pid as? Int)?.toLong() ?: -1L
}

fun File.asBase64(): String {
    val mimeType = Files.probeContentType(this.toPath())
    val data = this.readBytes()
    val base64 = Base64.getEncoder().encodeToString(data)
    return "data:$mimeType;base64,$base64"
}
