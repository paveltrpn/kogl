package render

import java.nio.ByteBuffer

import org.lwjgl.opengl.GL46.*
import org.lwjgl.system.MemoryStack

import image.*
import java.nio.FloatBuffer

class Texture(image: Image) {
    private val _texure: Int

    init {
        _texure = glGenTextures()

        glTexParameteri(
            GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
            GL_LINEAR_MIPMAP_LINEAR
        )

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        MemoryStack.stackPush().use { stack ->
            val buffer: ByteBuffer = stack.calloc(image.data.size)
            buffer.put(image.data)
            buffer.flip()
            glTexImage2D(
                GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA,
                GL_UNSIGNED_BYTE, buffer
            )
        }

        glGenerateMipmap(GL_TEXTURE_2D)
    }

    fun clean(): Unit {
        glDeleteTextures(_texure)
    }

    fun bind(bpoint: Int): Unit {
        glActiveTexture(GL_TEXTURE0 + bpoint)
        glBindTexture(GL_TEXTURE_2D, _texure)
    }

}