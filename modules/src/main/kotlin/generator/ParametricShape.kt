package generator

// ============================================================================
// ======================= ParametricShape ====================================
// ============================================================================
open class ParametricShape(
    private val _eval: (Float) -> ShapeVertex,
    private val _segments: Int = 16
) : GeneratorShape {

    override fun vertices(): Sequence<ShapeVertex> = sequence {
        if (_segments == 0) {
            return@sequence
        }
        val delta = 1.0f / _segments
        for (i in 0.._segments) {
            yield(_eval(i * delta))
        }
    }

    override fun edges(): Sequence<Edge> = sequence {
        for (i in 0 until _segments) {
            yield(Edge(i, i + 1))
        }
    }
}
