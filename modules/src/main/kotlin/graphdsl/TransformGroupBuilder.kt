package graphdsl

import algebra.*
import graph.*

@DslMarker
annotation class TransformGroupBuilderDslMarker

@TransformGroupBuilderDslMarker
class TransformGroupBuilder : NodeBuilder() {
    private var _offset = Vector3()
    private var _scale = Vector3()
    private var _axis = Vector3(0.0f, 0.0f, 1.0f)
    private var _angl = 0.0f

    private var _transformGroup: TransformGroup? = null

    var offset: Vector3
        get(): Vector3 {
            return _offset
        }
        set(value) {
            _offset = value
        }

    var scale: Vector3
        get(): Vector3 {
            return _scale
        }
        set(value) {
            _scale = value
        }

    var axis: Vector3
        get(): Vector3 {
            return _axis
        }
        set(value) {
            _axis = value
        }

    var angl: Float
        get(): Float {
            return _angl
        }
        set(value) {
            _angl = value
        }


    fun offset(block: TransformGroup.() -> Unit): Unit {
        _transformGroup = TransformGroup().apply {
            block()
            matrix = algebra.offset(offset)
        }
    }

    fun rotate(block: TransformGroup.() -> Unit): Unit {
        _transformGroup = TransformGroup().apply {
            block()
            matrix = algebra.rotation(axis, angl)
        }
    }

    fun scale(block: TransformGroup.() -> Unit): Unit {
        _transformGroup = TransformGroup().apply {
            block()
            matrix = algebra.scale(scale)
        }
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


