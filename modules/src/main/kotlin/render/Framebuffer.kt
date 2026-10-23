package render

import org.lwjgl.opengl.GL46.*
import java.nio.*

// ============================================================================
// ======================= Framebuffer ========================================
// ============================================================================

class Framebuffer(
    width: Int,
    height: Int,
    withDepth: Boolean = true,
    colorFormat: Int = GL_RGB
) {
    private val _fbo: Int = glGenFramebuffers()

    private val _withDepth: Boolean = withDepth
    private val _colorFormat: Int = colorFormat

    private var _colorTexture: Int = 0
    private var _depthTexture: Int = 0

    init {
        glBindFramebuffer(GL_FRAMEBUFFER, _fbo)
        _create(width, height)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    private fun _create(width: Int, height: Int): Unit {
        _colorTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, _colorTexture)

        glTexImage2D(GL_TEXTURE_2D, 0, _colorFormat, width, height, 0, _colorFormat, GL_UNSIGNED_BYTE, null as ByteBuffer?)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, _colorTexture, 0)
        glBindTexture(GL_TEXTURE_2D, 0)

        if (_withDepth) {
            _depthTexture = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, _depthTexture)

            glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null as FloatBuffer?)

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, _depthTexture, 0)
            glBindTexture(GL_TEXTURE_2D, 0)
        }

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw RuntimeException("Framebuffer is not complete!")
        }
    }

    private fun destroy(): Unit {
        if (_colorTexture != 0) {
            glDeleteTextures(_colorTexture)
            _colorTexture = 0
        }

        if (_depthTexture != 0) {
            glDeleteTextures(_depthTexture)
            _depthTexture = 0
        }
    }

    fun bind(): Unit {
        glBindFramebuffer(GL_FRAMEBUFFER, _fbo)
    }

    fun unbind(): Unit {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun bindTexture(unit: Int): Unit {
        glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(GL_TEXTURE_2D, _colorTexture)
    }

    fun bindDepthTexture(unit: Int): Unit {
        glActiveTexture(GL_TEXTURE0 + unit)
        glBindTexture(GL_TEXTURE_2D, _depthTexture)
    }

    fun resize(width: Int, height: Int): Unit {
        glBindFramebuffer(GL_FRAMEBUFFER, _fbo)
        destroy()
        _create(width, height)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun clean(): Unit {
        destroy()
        glDeleteFramebuffers(_fbo)
    }
}
