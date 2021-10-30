/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-19.
 */

package pl.szczodrzynski.mcstart.config

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.DotenvEntry
import io.github.cdimascio.dotenv.internal.DotenvParser
import io.github.cdimascio.dotenv.internal.DotenvReader

class Dotenv(
    directory: String = "./",
    filename: String = ".env",
) : Dotenv {

    private val envVars = mutableMapOf<String, String>()
    private val entries = mutableSetOf<DotenvEntry>()
    private val entriesInFile = mutableSetOf<DotenvEntry>()
    private val envVarsInFile = mutableMapOf<String, String>()

    init {
        val reader = DotenvParser(
            DotenvReader(directory, filename),
            false, false
        )
        val vars = reader.parse()

        envVarsInFile.putAll(vars.map { it.key to it.value })
        envVars.putAll(vars.map { it.key to it.value })
        envVars.putAll(System.getenv())

        entries.addAll(envVars.entries.map { DotenvEntry(it.key, it.value) })
        entriesInFile.addAll(envVars.entries.map { DotenvEntry(it.key, it.value) })
    }

    override fun entries() = entries
    override fun entries(filter: Dotenv.Filter?) = if (filter != null)
        entriesInFile
    else entries()

    override operator fun get(key: String) = System.getenv(key) ?: envVars[key]
    override fun get(key: String, defaultValue: String) = get(key) ?: defaultValue
}
