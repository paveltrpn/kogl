package generator

import algebra.Vector2

open class ParametricMesh(
    private val _eval: (Vector2) -> MeshVertex,
    private val _segments: IntPair
) : GeneratorMesh {

    override fun vertices(): Sequence<MeshVertex> = sequence {
        if (_segments.mX == 0 || _segments.mY == 0) {
            return@sequence
        }
        val dx = 1.0f / _segments.mX
        val dy = 1.0f / _segments.mY
        for (iy in 0.._segments.mY) {
            for (ix in 0.._segments.mX) {
                yield(_eval(Vector2(ix * dx, iy * dy)))
            }
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        val sx = _segments.mX
        val sy = _segments.mY
        if (sx == 0 || sy == 0) {
            return@sequence
        }
        for (iy in 0 until sy) {
            for (ix in 0 until sx) {
                val base = iy * (sx + 1) + ix
                yield(Triangle(base, base + 1, base + sx + 1))
                yield(Triangle(base + 1, base + sx + 2, base + sx + 1))
            }
        }
    }
}
