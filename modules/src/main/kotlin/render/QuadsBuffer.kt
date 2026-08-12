package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack
import java.nio.FloatBuffer

class QuadsBuffer(capacity: Int) {
    private var _vao: Int = 0
    private val _vbo: Int
    private val _tbo: Int

    // Count of vertices in this buffer is possible count of quads
    // that can hold this buffer times vertex per quad.
    private var _vertexCount = capacity * 6

    init {
        _vao = glGenVertexArrays()
        glBindVertexArray(_vao)

        _vbo = glGenBuffers()
        _tbo = glGenBuffers()

        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, _vbo)
        glBufferData(GL_ARRAY_BUFFER, (Float.SIZE_BYTES * 3 * _vertexCount).toLong(), GL_DYNAMIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L)

        glEnableVertexAttribArray(1)
        glBindBuffer(GL_ARRAY_BUFFER, _tbo)
        glBufferData(GL_ARRAY_BUFFER, (Float.SIZE_BYTES * 2 * _vertexCount).toLong(), GL_DYNAMIC_DRAW)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0L)

        glBindVertexArray(0)
    }

    fun clean() {
        glDeleteBuffers(_vbo)
        glDeleteBuffers(_tbo)
        glDeleteVertexArrays(_vao)
    }

    fun updateData(vertices: FloatArray, txcoords: FloatArray) {
        glBindVertexArray(_vao)

        glBindBuffer(GL_ARRAY_BUFFER, _vbo)

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(vertices.size)
            buffer.put(vertices)
            buffer.flip()
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)
        }

//        glBindBuffer(GL_ARRAY_BUFFER, _tbo)
//
//        MemoryStack.stackPush().use { stack ->
//            val buffer: FloatBuffer = stack.callocFloat(txcoords.size)
//            buffer.put(txcoords)
//            buffer.flip()
//            glBufferSubData(GL_ARRAY_BUFFER, 0, buffer)
//        }

//        val pointer = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY)
//        MemoryStack.stackPush().use { stack ->
//            val buffer = stack.calloc(vertices.size * 3)
//            buffer.asFloatBuffer().put(vertices)
//            buffer.flip()
//
//            pointer?.put(buffer)
//        }
//
//        glUnmapBuffer(GL_ARRAY_BUFFER)

        glBindVertexArray(0)
    }

    fun draw() {
        glBindVertexArray(_vao)
        glDrawArrays(GL_TRIANGLES, 0, _vertexCount / 3)
        glBindVertexArray(0)
    }
}
