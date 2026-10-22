package graph

import algebra.*
import render.*

// ============================================================================
// ======================= DrawCallVisitorBase ================================
// ============================================================================

abstract class DrawCallVisitorBase(delta: Float, viewMatrix: Matrix4) : TransformVisitor() {
    protected val mDelta: Float
    protected var mViewMatrix: Matrix4
    protected var mProgram: Program = Program()
    protected var mDrawCallsCount = 0

    init {
        mDelta = delta
        mViewMatrix = viewMatrix
    }

    val drawCallsCount: Int
        get(): Int {
            return mDrawCallsCount
        }

    override fun apply(node: Switch): Unit {
        node.traverse(this)
    }

    override fun apply(node: StateGroup): Unit {
        mProgram = node.program

        mProgram.use()

        node.traverse(this)
    }

    override fun apply(node: Group): Unit {
        node.traverse(this)
    }
}
