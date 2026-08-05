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

class VertexBufferOLD {
    private val _vao: Int
    private val _buffers: IntBuffer
    private var _vertexCount: Int = 0
    private var _indexCount: Int = 0

    init {
        _buffers = MemoryUtil.memAllocInt(VertexBuffersEnum.entries.size)

        _vao = glGenVertexArrays()
        glBindVertexArray(_vao)

        glGenBuffers(_buffers);

        glBindVertexArray(0)
    }

    fun clean() {
        glDeleteBuffers(_buffers)
        glDeleteVertexArrays(_vao)

        MemoryUtil.memFree(_buffers)
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
        glBindBuffer(GL_ARRAY_BUFFER, _buffers[VertexBuffersEnum.VERTICES.ordinal])

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
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _buffers[VertexBuffersEnum.INDICES.ordinal])

        MemoryStack.stackPush().use { stack ->
            val buffer: IntBuffer = stack.callocInt(data.size)
            buffer.put(data)
            buffer.flip()
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)
        }

        glBindVertexArray(0)
    }

    fun updateVertexData(data: FloatArray) {
        glBindVertexArray(_vao)
        glBindBuffer(GL_ARRAY_BUFFER, _buffers[VertexBuffersEnum.VERTICES.ordinal])

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(data.size)
            buffer.put(data)
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
    }

//    NOTE: Unused!
//    fun updateIndexData(data: IntArray) {
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, VertexBuffersEnum.INDICES.ordinal)
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
