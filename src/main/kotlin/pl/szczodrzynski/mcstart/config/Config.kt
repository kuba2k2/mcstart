/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-18.
 */

package pl.szczodrzynski.mcstart.config

class Config {
    private val envMapping = mapOf(
        "SERVER_PORT" to "server-port",
        "ENABLE_QUERY" to "enable-query",
        "MOTD" to "motd",
        "MAX_PLAYERS" to "max-players",
        "ENABLE_WHITELIST" to "white-list",
    )

    private val dotenv = Dotenv()

    val serverPath = dotenv["SERVER_PATH"] ?: "."

    private val properties = Dotenv(serverPath, "server.properties")

    val serverPort = getInt("SERVER_PORT") ?: 25565

    val socketTimeout = getLong("MCS_SOCKET_TIMEOUT") ?: 5000L
    val matchProtocolModern = getBoolean("MCS_MATCH_PROTOCOL_MODERN") ?: false
    val matchProtocolLegacy = getBoolean("MCS_MATCH_PROTOCOL_LEGACY") ?: true
    val versionProtocol = getInt("MCS_VERSION_PROTOCOL") ?: 1
    val versionName = getString("MCS_VERSION_NAME") ?: "Stopped"

    val autoStop = getBoolean("MCS_AUTO_STOP") ?: false
    val autoStopServer = getString("MCS_AUTO_STOP_HOSTNAME") ?: "localhost"
    val autoStopPollingDelay = getInt("MCS_AUTO_STOP_POLLING_DELAY") ?: 60
    val autoStopPollingInterval = getInt("MCS_AUTO_STOP_POLLING_INTERVAL") ?: 10
    val autoStopTimeout = getInt("MCS_AUTO_STOP_TIMEOUT") ?: (10 * 60)
    val autoStopCommand = getString("MCS_AUTO_STOP_COMMAND") ?: "stop"

    val playersOnline = getInt("MCS_PLAYERS_ONLINE") ?: 0
    val playersMax = getInt("MCS_PLAYERS_MAX")
        ?: getInt("MAX_PLAYERS")
        ?: 0

    val motdText = getString("MCS_MSG_MOTD")
        ?: getString("MOTD")
        ?: "The server is stopped, join to start."
    val startingText = getString("MCS_MSG_STARTING")
        ?: "Hi \$USERNAME, the server is starting..."
    val disconnectText = getString("MCS_MSG_NOT_WHITELISTED")
        ?: "You are not whitelisted to start this server."

    val whitelistEnabled = getBoolean("MCS_WHITELIST")
        ?: getBoolean("ENABLE_WHITELIST")
        ?: false
    val whitelistUseServer = getBoolean("MCS_WHITELIST_SERVER")
        ?: getBoolean("ENABLE_WHITELIST")
        ?: whitelistEnabled
    val whitelistFile = getString("MCS_WHITELIST_FILE")

    val whitelist = Whitelist(this)

    private fun getString(name: String): String? {
        val property = envMapping[name]
        val value = if (property != null)
            properties[property] ?: dotenv[name]
        else
            dotenv[name] ?: null
        return value?.replace("\\n", "\n")
    }

    private fun getInt(name: String) = getString(name)?.toIntOrNull()
    private fun getLong(name: String) = getString(name)?.toLongOrNull()
    private fun getBoolean(name: String) = getString(name)?.toBoolean()

    override fun toString(): String {
        return "Config(serverPath='$serverPath', serverPort=$serverPort, socketTimeout=$socketTimeout, versionProtocol=$versionProtocol, versionName='$versionName', autoStop=$autoStop, autoStopServer='$autoStopServer', autoStopPollingDelay=$autoStopPollingDelay, autoStopPollingInterval=$autoStopPollingInterval, autoStopTimeout=$autoStopTimeout, autoStopCommand='$autoStopCommand', playersOnline=$playersOnline, playersMax=$playersMax, motdText='$motdText', startingText='$startingText', disconnectText='$disconnectText', whitelistEnabled=$whitelistEnabled, whitelistUseServer=$whitelistUseServer, whitelistFile=$whitelistFile)"
    }
}
