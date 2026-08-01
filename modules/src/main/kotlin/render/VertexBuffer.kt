package render

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.IntBuffer

enum class VertexBuffersEnum {
    VERTICIES,
    INDICIES,
    COLORS,
    TEXCRDS,
    NORMALS
}

class VertexBuffer {
    private val _stack: MemoryStack
    private val _array: Int
    private val _buffers: IntBuffer
    private var _verteciesCount: Int = 0
    private var _indexCount: Int = 0

    init {
        _stack = MemoryStack.stackPush()

        // _buffers = _stack.mallocInt(3)
        _buffers = MemoryUtil.memAllocInt(3)

        _array = glCreateVertexArrays()

        glGenBuffers(_buffers);
    }

    fun clean() {
        glDeleteVertexArrays(_array);
        glDeleteBuffers(_buffers);

        MemoryUtil.memFree(_buffers)
    }

    fun bind() {
        glBindVertexArray(_array);
    }

    fun bindVertexData(data: FloatArray) {
        _verteciesCount = data.size

        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, _buffers[VertexBuffersEnum.VERTICIES.ordinal]);
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L);
    }

    fun bindIndexData(data: IntArray) {
        _indexCount = data.size
        // glEnableVertexAttribArray(1);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _buffers[VertexBuffersEnum.INDICIES.ordinal]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        // glVertexAttribPointer(1, 3, GL_UNSIGNED_INT, false, 0, 0L);
    }

//    fun bindNormalData( long verteciesCount, const void *data ) {
//        const auto bufferSize = verteciesCount * 3 * sizeof( float );
//        glEnableVertexAttribArray( 1 );
//        glBindBuffer( GL_ARRAY_BUFFER, buffers_[NORMAL_BUFFER] );
//        glBufferData( GL_ARRAY_BUFFER, bufferSize, data, GL_DYNAMIC_DRAW );
//        glVertexAttribPointer( 1, 3, GL_FLOAT, GL_FALSE, 0, nullptr );
//    }

//    fun bindTexcrdData( long verteciesCount, const void *data ) {
//        const auto bufferSize = verteciesCount * 2 * sizeof( float );
//        glEnableVertexAttribArray( 2 );
//        glBindBuffer( GL_ARRAY_BUFFER, buffers_[TEXCRD_BUFFER] );
//        glBufferData( GL_ARRAY_BUFFER, bufferSize, data, GL_STATIC_DRAW );
//        glVertexAttribPointer( 2, 2, GL_FLOAT, GL_FALSE, 0, nullptr );
//    }

    fun updateVertexData(data: FloatArray) {
        glBindBuffer(GL_ARRAY_BUFFER, _buffers[VertexBuffersEnum.VERTICIES.ordinal]);

        // GL_WRITE_ONLY means we only intend to write data to the buffer.
        // It returns a ByteBuffer pointing directly to the GPU-accessible memory.
        val byteBuffer: ByteBuffer? = glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY)

        if (byteBuffer != null) {
            // Create a FloatBuffer view of the ByteBuffer to easily put floats.
            val floatBuffer = byteBuffer.asFloatBuffer()

            // Put your data into the mapped memory.
            floatBuffer.put(data)

            // Unmap the buffer immediately after you are done modifying it.
            // This tells the GPU that you are finished and the data can be synchronized.
            glUnmapBuffer(GL_ARRAY_BUFFER)
        }

        // Unbind the buffer.
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    fun updateIndexData(data: IntArray) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _buffers[VertexBuffersEnum.INDICIES.ordinal]);

        val byteBuffer: ByteBuffer? = glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY)

        if (byteBuffer != null) {
            val intBuffer = byteBuffer.asIntBuffer()

            intBuffer.put(data)

            glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER)
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

//    fun updateNormalsData( long size, const void *data ) {
//        glBindBuffer( GL_ARRAY_BUFFER, buffers_[NORMAL_BUFFER] );
//        void *ptr = glMapBuffer( GL_ARRAY_BUFFER, GL_WRITE_ONLY );
//        memcpy( ptr, data, size );
//        glUnmapBuffer( GL_ARRAY_BUFFER );
//    }

    fun release() {
        glBindVertexArray(0);
    }

    fun draw() {
        glBindVertexArray(_array);
        glDrawArrays(GL_TRIANGLES, 0, _verteciesCount);
    }

    fun drawIndexed() {
        glBindVertexArray(_array);
        //glDrawElements(GL_TRIANGLES, _indexCount / 3, GL_UNSIGNED_INT, 0L);
        glDrawElements(GL_TRIANGLES, 2, GL_UNSIGNED_INT, 0L);
    }
}
