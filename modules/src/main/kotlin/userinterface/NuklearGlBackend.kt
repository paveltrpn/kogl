package userinterface

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.*
import org.lwjgl.nuklear.*
import org.lwjgl.nuklear.Nuklear.*

import config.*
import render.*

import java.nio.*
import kotlin.math.*

// ============================================================================
// ======================= NuklearGlBackend ===================================
// ============================================================================
class NuklearGlBackend {
    private val _INITIAL_VERTEX_SIZE = 512L * 1024L
    private val _INITIAL_INDEX_SIZE = 128L * 1024L

    private val _program: Program

    private var _vao: Int = 0
    private var _vbo: Int = 0
    private var _ebo: Int = 0

    private var _vertexCapacity: Long = 0
    private var _indexCapacity: Long = 0

    private var _fontTexture: Int = 0

    private var _scaleUniformLocation: Int = -1
    private var _translateUniformLocation: Int = -1
    private var _fontTexUniformLocation: Int = -1

    init {
        val nuklearProgramSource = ShaderSource("nuklear")
        _program = Program().apply {
            source(nuklearProgramSource)
            build()
        }

        _scaleUniformLocation = glGetUniformLocation(_program.handle, "scale")
        _translateUniformLocation = glGetUniformLocation(_program.handle, "translate")
        _fontTexUniformLocation = glGetUniformLocation(_program.handle, "fontTex")

        _vao = glGenVertexArrays()
        _vbo = glGenBuffers()
        _ebo = glGenBuffers()

        glBindVertexArray(_vao)

        glBindBuffer(GL_ARRAY_BUFFER, _vbo)
        glBufferData(GL_ARRAY_BUFFER, _INITIAL_VERTEX_SIZE, GL_STREAM_DRAW)
        _vertexCapacity = _INITIAL_VERTEX_SIZE

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _ebo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INITIAL_INDEX_SIZE, GL_STREAM_DRAW)
        _indexCapacity = _INITIAL_INDEX_SIZE

        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 20, 0L)

        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 8L)

        glEnableVertexAttribArray(2)
        glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, 20, 16L)

        glBindVertexArray(0)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun uploadFontTexture(pixels: ByteBuffer, width: Int, height: Int) {
        _fontTexture = glGenTextures()

        glBindTexture(GL_TEXTURE_2D, _fontTexture)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels)

        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun convert(
        ctx: NkContext,
        cmds: NkBuffer,
        vbuf: NkBuffer,
        ebuf: NkBuffer,
        config: NkConvertConfig
    ) {
        nk_convert(ctx, cmds, vbuf, ebuf, config)

        val vertexSize = nk_buffer_total(vbuf)
        val indexSize = nk_buffer_total(ebuf)

        if (vertexSize == 0L || indexSize == 0L) {
            return
        }

        glBindBuffer(GL_ARRAY_BUFFER, _vbo)

        if (vertexSize > _vertexCapacity) {
            _vertexCapacity = vertexSize * 2L
        }

        glBufferData(GL_ARRAY_BUFFER, _vertexCapacity, GL_STREAM_DRAW)

        val vertexData = MemoryUtil.memByteBuffer(nk_buffer_memory_const(vbuf), vertexSize.toInt())
        glBufferSubData(GL_ARRAY_BUFFER, 0L, vertexData)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _ebo)

        if (indexSize > _indexCapacity) {
            _indexCapacity = indexSize * 2L
        }

        glBufferData(GL_ELEMENT_ARRAY_BUFFER, _indexCapacity, GL_STREAM_DRAW)

        val indexData = MemoryUtil.memByteBuffer(nk_buffer_memory_const(ebuf), indexSize.toInt())
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0L, indexData)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    fun draw(ctx: NkContext, cmds: NkBuffer) {
        val width = Config.instance().konfig.window_width
        val height = Config.instance().konfig.window_height

        glDisable(GL_DEPTH_TEST)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        glEnable(GL_SCISSOR_TEST)

        _program.use()

        glUniform2f(_scaleUniformLocation, 2.0f / width.toFloat(), -2.0f / height.toFloat())
        glUniform2f(_translateUniformLocation, -1.0f, 1.0f)

        glUniform1i(_fontTexUniformLocation, 0)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, _fontTexture)

        glBindVertexArray(_vao)

        var indexOffset = 0
        var command: NkDrawCommand? = nk__draw_begin(ctx, cmds)

        while (command != null) {
            val elemCount = command.elem_count()

            if (elemCount > 0) {
                val clip = command.clip_rect()

                val x0 = maxOf(0, floor(clip.x().toDouble()).toInt())
                val y0 = maxOf(0, floor(clip.y().toDouble()).toInt())
                val x1 = minOf(width, ceil((clip.x() + clip.w()).toDouble()).toInt())
                val y1 = minOf(height, ceil((clip.y() + clip.h()).toDouble()).toInt())

                if (!(x1 <= x0 || y1 <= y0)) {
                    glScissor(x0, height - y1, x1 - x0, y1 - y0)
                    glDrawElements(GL_TRIANGLES, elemCount, GL_UNSIGNED_SHORT, indexOffset.toLong() * 2L)
                }
            }

            indexOffset += elemCount
            command = nk__draw_next(command, cmds, ctx)
        }

        glBindVertexArray(0)

        glBindTexture(GL_TEXTURE_2D, 0)

        glDisable(GL_SCISSOR_TEST)
        glDisable(GL_BLEND)
    }

    fun free() {
        glDeleteTextures(_fontTexture)

        glDeleteBuffers(_vbo)
        glDeleteBuffers(_ebo)
        glDeleteVertexArrays(_vao)

        _program.clean()
    }
}
