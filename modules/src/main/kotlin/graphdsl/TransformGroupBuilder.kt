package graphdsl

import algebra.*
import graph.*

class TransformGroupBuilder : NodeBuilder() {
    private var _transformGroup: TransformGroup? = null

    fun offset(offset: Vector3, block: TransformGroup.() -> Unit): Unit {
        _transformGroup = TransformGroup()
        _transformGroup?.matrix = algebra.offset(offset)
        _transformGroup?.apply(block)
    }

    fun rotate(axis: Vector3, angl: Float, block: TransformGroup.() -> Unit): Unit {
        _transformGroup = TransformGroup()
        _transformGroup?.matrix = algebra.rotation(axis, angl)
        _transformGroup?.apply(block)
    }

    fun scale(scale: Vector3, block: TransformGroup.() -> Unit): Unit {
        _transformGroup = TransformGroup()
        _transformGroup?.matrix = algebra.scale(scale)
        _transformGroup?.apply(block)
    }

    fun get(): TransformGroup {
        return _transformGroup ?: throw RuntimeException("TransformGroup not initialized!")
    }

    override fun attach(node: Node) {
        TODO("Not yet implemented")
    }
}

fun buildTransformGroup(block: TransformGroupBuilder.() -> Unit): TransformGroup {
    return TransformGroupBuilder().apply(block).get()
}


