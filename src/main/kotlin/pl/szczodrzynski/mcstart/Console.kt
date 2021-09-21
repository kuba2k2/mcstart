/*
 * Copyright (c) Kuba Szczodrzyński 2021-9-21.
 */

package pl.szczodrzynski.mcstart

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/* ah yes, apparently this is the easiest workaround. */
object Console : CoroutineScope {

    override val coroutineContext = Job() + Dispatchers.IO
    private val subscribers = mutableMapOf<Any, (line: String) -> Unit>()

    init {
        val scanner = Scanner(System.`in`)
        launch(Dispatchers.IO) {
            while (true) {
                val line = scanner.nextLine()
                broadcast(line)
            }
        }
    }

    private fun broadcast(line: String) {
        subscribers.forEach { (_, block) ->
            block(line)
        }
    }

    fun subscribe(key: Any, block: (line: String) -> Unit) {
        subscribers[key] = block
    }

    fun unsubscribe(key: Any) {
        subscribers.remove(key)
    }
}
