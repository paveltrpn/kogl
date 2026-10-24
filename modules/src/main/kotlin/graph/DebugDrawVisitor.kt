package graph

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.*

import algebra.*
import generator.*
import mesh.*
import render.*

// ============================================================================
// ======================= DebugDrawVisitor ===================================
// ============================================================================

class DebugDrawVisitor(
    delta: Float,
    viewMatrix: Matrix4
) : DrawCallVisitorBase(delta, viewMatrix) {
    companion object {
        private const val BOX_VERTEX_COUNT = 24

        private val _boxColor = Vector3(1.0f, 1.0f, 0.0f)

        private val _dummyDrawable: SpinableDrawable by lazy {
            SpinableDrawable().apply {
                mesh = SeparatedArraysMesh(IcoSphereMesh(1.0f, 1).toObjMesh("lightDummy"))
                anglSpeed = 0.0f
            }
        }

        private val _debugProgram: Program by lazy {
            Program().apply {
                source = ShaderSource("lightDummy")
                build()
            }
        }

        private val _boxVao: Int by lazy {
            val vertices = floatArrayOf(
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,

                0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,

                0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f
            )

            val vao = glCreateVertexArrays()
            val vbo = glCreateBuffers()

            MemoryStack.stackPush().use { stack: MemoryStack ->
                val buffer = stack.mallocFloat(vertices.size)
                buffer.put(vertices)
                buffer.flip()
                glNamedBufferStorage(vbo, buffer, GL_DYNAMIC_STORAGE_BIT)
            }

            glVertexArrayVertexBuffer(vao, 0, vbo, 0, Float.SIZE_BYTES * 3)
            glEnableVertexArrayAttrib(vao, 0)
            glVertexArrayAttribFormat(vao, 0, 3, GL_FLOAT, false, 0)
            glVertexArrayAttribBinding(vao, 0, 0)

            glBindVertexArray(0)

            vao
        }
    }

    override fun apply(node: Leaf): Unit {
        when (node) {
            is OmniLight -> {
                val position = mModelMatrixStack.top.vecMultiply(Vector3(0.0f, 0.0f, 0.0f))

                val offset = algebra.offset(position)
                offset.transpose()

                val model = algebra.scale(0.3f).multiply(offset)

                val dummy = _dummyDrawable
                dummy.modelMatrix = model
                dummy.color = node.color

                _debugProgram.use()

                with(_debugProgram) {
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

            is Drawable -> {
                if (node.hasMesh) {
                    val box = node.mesh.boundingBox

                    val offset = algebra.offset(box.min)
                    offset.transpose()

                    val boxLocal = algebra.scale(box.size).multiply(offset)
                    val model = boxLocal.multiply(node.modelMatrix)

                    _debugProgram.use()

                    with(_debugProgram) {
                        set("view_matrix" to mViewMatrix, false)
                        set("model_matrix" to model, false)
                        this assign value("color" to _boxColor)
                    }

                    glBindVertexArray(_boxVao)
                    glDrawArrays(GL_LINES, 0, BOX_VERTEX_COUNT)
                    glBindVertexArray(0)

                    mProgram.use()

                    mDrawCallsCount++
                }
            }
        }
    }
}
