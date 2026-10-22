package generator

open class ParametricPath(
    private val _eval: (Float) -> PathVertex,
    private val _segments: Int = 16
) : GeneratorPath {

    override fun vertices(): Sequence<PathVertex> = sequence {
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
