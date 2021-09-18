/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-18.
 */

package pl.szczodrzynski.mcstart.config

import com.google.gson.JsonParser
import java.io.File

class Whitelist(private val config: Config) {

    private val players = mutableListOf<String>()

    init {
        if (config.whitelistEnabled) {
            var file = if (config.whitelistUseServer)
                // read default server whitelist
                File(config.serverPath, "whitelist.json")
            else
                // read user specified whitelist file, else default txt
                File(config.whitelistFile ?: "whitelist.txt")
            // if no server whitelist JSON, try to find older txt
            if (!file.exists() && config.whitelistUseServer) {
                file = File(config.serverPath, "white-list.txt")
            }
            // read the file, else do nothing (no players whitelisted)
            val fileType = file.extension.lowercase()
            if (file.exists() && fileType == "json") {
                readWhitelistJson(file)
            } else if (file.exists() && fileType == "txt") {
                readWhitelistTxt(file)
            }
        }
    }

    private fun readWhitelistJson(file: File) {
        val json = file.reader().use {
            JsonParser.parseReader(it)
        }
        if (json.isJsonArray) {
            players.addAll(json.asJsonArray.mapNotNull {
                it.asJsonObject.get("name")?.asString
            })
        }
    }

    private fun readWhitelistTxt(file: File) {
        players.addAll(file.readLines())
    }

    fun allows(playerName: String): Boolean {
        return !config.whitelistEnabled || players.isEmpty() || players.contains(playerName)
    }

    override fun toString(): String {
        return "Whitelist(players=$players)"
    }
}
