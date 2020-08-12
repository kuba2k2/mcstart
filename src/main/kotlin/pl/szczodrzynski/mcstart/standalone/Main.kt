/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.standalone

import com.google.gson.JsonParser
import pl.szczodrzynski.mcstart.Config
import java.io.File

fun main(args: Array<String>) {
    val config = Config.INSTANCE.standalone
    if (config.whitelistEnabled && config.whitelistUseServer) {
        try {
            config.whitelist = JsonParser.parseString(
                File(Config.PATH, "../whitelist.json").readText()
            ).asJsonArray.mapNotNull {
                it.asJsonObject?.get("name")?.asString
            }
        }
        catch (e: Exception) {
            // TODO better error handling
        }
    }

    println("Server command line: "+args.joinToString(" "))

    while (true) {
        TcpServer() // wait for a privileged player join
        McServer(args.joinToString(" ")) // start the Minecraft server
    }
}
