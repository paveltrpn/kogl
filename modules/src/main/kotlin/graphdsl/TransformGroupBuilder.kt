package graphdsl

import algebra.*
import graph.*

class TransformGroupBuilder : NodeBuilder() {
    private var _offset = Vector3()
    private var _axis = Vector3()
    private var _angl = 0.0f
    private var _scale = Vector3()

    private var _transformGroup = TransformGroup()

    var offset: Vector3
        get(): Vector3 {
            return _offset
        }
        set(value) {
            _offset = value
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

    var scale: Vector3
        get(): Vector3 {
            return _scale
        }
        set(value) {
            _scale = value
        }

    fun offset(block: TransformGroup.() -> Unit): Unit {
        _transformGroup.matrix = algebra.offset(_offset)
        _transformGroup.apply(block)
    }

    fun rotate(block: TransformGroup.() -> Unit): Unit {
        _transformGroup.matrix = algebra.rotation(_axis, _angl)
        _transformGroup.apply(block)
    }

    fun scale(block: TransformGroup.() -> Unit): Unit {
        _transformGroup.matrix = algebra.scale(_scale)
        _transformGroup.apply(block)
    }

    fun get(): TransformGroup {
        return _transformGroup
    }
}

fun buildTransformGroup(block: TransformGroupBuilder.() -> Unit): TransformGroup {
    return TransformGroupBuilder().apply(block).get()
}


