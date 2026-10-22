package userinterface

import algebra.*

// ============================================================================
// ======================= UiComponent ========================================
// ============================================================================
abstract class UiComponent {
    protected var mQuadsCount = 0
    abstract fun draw(): Unit
}

// ============================================================================
// ======================= Ui =================================================
// ============================================================================
abstract class Ui {
    protected val mComponentsList: MutableList<UiComponent> = mutableListOf()

    fun add(item: UiComponent): Unit {
        item.draw()
        mComponentsList.addLast(item)
    }

    fun add(items: List<UiComponent>): Unit {
        for (item in items) {
            add(item)
        }
    }

    abstract fun flushAndDraw(): Unit
}