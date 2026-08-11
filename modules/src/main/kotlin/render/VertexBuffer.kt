package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.IntBuffer
import java.nio.FloatBuffer

enum class VertexBuffersEnum {
    VERTICES,
    INDICES,
    COLORS,
    TEXCRDS,
    NORMALS,
    INTERLEAVED
}

class VertexBuffer(vertexCount: Int) {
    private var _vao: Int = 0
    private val _vbo: Int
    private val _tbo: Int

    private var _vertexCount = vertexCount

    init {
        _vao = glGenVertexArrays()
        glBindVertexArray(_vao)

        _vbo = glGenBuffers();
        _tbo = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, _vbo)
        glBufferData(GL_ARRAY_BUFFER, (Float.SIZE_BYTES * 3 * _vertexCount).toLong(), GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L)

        glBindBuffer(GL_ARRAY_BUFFER, _tbo)
        glBufferData(GL_ARRAY_BUFFER, (Float.SIZE_BYTES * 2 * _vertexCount).toLong(), GL_DYNAMIC_DRAW)
        glEnableVertexAttribArray(1)
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

        glBindBuffer(GL_ARRAY_BUFFER, _tbo)

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(txcoords.size)
            buffer.put(txcoords)
            buffer.flip()
            glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)
        }

//        val pointer = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY)
//        MemoryStack.stackPush().use { stack ->
//            val buffer: ByteBuffer = stack.calloc(data.size * 4)
//            buffer.asFloatBuffer().put(data)
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
        glDrawArrays(GL_TRIANGLES, _vertexCount, GL_UNSIGNED_INT)
        glBindVertexArray(0)
    }
}
