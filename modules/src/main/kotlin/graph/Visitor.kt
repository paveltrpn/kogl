package  graph

import algebra.rotation

interface Visitor {
    fun apply(node: Node) = Unit
    fun apply(node: Drawable) = Unit
    fun apply(node: StateGroup) = Unit
    fun apply(node: Transform) = Unit
}

class SpinVisitor : Visitor {
    override fun apply(node: Drawable): Unit {
        when (node) {
            is SpinableDrawable -> {
//                node._angl += node._anglSpeed
//
//                if (_angl > 360.0f || _angl < -360.0f) _angl = 0.0f
//
//                val spin = rotation(_axis, _angl)
//
//                // Why transpose?
//                tr.transpose()
//
//                _combined = spin.multiply(tr)
            }
        }
    }
}
