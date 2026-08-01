package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack
import java.nio.IntBuffer
import java.nio.FloatBuffer

enum class VertexBuffersEnum {
    VERTICIES,
    INDICIES,
    COLORS,
    TEXCRDS,
    NORMALS
}

class VertexBuffer {
    private val _vao: Int
    private val _vbo: Int
    private val _ebo: Int
    private var _vertexCount: Int = 0
    private var _indexCount: Int = 0

    init {
        _vao = glGenVertexArrays()
        glBindVertexArray(_vao)

        _vbo = glGenBuffers()
        _ebo = glGenBuffers()

        glBindVertexArray(0)
    }

    fun clean() {
        glDeleteBuffers(_ebo)
        glDeleteBuffers(_vbo)
        glDeleteVertexArrays(_vao)
    }

    fun bind() {
        glBindVertexArray(_vao)
    }

    fun release() {
        glBindVertexArray(0)
    }

    fun bindVertexData(data: FloatArray) {
        _vertexCount = data.size / 3

        glBindVertexArray(_vao)
        glBindBuffer(GL_ARRAY_BUFFER, _vbo)

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(data.size)
            buffer.put(data)
            buffer.flip()
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)
        }

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L)
        glBindVertexArray(0)
    }

    fun bindIndexData(data: IntArray) {
        _indexCount = data.size

        glBindVertexArray(_vao)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _ebo)

        MemoryStack.stackPush().use { stack ->
            val buffer: IntBuffer = stack.callocInt(data.size)
            buffer.put(data)
            buffer.flip()
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)
        }

        glBindVertexArray(0)
    }

    fun updateVertexData(data: FloatArray) {
        glBindBuffer(GL_ARRAY_BUFFER, _vbo)

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(data.size)
            buffer.put(data)
            buffer.flip()
            glBufferSubData(GL_ARRAY_BUFFER, 0, buffer)
        }
    }

//    NOTE: Unused!
//    fun updateIndexData(data: IntArray) {
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _ebo)
//
//        MemoryStack.stackPush().use { stack ->
//            val buffer: IntBuffer = stack.callocInt(data.size)
//            buffer.put(data)
//            buffer.flip()
//            glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, buffer)
//        }
//    }

    fun drawIndexed() {
        glBindVertexArray(_vao)
        glDrawElements(GL_TRIANGLES, _indexCount, GL_UNSIGNED_INT, 0L)
        glBindVertexArray(0)
    }
}
