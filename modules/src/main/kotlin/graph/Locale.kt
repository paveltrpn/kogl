package graph

import algebra.*

class Locale {
    private var _origin: Vector3

    private val _graph: TransformGroup = TransformGroup()

    constructor() {
        _origin = Vector3()
        _graph.matrix = matrix
    }

    constructor(origin: Vector3) {
        _origin = origin
        _graph.matrix = matrix
    }

    var origin: Vector3
        get(): Vector3 {
            return _origin
        }
        set(value) {
            _origin = value
            _graph.matrix = matrix
        }

    val matrix: Matrix4
        get(): Matrix4 {
            return algebra.offset(_origin)
        }

    val root: Group
        get(): Group {
            return _graph
        }

    fun addStateGroup(node: StateGroup): Unit {
        _graph.addChild(node)
    }

    fun addStateGroups(nodes: List<StateGroup>): Unit {
        for (node in nodes) {
            _graph.addChild(node)
        }
    }

    val isEmpty: Boolean
        get(): Boolean {
            return _graph.cildrenCount == 0
        }
}
