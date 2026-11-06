package kogl

import events.*

fun main(args: Array<String>): Unit {
    println("kogl start...")
    val eventBus = EventBus()
    val window: Window = Window(eventBus)
    window.run()
}
