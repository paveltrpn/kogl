package graph

import algebra.*
import render.*

// ============================================================================
// ======================= DrawableVisitor ====================================
// ============================================================================

class DrawableVisitor(
    delta: Float,
    viewMatrix: Matrix4,
    val lights: List<OmniLightData> = emptyList()
) : DrawCallVisitorBase(delta, viewMatrix) {
    companion object {
        private const val MAX_LIGHTS = 16
    }

    override fun apply(node: StateGroup): Unit {
        mProgram = node.program

        mProgram.use()

        if (mProgram.hasUniform("lightsCount")) {
            val count = minOf(lights.size, MAX_LIGHTS)

            mProgram.set("lightsCount" to count)

            for (i in 0 until count) {
                val light = lights[i]

                mProgram.set("lights[$i].pos" to light.position)
                mProgram.set("lights[$i].color" to light.color)
                mProgram.set("lights[$i].intensity" to light.intensity)
                mProgram.set("lights[$i].radius" to light.radius)
            }
        }

        node.traverse(this)
    }

    override fun apply(node: Leaf): Unit {
        when (node) {
            is Drawable -> {
                when (node) {
                    is SpinableDrawable -> {
                        val local = node.updateLocal(mDelta)

                        val top = mModelMatrixStack.top.copy()
                        top.transpose()

                        node.applyTransform(local.multiply(top))
                    }

                    is FlyaroundDrawable -> {
                        val local = node.updateLocal(mDelta)

                        val top = mModelMatrixStack.top.copy()
                        top.transpose()

                        node.applyTransform(local.multiply(top))
                    }
                }

                // ...update shader uniform...
                with(mProgram) {
                    set("view_matrix" to mViewMatrix, false)
                    this assign value("model_matrix" to node.modelMatrix, false)
                    // set("model_matrix" to node.modelMatrix, false)

                    // set("color" to node.color)
                    this assign value("color" to node.color)
                }

                // ...and draw call.
                node.draw()

                mDrawCallsCount++
            }
        }
    }
}
