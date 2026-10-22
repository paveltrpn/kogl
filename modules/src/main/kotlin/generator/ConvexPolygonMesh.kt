package generator

import algebra.Vector2
import algebra.Vector3

class ConvexPolygonMesh : GeneratorMesh {

    private val _vertices: List<Vector3>
    private val _segments: Int
    private val _rings: Int
    private val _center: Vector3
    private val _normal: Vector3
    private val _tangent: Vector3
    private val _bitangent: Vector3
    private val _texDelta: Vector2

    constructor(
        mRadius: Float = 1.0f,
        mSides: Int = 5,
        mSegments: Int = 4,
        mRings: Int = 4
    ) : this(makeVertices(mRadius, mSides), mSegments, mRings)

    constructor(
        mVertices: List<Vector3>,
        mSegments: Int = 1,
        mRings: Int = 1
    ) {
        _vertices = mVertices
        _segments = mSegments
        _rings = mRings
        _center = calcCenter(mVertices)
        _normal = calcNormal(_center, mVertices)
        if (mVertices.isNotEmpty()) {
            val tangent = normalize(sub(mVertices[0], _center))
            val bitangent = cross(_normal, tangent)
            var texMin = Vector2()
            var texMax = Vector2()
            for (vertex in mVertices) {
                val delta = sub(vertex, _center)
                val uv = Vector2(dot(tangent, delta), dot(bitangent, delta))
                texMin = min(texMin, uv)
                texMax = max(texMax, uv)
            }
            val size = sub(texMax, texMin)
            _tangent = scale(tangent, 1.0f / size.x)
            _bitangent = scale(bitangent, 1.0f / size.y)
            _texDelta = div(texMin, size)
        } else {
            _tangent = Vector3()
            _bitangent = Vector3()
            _texDelta = Vector2()
        }
    }

    override fun vertices(): Sequence<MeshVertex> = sequence {
        if (_segments == 0 || _rings == 0 || _vertices.size < 3) {
            return@sequence
        }
        var centerDone = false
        var segmentIndex = 0
        var sideIndex = 0
        var ringIndex = 0
        while (ringIndex != _rings) {
            val vertex = MeshVertex()
            if (centerDone) {
                val ringDelta = ringIndex.toFloat() / _rings
                val segmentDelta = segmentIndex.toFloat() / _segments
                val nextSide = (sideIndex + 1) % _vertices.size
                val a = mix(_vertices[sideIndex], _center, ringDelta)
                val b = mix(_vertices[nextSide], _center, ringDelta)
                vertex.mPosition = mix(a, b, segmentDelta)
            } else {
                vertex.mPosition = _center
            }
            vertex.mNormal = _normal
            val delta = sub(vertex.mPosition, _center)
            vertex.mTexCoord = sub(
                Vector2(dot(_tangent, delta), dot(_bitangent, delta)),
                _texDelta
            )
            yield(vertex)
            if (!centerDone) {
                centerDone = true
            } else {
                segmentIndex += 1
                if (segmentIndex == _segments) {
                    segmentIndex = 0
                    sideIndex += 1
                    if (sideIndex == _vertices.size) {
                        sideIndex = 0
                        ringIndex += 1
                    }
                }
            }
        }
    }

    override fun triangles(): Sequence<Triangle> = sequence {
        if (_segments == 0 || _vertices.size < 3) {
            return@sequence
        }
        val verticesPerRing = _segments * _vertices.size
        var odd = false
        var segmentIndex = 0
        var sideIndex = 0
        var ringIndex = 0
        while (ringIndex != _rings) {
            val delta = ringIndex * verticesPerRing + 1
            val n1 = sideIndex * _segments + segmentIndex
            val n2 = (n1 + 1) % verticesPerRing
            if (ringIndex == _rings - 1) {
                yield(Triangle(0, n1 + delta, n2 + delta))
            } else {
                if (!odd) {
                    yield(Triangle(n1 + delta, n2 + delta, n1 + verticesPerRing + delta))
                } else {
                    yield(Triangle(n2 + delta, n2 + verticesPerRing + delta, n1 + verticesPerRing + delta))
                }
            }
            if (ringIndex == _rings - 1) {
                segmentIndex += 1
                if (segmentIndex == _segments) {
                    segmentIndex = 0
                    sideIndex += 1
                    if (sideIndex == _vertices.size) {
                        ringIndex += 1
                    }
                }
            } else {
                odd = !odd
                if (!odd) {
                    segmentIndex += 1
                    if (segmentIndex == _segments) {
                        segmentIndex = 0
                        sideIndex += 1
                        if (sideIndex == _vertices.size) {
                            sideIndex = 0
                            odd = false
                            ringIndex += 1
                        }
                    }
                }
            }
        }
    }

    companion object {

        fun create(
            mVertices: List<Vector2>,
            mSegments: Int = 1,
            mRings: Int = 1
        ): ConvexPolygonMesh {
            return ConvexPolygonMesh(convertVertices(mVertices), mSegments, mRings)
        }

        private fun makeVertices(radius: Float, sides: Int): List<Vector3> {
            val result = ArrayList<Vector3>()
            for (shapeVertex in CircleShape(radius, sides).vertices()) {
                result.add(Vector3(shapeVertex.mPosition.x, shapeVertex.mPosition.y, 0.0f))
            }
            if (result.isNotEmpty()) {
                result.removeAt(result.size - 1)
            }
            return result
        }

        private fun convertVertices(vertices: List<Vector2>): List<Vector3> {
            val result = ArrayList<Vector3>(vertices.size)
            for (vertex in vertices) {
                result.add(Vector3(vertex.x, vertex.y, 0.0f))
            }
            return result
        }

        private fun calcCenter(vertices: List<Vector3>): Vector3 {
            var result = Vector3(0.0f, 0.0f, 0.0f)
            if (vertices.isEmpty()) {
                return result
            }
            for (vertex in vertices) {
                result = add(result, vertex)
            }
            return scale(result, 1.0f / vertices.size)
        }

        private fun calcNormal(center: Vector3, vertices: List<Vector3>): Vector3 {
            var result = Vector3(0.0f, 0.0f, 0.0f)
            if (vertices.isEmpty()) {
                return result
            }
            for (i in vertices.indices) {
                result = add(result, normal(center, vertices[i], vertices[(i + 1) % vertices.size]))
            }
            return normalize(result)
        }
    }
}
