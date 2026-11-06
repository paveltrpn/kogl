package graph

import algebra.*
import render.*

// ============================================================================
// ======================= DrawCallVisitorBase ================================
// ============================================================================

abstract class DrawCallVisitorBase(
    frameTime: FrameTime,
    viewMatrix: Matrix4,
    program: Program = Program()
) : TransformVisitor() {
    protected val mFrameTime: FrameTime
    protected var mViewMatrix: Matrix4
    protected var mProgram: Program = program
    protected var mDrawCallsCount = 0

    init {
        mFrameTime = frameTime
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
