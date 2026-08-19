package graph

import algebra.*

class Locale(origin: Vector3) {
    private val _origin: Vector3

    private val _graph: Group = Group()

    init {
        _origin = origin
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
}
