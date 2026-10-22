package graph

import org.lwjgl.opengl.GL46.*

import algebra.*
import generator.*
import mesh.*
import render.*

// ============================================================================
// ======================= DrawableVisitor ====================================
// ============================================================================

class DrawableVisitor(
    delta: Float,
    viewMatrix: Matrix4,
    val lights: List<OmniLightData> = emptyList(),
    val lightDummiesVisible: Boolean = true
) : DrawCallVisitorBase(delta, viewMatrix) {
    companion object {
        private const val MAX_LIGHTS = 16

        private val _dummyDrawable: SpinableDrawable by lazy {
            SpinableDrawable().apply {
                mesh = SeparatedArraysMesh(IcoSphereMesh(1.0f, 1).toObjMesh("lightDummy"))
                anglSpeed = 0.0f
            }
        }

        private val _dummyProgram: Program by lazy {
            Program().apply {
                source = ShaderSource("lightDummy")
                build()
            }
        }
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
            is OmniLight -> {
                if (lightDummiesVisible) {
                    val position = mModelMatrixStack.top.vecMultiply(Vector3(0.0f, 0.0f, 0.0f))

                    val offset = algebra.offset(position)
                    offset.transpose()

                    val model = algebra.scale(0.3f).multiply(offset)

                    val dummy = _dummyDrawable
                    dummy.modelMatrix = model
                    dummy.color = node.color

                    _dummyProgram.use()

                    with(_dummyProgram) {
                        set("view_matrix" to mViewMatrix, false)
                        set("model_matrix" to dummy.modelMatrix, false)
                        this assign value("color" to dummy.color)
                    }

                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
                    dummy.draw()
                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)

                    mProgram.use()

                    mDrawCallsCount++
                }
            }

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
