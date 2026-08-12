package ui

import algebra.*

abstract class UiComponent {
    protected var _quadsCount = 0
    abstract fun draw(): Unit
}

abstract class Ui {
    protected val _componentsList: MutableList<UiComponent> = mutableListOf()

    fun add(item: UiComponent): Unit {
        item.draw()
        _componentsList.addLast(item)
    }

    fun add(items: List<UiComponent>): Unit {
        for (item in items) {
            add(item)
        }
    }

    abstract fun flush(): Unit
}