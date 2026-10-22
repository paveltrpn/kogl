package render

import org.lwjgl.opengl.GL46.*
import java.nio.*

// ============================================================================
// ======================= Framebuffer ========================================
// ============================================================================

class Framebuffer(width: Int, height: Int) {
    private val _fbo: Int = glGenFramebuffers()

    private var _colorTexture: Int = 0
    private var _depthStencilRenderbuffer: Int = 0

    init {
        glBindFramebuffer(GL_FRAMEBUFFER, _fbo)
        _create(width, height)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    private fun _create(width: Int, height: Int): Unit {
        _colorTexture = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, _colorTexture)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, null as ByteBuffer?)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, _colorTexture, 0)
        glBindTexture(GL_TEXTURE_2D, 0)

        _depthStencilRenderbuffer = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, _depthStencilRenderbuffer)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height)

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, _depthStencilRenderbuffer)
        glBindRenderbuffer(GL_RENDERBUFFER, 0)

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw RuntimeException("Framebuffer is not complete!")
        }
    }

    private fun _destroy(): Unit {
        if (_colorTexture != 0) {
            glDeleteTextures(_colorTexture)
            _colorTexture = 0
        }

        if (_depthStencilRenderbuffer != 0) {
            glDeleteRenderbuffers(_depthStencilRenderbuffer)
            _depthStencilRenderbuffer = 0
        }
    }

    fun bind(): Unit {
        glBindFramebuffer(GL_FRAMEBUFFER, _fbo)
    }

    fun unbind(): Unit {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun bindTexture(): Unit {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, _colorTexture)
    }

    fun resize(width: Int, height: Int): Unit {
        glBindFramebuffer(GL_FRAMEBUFFER, _fbo)
        _destroy()
        _create(width, height)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun clean(): Unit {
        _destroy()
        glDeleteFramebuffers(_fbo)
    }
}
