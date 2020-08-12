package pl.szczodrzynski.mcstart.plugin

import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {

    lateinit var pluginConfig: PluginConfig

    override fun onEnable() {
        saveDefaultConfig()

        val config = this.config.getConfigurationSection("plugin")
        pluginConfig = PluginConfig()
        config?.let {
            pluginConfig.autoStopEnabled = it.getBoolean("autoStopEnabled")
            pluginConfig.autoStopTimeout = it.getInt("autoStopTimeout")
            pluginConfig.startTimeout = it.getBoolean("startTimeout")
            pluginConfig.startTimeoutDelay = it.getInt("startTimeoutDelay")
        }

        if (pluginConfig.autoStopEnabled) {
            PlayerListener(this) // start listening to player join/quit events
        }
    }
}
