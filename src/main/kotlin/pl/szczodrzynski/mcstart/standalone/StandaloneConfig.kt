/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.standalone

class StandaloneConfig {

    var serverPort = 25565

    var protocolVersion = 210
    var versionName = "1.10.2"
    var playersOnline = 100
    var playersMax = -10

    var motd = "A MCStart sample server"
    var startingText = "Hi \$USERNAME, the server is now starting..."
    var disconnectText = "You've been disconnected, as this server is running MCStart"

    var whitelistEnabled = true
    var whitelistUseServer = true
    var whitelist = listOf<String>()
}
