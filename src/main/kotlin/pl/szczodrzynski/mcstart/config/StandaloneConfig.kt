/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.config

class StandaloneConfig {

    var serverPort = 25565 // should be equal to the server port
    var socketTimeout = 5000L // timeout for client connections (recommended)

    var protocolVersion = 1 // if equal to your MC client version, it will show player count (below)
    var versionName = "Stopped" // if protocol version differs from the client, this text will be shown
    var playersOnline = 0
    var playersMax = 0

    var motd = "The server is stopped, join to start."
    var startingText = "Hi \$USERNAME, the server is starting..."
    var disconnectText = "You are not whitelisted to start this server."

    var whitelistEnabled = true
    var whitelistUseServer = true
    var whitelist = listOf<String>()
}
