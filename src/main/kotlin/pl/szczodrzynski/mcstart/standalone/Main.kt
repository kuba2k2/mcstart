/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.standalone

import java.net.ServerSocket

fun main(args: Array<String>) {
    val server = ServerSocket(25565)
    println("Server running on port ${server.localPort}")
    val config = Config()
    while (true) {
        val client = server.accept()
        ServerThread(config, client).start()
    }
}
