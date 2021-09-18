/*
 * Copyright (c) Kuba Szczodrzyński 2020-7-24.
 */

package pl.szczodrzynski.mcstart.packet

import com.github.mgrzeszczak.jsondsl.Json
import pl.szczodrzynski.mcstart.config.Config
import pl.szczodrzynski.mcstart.ext.convertFormat
import pl.szczodrzynski.mcstart.ext.log
import pl.szczodrzynski.mcstart.ext.writeString
import java.net.Socket

class ServerListRequest(
    config: Config,
    client: Socket
) {

    init {
        log("ServerListRequest()")
        val output = mutableListOf<Byte>()

        val json = Json.obj {
            "version" to obj {
                "name" to config.versionName.convertFormat()
                "protocol" to config.versionProtocol
            }
            "players" to obj {
                "max" to config.playersMax
                "online" to config.playersOnline
                "sample" to array()
            }
            "description" to obj {
                "text" to config.motdText.convertFormat()
                "mcstart" to "mcstart" // to make MCStart detection easier (e.g. with mcstatus python lib)
            }
        }
        output.writeString(json.toString())
        Packet.withData(0x00, output).write(client.outputStream)
    }
}
