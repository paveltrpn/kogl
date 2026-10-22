package graph

import algebra.Matrix4
import render.Program

// ============================================================================
// ======================= TransformVisitor ===================================
// ============================================================================

abstract class TransformVisitor() : Visitor() {
    protected var mModelMatrixStack = MatrixStack()

    init {
    }

    override fun apply(node: TransformGroup): Unit {
        with(mModelMatrixStack) {
            push(node)
            node.traverse(this@TransformVisitor)
            pop()
        }
    }
}