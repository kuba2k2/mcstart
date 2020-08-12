/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-27.
 */

package pl.szczodrzynski.mcstart.plugin

class PluginConfig {

    var autoStopEnabled = true // whether to stop the server if all players left
    var autoStopTimeout = 5 * 60 // 5 minutes
    var startTimeout = true // whether to start the autoStopTimeout on server start
    var startTimeoutDelay = 60 // delay the timeout on server start
}
