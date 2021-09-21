/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.DEBUG
import pl.szczodrzynski.mcstart.ext.log
import pl.szczodrzynski.mcstart.tcp.TcpServer

fun main(args: Array<String>) {

    println("MCStart version $Version")
    val config = Config()
    if (config.debug) {
        DEBUG = true
        println("Loaded configuration: $config")
        println("Loaded whitelist: ${config.whitelist}")
        println("Server command line: ${args.joinToString(" ")}")
    }

    if (args.isEmpty()) {
        throw IllegalArgumentException("Server command line is missing")
    }

    // wait for MC server to exit gracefully on SIGTERM
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            log("Shutdown hook called")
            McServer.process?.waitFor()
            log("Process finished")
        }
    })

    while (true) {
        TcpServer(config) // wait for a privileged player to join
        println("----\nTCP server exited")
        McServer(config, args) // start the Minecraft server
    }
}
