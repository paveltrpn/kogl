package graph

import org.lwjgl.opengl.GL46.*

import algebra.*
import render.*

// ============================================================================
// ======================= GridDrawVisitor ====================================
// ============================================================================

class GridDrawVisitor(delta: Float, val cameraPos: Vector3, viewMatrix: Matrix4) :
    DrawCallVisitorBase(delta, viewMatrix) {
    override fun apply(node: Leaf): Unit {
        when (node) {
            is Grid -> {
                with(mProgram) {
                    set("view_matrix" to mViewMatrix, false)
                    set("model_matrix" to Matrix4(), false)

                    set("scale" to 512.0f)
                    set("maxRange" to 256.0f)
                    set("yOffset" to 0.0f)

                    //set("gridSize" to 1.0f)
                    this assign value("gridSize" to 1.0f)

                    set("majorDivisor" to 5.0f)

                    set("lineThickness" to 0.0256f)
                    set("zoomSensitivity" to 0.5f)

                    this assign value("colorMajor" to Vector3(0.7f, 0.1f, 0.1f))
                    this assign value("colorMinor" to Vector3(0.1f, 0.1f, 0.5f))

                    this assign value("cameraPos" to cameraPos)
                }

                glEnable(GL_BLEND)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                node.draw()
                glDisable(GL_BLEND)

                mDrawCallsCount++
            }
        }
    }
}
