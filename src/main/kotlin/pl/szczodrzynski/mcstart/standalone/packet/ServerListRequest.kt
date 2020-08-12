/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.standalone.packet

import com.github.mgrzeszczak.jsondsl.Json
import pl.szczodrzynski.mcstart.standalone.StandaloneConfig
import pl.szczodrzynski.mcstart.standalone.Packet
import pl.szczodrzynski.mcstart.standalone.ext.log
import pl.szczodrzynski.mcstart.standalone.ext.writeString
import java.net.Socket

class ServerListRequest(
    config: StandaloneConfig,
    client: Socket
) {

    init {
        log("ServerListRequest()")
        val output = mutableListOf<Byte>()

        val json = Json.obj {
            "version" to obj {
                "name" to config.versionName
                "protocol" to config.protocolVersion
            }
            "players" to obj {
                "max" to config.playersMax
                "online" to config.playersOnline
                "sample" to array()
            }
            "description" to obj {
                "text" to config.motd
                "mcstart" to "mcstart" // to make MCStart detection easier (e.g. with mcstatus python lib)
            }
        }
        output.writeString(json.toString())
        Packet.withData(0x00, output).write(client.outputStream)
    }
}
