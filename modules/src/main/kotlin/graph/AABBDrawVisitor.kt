package graph

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.*

import algebra.*
import mesh.*
import render.*

// ============================================================================
// ======================= AABBDrawVisitor ====================================
// ============================================================================

class AABBDrawVisitor(delta: Float, viewMatrix: Matrix4) :
    DrawCallVisitorBase(delta, viewMatrix) {
    companion object {
        private const val BOX_VERTEX_COUNT = 24

        private val _boxColor = Vector3(1.0f, 1.0f, 0.0f)

        private val _boxProgram: Program by lazy {
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
            is Drawable -> {
                if (!node.hasMesh) {
                    return
                }

                val box = node.mesh.boundingBox

                val offset = algebra.offset(box.min)
                offset.transpose()

                val boxLocal = algebra.scale(box.size).multiply(offset)
                val model = boxLocal.multiply(node.modelMatrix)

                _boxProgram.use()

                with(_boxProgram) {
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
