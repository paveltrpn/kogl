package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.IntBuffer
import java.nio.FloatBuffer

import mesh.*

// ============================================================================
// ======================= MeshBuffer =========================================
// ============================================================================

abstract class MeshBuffer {
    // OpenGL related.
    protected var _vao: Int
    protected var _buffers: IntBuffer

    init {
        _buffers = MemoryUtil.memAllocInt(VertexBuffersEnum.entries.size)
        glCreateBuffers(_buffers);

        _vao = glCreateVertexArrays()
        glBindVertexArray(_vao)
    }

    fun clean(): Unit {
        glDeleteBuffers(_buffers)
        glDeleteVertexArrays(_vao)

        MemoryUtil.memFree(_buffers)
    }

    abstract fun draw(): Unit
}

// ============================================================================
// ======================= IndexedMeshBuffer ==================================
// ============================================================================

class IndexedMeshBuffer(mesh: IndexedMesh) : MeshBuffer() {
    private val _mesh = mesh

    init {
//        MemoryStack.stackPush().use { stack ->
//            val buffer: FloatBuffer = stack.callocFloat(_mesh.vertices.size)
//            buffer.put(_mesh.vertices)
//            buffer.flip()
//            glNamedBufferStorage(VertexBuffersEnum.VERTICES.ordinal, buffer, GL_DYNAMIC_STORAGE_BIT);
//        }

        // Create buffer for vertices.
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

        // Create buffer for normals.
        glNamedBufferStorage(
            _buffers[VertexBuffersEnum.NORMALS.ordinal],
            _mesh.vnormals.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        );

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(_mesh.vnormals.size)
            buffer.put(_mesh.vnormals)
            buffer.flip()
            glNamedBufferSubData(_buffers[VertexBuffersEnum.NORMALS.ordinal], 0, buffer)
        }

        // Create buffer for indices.
        MemoryStack.stackPush().use { stack ->
            val buffer: IntBuffer = stack.callocInt(_mesh.indices.size)
            buffer.put(_mesh.indices)
            buffer.flip()
            glNamedBufferStorage(_buffers[VertexBuffersEnum.INDICES.ordinal], buffer, GL_DYNAMIC_STORAGE_BIT);
        }

