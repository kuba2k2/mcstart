/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-18.
 */

package pl.szczodrzynski.mcstart.config

import io.github.cdimascio.dotenv.dotenv

class Config {
    private val envMapping = mapOf(
        "SERVER_PORT" to "server-port",
        "ENABLE_QUERY" to "enable-query",
        "QUERY_PORT" to "query.port",
        "MOTD" to "motd",
        "MAX_PLAYERS" to "max-players",
        "ENABLE_WHITELIST" to "white-list",
    )

    private val dotenv = dotenv {
        ignoreIfMissing = true
    }

    val serverPath = dotenv["SERVER_PATH"] ?: "."

    private val properties = dotenv {
        directory = serverPath
        filename = "server.properties"
        ignoreIfMissing = true
    }

    val serverPort = getInt("SERVER_PORT") ?: 25565
    val queryPort = getInt("QUERY_PORT") ?: 25565

    private val enableQueryServer = getBoolean("ENABLE_QUERY") ?: true
    val enableQuery = enableQueryServer && getBoolean("MCS_ENABLE_QUERY") ?: true

    val socketTimeout = getLong("MCS_SOCKET_TIMEOUT") ?: 5000L
    val versionProtocol = getInt("MCS_VERSION_PROTOCOL") ?: 1
    val versionName = getString("MCS_VERSION_NAME") ?: "Stopped"

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
        return "Config(serverPath='$serverPath', serverPort=$serverPort, queryPort=$queryPort, enableQuery=$enableQuery, socketTimeout=$socketTimeout, versionProtocol=$versionProtocol, versionName='$versionName', playersOnline=$playersOnline, playersMax=$playersMax, motdText='$motdText', startingText='$startingText', disconnectText='$disconnectText', whitelistEnabled=$whitelistEnabled, whitelistUseServer=$whitelistUseServer)"
    }
}
