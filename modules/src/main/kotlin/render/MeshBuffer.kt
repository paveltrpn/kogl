package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.*
import java.nio.*

import mesh.*

// ============================================================================
// ======================= MeshBuffersEnum ====================================
// ============================================================================

enum class MeshBuffersEnum {
    VERTICES,
    INDICES,
    COLORS,
    TEXCRDS,
    NORMALS,
    INTERLEAVED
}

// ============================================================================
// ======================= MeshBuffer =========================================
// ============================================================================

abstract class MeshBuffer {
    // OpenGL related.
    protected val mVao: Int
    protected val mBuffers: IntBuffer

    init {
        mBuffers = MemoryUtil.memAllocInt(MeshBuffersEnum.entries.size)
        glCreateBuffers(mBuffers)

        mVao = glCreateVertexArrays()
        glBindVertexArray(mVao)
    }

    fun clean(): Unit {
        glDeleteBuffers(mBuffers)
        glDeleteVertexArrays(mVao)

        MemoryUtil.memFree(mBuffers)
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
            mBuffers[MeshBuffersEnum.VERTICES.ordinal],
            _mesh.vertices.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        )

        MemoryStack.stackPush().use { stack: MemoryStack ->
            val buffer: FloatBuffer = stack.callocFloat(_mesh.vertices.size)
            buffer.put(_mesh.vertices)
            buffer.flip()
            glNamedBufferSubData(mBuffers[MeshBuffersEnum.VERTICES.ordinal], 0, buffer)
        }

        // Create buffer for normals.
        glNamedBufferStorage(
            mBuffers[MeshBuffersEnum.NORMALS.ordinal],
            _mesh.vnormals.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        )

        MemoryStack.stackPush().use { stack: MemoryStack ->
            val buffer: FloatBuffer = stack.callocFloat(_mesh.vnormals.size)
            buffer.put(_mesh.vnormals)
            buffer.flip()
            glNamedBufferSubData(mBuffers[MeshBuffersEnum.NORMALS.ordinal], 0, buffer)
        }

        // Create buffer for indices.
        MemoryStack.stackPush().use { stack: MemoryStack ->
            val buffer: IntBuffer = stack.callocInt(_mesh.indices.size)
            buffer.put(_mesh.indices)
            buffer.flip()
            glNamedBufferStorage(mBuffers[MeshBuffersEnum.INDICES.ordinal], buffer, GL_DYNAMIC_STORAGE_BIT)
        }

        // Link position to VAO attribute 0.
        glVertexArrayVertexBuffer(
            mVao, /* binding */ 0,
            mBuffers[MeshBuffersEnum.VERTICES.ordinal],
            0,
            Float.SIZE_BYTES * 3
        )
        glEnableVertexArrayAttrib(mVao, /* attribute */ 0)
        glVertexArrayAttribFormat(mVao, /* attribute */ 0, 3, GL_FLOAT, false, 0)
        glVertexArrayAttribBinding(mVao, /* attribute */ 0, /* binding */ 0)

        // Link normals buffer to VAO attribute 1.
        glVertexArrayVertexBuffer(mVao, 1, mBuffers[MeshBuffersEnum.NORMALS.ordinal], 0, Float.SIZE_BYTES * 3)
        glEnableVertexArrayAttrib(mVao, 1)
        glVertexArrayAttribFormat(mVao, 1, 3, GL_FLOAT, false, 0)
        glVertexArrayAttribBinding(mVao, 1, 1)

        // Attach Index Buffer(EBO) directly to the VAO.
        glVertexArrayElementBuffer(
            mVao,
            mBuffers[MeshBuffersEnum.INDICES.ordinal]
        )

