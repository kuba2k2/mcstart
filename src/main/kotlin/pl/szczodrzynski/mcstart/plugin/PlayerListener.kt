package pl.szczodrzynski.mcstart.plugin

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitTask

class PlayerListener(val plugin: Plugin) : Listener {

    private var task: BukkitTask? = null

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        if (plugin.pluginConfig.startTimeout && plugin.server.onlinePlayers.isEmpty()) {
            val timeout = plugin.pluginConfig.autoStopTimeout + plugin.pluginConfig.startTimeoutDelay
            plugin.logger.warning("Server will stop in $timeout seconds if no player joins")
            task = ShutdownTask(plugin).runTaskLater(plugin, timeout * 20L)
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        // cancel the pending shutdown (if any)
        if (task != null) {
            plugin.logger.info("Cancelling pending shutdown task...")
            task?.cancel()
            task = null
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        if ((plugin.server.onlinePlayers - event.player).isEmpty()) {
            plugin.logger.warning("All players left, scheduling server shutdown...")
            task?.cancel()
            val timeout = plugin.pluginConfig.autoStopTimeout
            task = ShutdownTask(plugin).runTaskLater(plugin, timeout * 20L)
        }
    }
}
