package pl.szczodrzynski.mcstart.plugin

import org.bukkit.scheduler.BukkitRunnable

class ShutdownTask(val plugin: Plugin) : BukkitRunnable() {

    override fun run() {
        // additional check for no players
        if (plugin.server.onlinePlayers.isEmpty()) {
            plugin.logger.info("Stopping server after timeout...")
            plugin.server.shutdown()
        }
    }
}
