package graph

import algebra.*
import kotlin.test.*

// ============================================================================
// ======================= CountingDrawable ===================================
// ============================================================================
private class CountingDrawable : Drawable() {
    private var _drawCount = 0

    val drawCount: Int
        get(): Int {
            return _drawCount
        }

    override fun draw(): Unit {
        _drawCount++
    }
}

// ============================================================================
// ======================= DrawableVisitorTest ================================
// ============================================================================
class DrawableVisitorTest {
    @Test
    fun skipsPreCulledDrawableBeforeUniformsAndDrawing(): Unit {
        val drawable = CountingDrawable()
        val visitor = DrawableVisitor(FrameTime(1.0f, 0.0), Matrix4())

        drawable.isFrustumCulled = true
        drawable.accept(visitor)

        assertEquals(0, drawable.drawCount)
        assertEquals(0, visitor.drawCallsCount)
    }
}
