package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.IntBuffer
import java.nio.FloatBuffer

import mesh.*

class MeshBuffer(mesh: IndexedMesh) {
    private val _mesh = mesh

    private val _vao: Int
    private val _buffers: IntBuffer

    init {
        _buffers = MemoryUtil.memAllocInt(VertexBuffersEnum.entries.size)
        glCreateBuffers(_buffers);

        _vao = glCreateVertexArrays()
        glBindVertexArray(_vao)

//        MemoryStack.stackPush().use { stack ->
//            val buffer: FloatBuffer = stack.callocFloat(_mesh.vertices.size)
//            buffer.put(_mesh.vertices)
//            buffer.flip()
//            glNamedBufferStorage(VertexBuffersEnum.VERTICES.ordinal, buffer, GL_DYNAMIC_STORAGE_BIT);
//        }

        glNamedBufferStorage(
            _buffers[VertexBuffersEnum.VERTICES.ordinal],
            _mesh.vertices.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        );

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(_mesh.vertices.size)
            buffer.put(_mesh.vertices)
            buffer.flip()
            glNamedBufferSubData(_buffers[VertexBuffersEnum.VERTICES.ordinal], 0, buffer)
        }

        MemoryStack.stackPush().use { stack ->
            val buffer: IntBuffer = stack.callocInt(_mesh.indices.size)
            buffer.put(_mesh.indices)
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

    fun drawIndexed() {
        glBindVertexArray(_vao)
        glDrawElements(GL_TRIANGLES, _mesh.indices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }
}
