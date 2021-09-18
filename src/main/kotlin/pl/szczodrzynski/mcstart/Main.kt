/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart

import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.config.Whitelist

fun main(args: Array<String>) {
    val config = Config()
    val whitelist = Whitelist(config)
    println(config)
    println(whitelist)

    println("Server command line: "+args.joinToString(" "))

    while (true) {
        TcpServer(config, whitelist) // wait for a privileged player to join
        McServer(args.joinToString(" ")) // start the Minecraft server
    }
}
