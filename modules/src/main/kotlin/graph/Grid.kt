package graph

import org.lwjgl.opengl.GL46.*

import algebra.*
import render.*
import mesh.*

class Grid : Leaf() {
    protected var _color = Vector3(1.0f, 1.0f, 1.0f)
    protected var _modelMatrix = Matrix4().idtt()

    protected var _origin = Vector3(0.0f, 0.0f, 0.0f)
    protected var _scale = Vector3(1.0f, 1.0f, 1.0f)
    protected var _axis = Vector3(0.0f, 1.0f, 0.0f)
    protected var _angl = 0.0f
    protected var _anglSpeed = 0.0f

    protected val _vao: Int

    init {
        _vao = glCreateVertexArrays()
        glBindVertexArray(_vao)
    }

    fun draw(): Unit {
        glBindVertexArray(_vao)
        glDrawArrays(GL_TRIANGLES, 0, 6)
        glBindVertexArray(0)
    }
}

