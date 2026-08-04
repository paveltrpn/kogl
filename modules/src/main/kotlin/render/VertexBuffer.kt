package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer
import java.nio.FloatBuffer

enum class VertexBuffersEnum {
    VERTICIES,
    INDICIES,
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

        _vao = glCreateVertexArrays()
        glBindVertexArray(_vao)

        glCreateBuffers(_buffers);

        glNamedBufferStorage(
            _buffers[VertexBuffersEnum.VERTICIES.ordinal],
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
            glNamedBufferStorage(_buffers[VertexBuffersEnum.INDICIES.ordinal], buffer, GL_DYNAMIC_STORAGE_BIT);
        }

        // Link VBO to VAO attribute 0 (position)
        glVertexArrayVertexBuffer(_vao, 0, _buffers[VertexBuffersEnum.VERTICIES.ordinal], 0, Float.SIZE_BYTES * 3);

        glEnableVertexArrayAttrib(_vao, 0);
        glVertexArrayAttribFormat(_vao, 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(_vao, 0, 0);

        // Attach Index Buffer(EBO) directly to the VAO
        glVertexArrayElementBuffer(
            _vao,
            _buffers[VertexBuffersEnum.INDICIES.ordinal]
        );
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
            glNamedBufferSubData(_buffers[VertexBuffersEnum.VERTICIES.ordinal], 0, buffer)
        }
    }

    fun drawIndexed() {
        glBindVertexArray(_vao)
        glDrawElements(GL_TRIANGLES, _indexCount, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }
}
