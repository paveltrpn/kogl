package graph

import algebra.*
import render.*

// ============================================================================
// ======================= DebugDrawVisitor ===================================
// ============================================================================
class DebugDrawVisitor(
    delta: Float,
    viewMatrix: Matrix4,
    private val _info: DebugDrawInfo,
    program: Program
) : DrawCallVisitorBase(delta, viewMatrix, program) {
    companion object {
        private val _boxColor = Vector3(1.0f, 1.0f, 0.0f)
    }

    override fun apply(node: Leaf): Unit {
        when (node) {
            is DebugLightDrawable -> {
                for (light in _info.lights) {
                    with(mProgram) {
                        set("view_matrix" to mViewMatrix, false)
                        set("model_matrix" to light.modelMatrix, false)
                        set("color" to light.color)
                    }

                    node.draw()
                    mDrawCallsCount++
                }
            }

            is DebugAABBDrawable -> {
                for (box in _info.aabbs) {
                    with(mProgram) {
                        set("view_matrix" to mViewMatrix, false)
                        set("model_matrix" to box.modelMatrix, false)
                        set("color" to _boxColor)
                    }

                    node.draw()
                    mDrawCallsCount++
                }
            }
        }
    }
}
