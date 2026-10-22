package generator

import algebra.Vector2
import algebra.Vector3

class SphericalTriangleMesh : GeneratorMesh {

    private val _v0: Vector3
    private val _v1: Vector3
    private val _v2: Vector3
    private val _normal: Vector3
    private val _segments: Int

    constructor(mRadius: Float = 1.0f, mSegments: Int = 4) : this(
        Vector3(mRadius, 0.0f, 0.0f),
        Vector3(0.0f, mRadius, 0.0f),
        Vector3(0.0f, 0.0f, mRadius),
        mSegments
    )

    constructor(
        mV0: Vector3,
        mV1: Vector3,
        mV2: Vector3,
        mSegments: Int = 4
    ) {
        _v0 = mV0
        _v1 = mV1
        _v2 = mV2
        _normal = normal(mV0, mV1, mV2)
        _segments = mSegments
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        var row = 0
        var col = 0
        var i = 0
        while (row != _segments) {
            if (col % 2 == 0) {
                yield(Triangle(i, i + 1, i + 1 + _segments - row))
            } else {
                yield(Triangle(i, i + 1 + _segments - row, i + _segments - row))
            }
            if (col % 2 == 0) {
                i += 1
            }
            col += 1
            if (col == 2 * (_segments - row) - 1) {
                i += 1
                col = 0
                row += 1
            }
        }
    }

    override fun vertices(): Sequence<MeshVertex> = sequence {
        var row = 0
        var col = 0
        while (row <= _segments) {
            val vertex = MeshVertex()
            if (row == _segments) {
                vertex.mPosition = _v2
                vertex.mTexCoord = Vector2(0.5f, 1.0f)
            } else {
                val t = 1.0f / _segments * row
                val t2 = 1.0f / (_segments - row) * col
                val e1 = slerp(_v0, _v2, t)
                val e2 = slerp(_v1, _v2, t)
                vertex.mPosition = slerp(e1, e2, t2)
                vertex.mTexCoord = Vector2(t2, t)
            }
            vertex.mNormal = normalize(vertex.mPosition)
            yield(vertex)
            col += 1
            if (col > _segments - row) {
                col = 0
                row += 1
            }
        }
    }
}
