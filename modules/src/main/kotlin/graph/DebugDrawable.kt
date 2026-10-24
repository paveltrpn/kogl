package graph

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.*

import generator.*
import mesh.*

// ============================================================================
// ======================= DebugDrawable ======================================
// ============================================================================
abstract class DebugDrawable(
    vertices: FloatArray,
    private val _primitiveMode: Int
) : Drawable() {
    private val _vao = glCreateVertexArrays()
    private val _vbo = glCreateBuffers()
    private val _vertexCount = vertices.size / 3

    init {
        MemoryStack.stackPush().use { stack: MemoryStack ->
            val buffer = stack.mallocFloat(vertices.size)
            buffer.put(vertices)
            buffer.flip()
            glNamedBufferStorage(_vbo, buffer, 0)
        }

        glVertexArrayVertexBuffer(_vao, 0, _vbo, 0, Float.SIZE_BYTES * 3)
        glEnableVertexArrayAttrib(_vao, 0)
        glVertexArrayAttribFormat(_vao, 0, 3, GL_FLOAT, false, 0)
        glVertexArrayAttribBinding(_vao, 0, 0)

        glBindVertexArray(0)
    }

    override fun draw(): Unit {
        glBindVertexArray(_vao)
        glDrawArrays(_primitiveMode, 0, _vertexCount)
        glBindVertexArray(0)
    }
}

// ============================================================================
// ======================= DebugLightDrawable =================================
// ============================================================================
class DebugLightDrawable : DebugDrawable(
    SeparatedArraysMesh(IcoSphereMesh(1.0f, 1).toObjMesh("lightDummy")).vertices,
    GL_TRIANGLES
) {
    override fun draw(): Unit {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
        super.draw()
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
    }
}

// ============================================================================
// ======================= DebugAABBDrawable ==================================
// ============================================================================
class DebugAABBDrawable : DebugDrawable(
    floatArrayOf(
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
    ),
    GL_LINES
)
