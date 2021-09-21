/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.DEBUG
import pl.szczodrzynski.mcstart.mc.GracefulShutdownHook
import pl.szczodrzynski.mcstart.mc.McServer
import pl.szczodrzynski.mcstart.tcp.TcpServer

fun main(args: Array<String>) {

    println("MCStart version $Version")
    val config = Config()
    if (config.debug) {
        DEBUG = true
        println("Loaded configuration: $config".replace("\n", "\\n"))
        println("Loaded whitelist: ${config.whitelist}")
        println("Server command line: ${args.joinToString(" ")}")
    }

    if (args.isEmpty()) {
        throw IllegalArgumentException("Server command line is missing")
    }

    // wait for MC server to exit gracefully on SIGTERM
    GracefulShutdownHook(config)

    while (true) {
        TcpServer(config) // wait for a privileged player to join
        println("----\nTCP server exited")
        McServer(config, args) // start the Minecraft server
    }
}
