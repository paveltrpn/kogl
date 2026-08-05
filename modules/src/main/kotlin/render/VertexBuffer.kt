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
    NORMALS
}

class VertexBuffer(vertices: FloatArray, indices: IntArray) {
    private val _vao: Int
    private val _buffers: IntBuffer
    private var _vertexCount: Int = 0
    private var _indexCount: Int = 0

    init {
        _indexCount = indices.size
        _vertexCount = vertices.size / 3

        _buffers = MemoryUtil.memAllocInt(VertexBuffersEnum.entries.size)
        glCreateBuffers(_buffers);

        _vao = glCreateVertexArrays()
        glBindVertexArray(_vao)

        glNamedBufferStorage(
            _buffers[VertexBuffersEnum.VERTICES.ordinal],
            vertices.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        );

//        MemoryStack.stackPush().use { stack ->
//            val buffer: FloatBuffer = stack.callocFloat(vertices.size)
//            buffer.put(vertices)
//            buffer.flip()
//            glNamedBufferStorage(vbo, buffer, GL_DYNAMIC_STORAGE_BIT);
//        }

        MemoryStack.stackPush().use { stack ->
            val buffer: IntBuffer = stack.callocInt(indices.size)
            buffer.put(indices)
            buffer.flip()
            glNamedBufferStorage(_buffers[VertexBuffersEnum.INDICES.ordinal], buffer, GL_DYNAMIC_STORAGE_BIT);
        }

        // Link VBO to VAO attribute 0 (position)
        glVertexArrayVertexBuffer(_vao, 0, _buffers[VertexBuffersEnum.VERTICES.ordinal], 0, Float.SIZE_BYTES * 3);
        glEnableVertexArrayAttrib(_vao, 0);
        glVertexArrayAttribFormat(_vao, 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(_vao, 0, 0);

        // Attach Index Buffer(EBO) directly to the VAO
        glVertexArrayElementBuffer(
            _vao,
            _buffers[VertexBuffersEnum.INDICES.ordinal]
        );

        glBindVertexArray(0)
    }

    fun clean(): Unit {
        glDeleteBuffers(_buffers)
        glDeleteVertexArrays(_vao)

        MemoryUtil.memFree(_buffers)
    }

    fun updateVertexData(data: FloatArray) {
//        val pointer = glMapNamedBufferRange(
//            vbo,
//            0,
//            (_vertexCount * 3 * 4).toLong(),
//            GL_MAP_WRITE_BIT or GL_MAP_INVALIDATE_RANGE_BIT
//        )
//
//        if (pointer == null) {
//            println(" ==== error ")
//        }
//
//        MemoryStack.stackPush().use { stack ->
//            val buffer: ByteBuffer = stack.calloc(data.size * 4)
//            buffer.asFloatBuffer().put(data)
//            buffer.flip()
//
//            pointer?.put(buffer)
//        }
//
//        glUnmapNamedBuffer(vbo)

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(data.size)
            buffer.put(data)
            buffer.flip()
            glNamedBufferSubData(_buffers[VertexBuffersEnum.VERTICES.ordinal], 0, buffer)
        }
    }

    fun drawIndexed() {
        glBindVertexArray(_vao)
        glDrawElements(GL_TRIANGLES, _indexCount, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }
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