        glBindVertexArray(0)
    }

    override fun draw() {
        glBindVertexArray(mVao)
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
            mBuffers[MeshBuffersEnum.VERTICES.ordinal],
            _mesh.vertices.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        )

        MemoryStack.stackPush().use { stack: MemoryStack ->
            val buffer: FloatBuffer = stack.callocFloat(_mesh.vertices.size)
            buffer.put(_mesh.vertices)
            buffer.flip()
            glNamedBufferSubData(mBuffers[MeshBuffersEnum.VERTICES.ordinal], 0, buffer)
        }

        // Create buffer for normals.
        glNamedBufferStorage(
            mBuffers[MeshBuffersEnum.NORMALS.ordinal],
            _mesh.vnormals.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        )

        MemoryStack.stackPush().use { stack: MemoryStack ->
            val buffer: FloatBuffer = stack.callocFloat(_mesh.vnormals.size)
            buffer.put(_mesh.vnormals)
            buffer.flip()
            glNamedBufferSubData(mBuffers[MeshBuffersEnum.NORMALS.ordinal], 0, buffer)
        }

        // Link vertex buffer to VAO attribute 0 (position)
        glVertexArrayVertexBuffer(mVao, 0, mBuffers[MeshBuffersEnum.VERTICES.ordinal], 0, Float.SIZE_BYTES * 3)
        glEnableVertexArrayAttrib(mVao, 0)
        glVertexArrayAttribFormat(mVao, 0, 3, GL_FLOAT, false, 0)
        glVertexArrayAttribBinding(mVao, 0, 0)

        // Link normals buffer to VAO attribute 1 (position)
        glVertexArrayVertexBuffer(mVao, 1, mBuffers[MeshBuffersEnum.NORMALS.ordinal], 0, Float.SIZE_BYTES * 3)
        glEnableVertexArrayAttrib(mVao, 1)
        glVertexArrayAttribFormat(mVao, 1, 3, GL_FLOAT, false, 0)
        glVertexArrayAttribBinding(mVao, 1, 1)

        glBindVertexArray(0)
    }

    override fun draw() {
        glBindVertexArray(mVao)
        glDrawArrays(GL_TRIANGLES, 0, _mesh.vertices.size)
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
            mBuffers[MeshBuffersEnum.INTERLEAVED.ordinal],
            _mesh.mesh.size.toLong() * 4,
            GL_DYNAMIC_STORAGE_BIT
        )

        MemoryStack.stackPush().use { stack: MemoryStack ->
            val buffer: FloatBuffer = stack.callocFloat(_mesh.mesh.size)
            buffer.put(_mesh.mesh)
            buffer.flip()
            glNamedBufferSubData(mBuffers[MeshBuffersEnum.INTERLEAVED.ordinal], 0, buffer)
        }

        glVertexArrayVertexBuffer(mVao, 0, mBuffers[MeshBuffersEnum.INTERLEAVED.ordinal], 0, Float.SIZE_BYTES * 8)

        // Configure Position Attribute (Location 0)
        glEnableVertexArrayAttrib(mVao, 0)
        glVertexArrayAttribFormat(mVao, 0, 3, GL_FLOAT, false, Float.SIZE_BYTES * 0)
        // Connect attrib 0 to binding index 0
        glVertexArrayAttribBinding(mVao, 0, 0)

        // Configure Normal Attribute (Location 1)
        glEnableVertexArrayAttrib(mVao, 1)
        glVertexArrayAttribFormat(mVao, 1, 3, GL_FLOAT, false, Float.SIZE_BYTES * 3)
        // Connect attrib 1 to binding index 0
        glVertexArrayAttribBinding(mVao, 1, 0)

        // Configure UV Attribute (Location 2)
        glEnableVertexArrayAttrib(mVao, 2)
        glVertexArrayAttribFormat(mVao, 2, 2, GL_FLOAT, false, Float.SIZE_BYTES * 6)
        // Connect attrib 2 to binding index 0
        glVertexArrayAttribBinding(mVao, 2, 0)

        glBindVertexArray(0)
    }

    override fun draw() {
        glBindVertexArray(mVao)
        glDrawArrays(GL_TRIANGLES, 0, _mesh.mesh.size)
        glBindVertexArray(0)
    }
}
