/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-27.
 */

package pl.szczodrzynski.mcstart

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import pl.szczodrzynski.mcstart.plugin.PluginConfig
import pl.szczodrzynski.mcstart.standalone.StandaloneConfig
import java.io.File

class Config {
    companion object {
        /**
         * The JAR path
         */
        var PATH = File(Config::class.java.protectionDomain.codeSource.location.toURI()).parentFile

        val INSTANCE by lazy {
            val yaml = Yaml(Constructor(Config::class.java))
            val file = File(PATH, "MCStart/config.yml")

            if (!file.exists()) {
                File(file.parent).mkdirs()
                val config = Config()
                file.writeText(yaml.dump(config))
                config
            }
            else {
                yaml.load<Config>(file.reader()) ?: Config()
            }
        }
    }

    var standalone = StandaloneConfig()
    var plugin = PluginConfig()
}
