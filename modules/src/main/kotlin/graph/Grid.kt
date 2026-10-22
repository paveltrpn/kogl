package graph

import org.lwjgl.opengl.GL46.*

import algebra.*
import render.*
import mesh.*

// ============================================================================
// ======================= Grid ===============================================
// ============================================================================

class Grid : Leaf() {
    protected var mColor = Vector3(1.0f, 1.0f, 1.0f)
    protected var mModelMatrix = Matrix4().idtt()

    protected var mOrigin = Vector3(0.0f, 0.0f, 0.0f)
    protected var mScale = Vector3(1.0f, 1.0f, 1.0f)
    protected var mAxis = Vector3(0.0f, 1.0f, 0.0f)
    protected var mAngl = 0.0f
    protected var mAnglSpeed = 0.0f

    protected val mVao: Int

    init {
        mVao = glCreateVertexArrays()
        glBindVertexArray(mVao)
    }

    fun draw(): Unit {
        glBindVertexArray(mVao)
        glDrawArrays(GL_TRIANGLES, 0, 6)
        glBindVertexArray(0)
    }
}