        // Link VBO to VAO attribute 0 (position)
        glVertexArrayVertexBuffer(
            _vao, /* binding */ 0,
            _buffers[VertexBuffersEnum.VERTICES.ordinal],
            0,
            Float.SIZE_BYTES * 3
        );
        glEnableVertexArrayAttrib(_vao, /* attribute */ 0);
        glVertexArrayAttribFormat(_vao, /* attribute */ 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(_vao, /* attribute */ 0, /* binding */ 0);

        // Link normals buffer to VAO attribute 1 (position)
        glVertexArrayVertexBuffer(_vao, 1, _buffers[VertexBuffersEnum.NORMALS.ordinal], 0, Float.SIZE_BYTES * 3);
        glEnableVertexArrayAttrib(_vao, 1);
        glVertexArrayAttribFormat(_vao, 1, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(_vao, 1, 1);

        // Attach Index Buffer(EBO) directly to the VAO
        glVertexArrayElementBuffer(
            _vao,
            _buffers[VertexBuffersEnum.INDICES.ordinal]
        );

        glBindVertexArray(0)
    }

    override fun draw() {
        glBindVertexArray(_vao)
        glDrawElements(GL_TRIANGLES, _mesh.indices.size, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

//    fun updateVertexData(data: FloatArray) {
//        // By buffer mapping.
//        val pointer = glMapNamedBufferRange(
//            vbo,
//            0,
//            (_vertexCount * 3 * 4).toLong(),
//            GL_MAP_WRITE_BIT or GL_MAP_INVALIDATE_RANGE_BIT
//        )
//
//        if (pointer == null) {
//            return
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
//
//        // By buffer subdata update.
//        MemoryStack.stackPush().use { stack ->
//            val buffer: FloatBuffer = stack.callocFloat(data.size)
//            buffer.put(data)
//            buffer.flip()
//            glNamedBufferSubData(_buffers[VertexBuffersEnum.VERTICES.ordinal], 0, buffer)
//        }
//    }
}

// ============================================================================
// ======================= ArrayMeshBuffer ====================================
// ============================================================================

class ArrayMeshBuffer(mesh: SeparatedArraysMesh) : MeshBuffer() {
    private val _mesh = mesh

    init {
        // Create buffer for vertices.
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

        // Create buffer for normals.
        glNamedBufferStorage(
            _buffers[VertexBuffersEnum.NORMALS.ordinal],
            _mesh.vnormals.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        );

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(_mesh.vnormals.size)
            buffer.put(_mesh.vnormals)
            buffer.flip()
            glNamedBufferSubData(_buffers[VertexBuffersEnum.NORMALS.ordinal], 0, buffer)
        }

        // Link vertex buffer to VAO attribute 0 (position)
        glVertexArrayVertexBuffer(_vao, 0, _buffers[VertexBuffersEnum.VERTICES.ordinal], 0, Float.SIZE_BYTES * 3);
        glEnableVertexArrayAttrib(_vao, 0);
        glVertexArrayAttribFormat(_vao, 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(_vao, 0, 0);

        // Link normals buffer to VAO attribute 1 (position)
        glVertexArrayVertexBuffer(_vao, 1, _buffers[VertexBuffersEnum.NORMALS.ordinal], 0, Float.SIZE_BYTES * 3);
        glEnableVertexArrayAttrib(_vao, 1);
        glVertexArrayAttribFormat(_vao, 1, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(_vao, 1, 1);

        glBindVertexArray(0)
    }

    override fun draw() {
        glBindVertexArray(_vao)
        glDrawArrays(GL_TRIANGLES, 0, _mesh.vertices.size);
        glBindVertexArray(0)
    }
}

// ============================================================================
// ======================= InterleavedMeshBuffer ==============================
// ============================================================================

class InterleavedMeshBuffer(mesh: InterleavedMesh) : MeshBuffer() {
    private val _mesh = mesh

    init {
        glNamedBufferStorage(
            _buffers[VertexBuffersEnum.INTERLEAVED.ordinal],
            _mesh.mesh.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        );

        MemoryStack.stackPush().use { stack ->
            val buffer: FloatBuffer = stack.callocFloat(_mesh.mesh.size)
            buffer.put(_mesh.mesh)
            buffer.flip()
            glNamedBufferSubData(_buffers[VertexBuffersEnum.INTERLEAVED.ordinal], 0, buffer)
        }

        glVertexArrayVertexBuffer(_vao, 0, _buffers[VertexBuffersEnum.INTERLEAVED.ordinal], 0, Float.SIZE_BYTES * 8);

        // Configure Position Attribute (Location 0)
        glEnableVertexArrayAttrib(_vao, 0);
        glVertexArrayAttribFormat(_vao, 0, 3, GL_FLOAT, false, Float.SIZE_BYTES * 0);
        // Connect attrib 0 to binding index 0
        glVertexArrayAttribBinding(_vao, 0, 0);

        // Configure Normal Attribute (Location 1)
        glEnableVertexArrayAttrib(_vao, 1);
        glVertexArrayAttribFormat(_vao, 1, 3, GL_FLOAT, false, Float.SIZE_BYTES * 3);
        // Connect attrib 1 to binding index 0
        glVertexArrayAttribBinding(_vao, 1, 0);

        // Configure UV Attribute (Location 2)
        glEnableVertexArrayAttrib(_vao, 2);
        glVertexArrayAttribFormat(_vao, 2, 2, GL_FLOAT, false, Float.SIZE_BYTES * 6);
        // Connect attrib 2 to binding index 0
        glVertexArrayAttribBinding(_vao, 2, 0);

        glBindVertexArray(0)
    }

    override fun draw() {
        glBindVertexArray(_vao)
        glDrawArrays(GL_TRIANGLES, 0, _mesh.mesh.size);
        glBindVertexArray(0)
    }
}
