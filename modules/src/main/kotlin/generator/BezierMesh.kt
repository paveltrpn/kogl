package generator

import algebra.Vector2
import algebra.Vector3

class BezierMesh(
    mControlPoints: Array<Array<Vector3>>,
    mSegments: IntPair = IntPair(16, 16)
) : GeneratorMesh {

    private val _impl: GeneratorMesh = ParametricMesh(bezierMeshEval(mControlPoints), mSegments)

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}

private fun bezierMeshEval(controlPoints: Array<Array<Vector3>>): (Vector2) -> MeshVertex {
    require(controlPoints.size > 1) { "BezierMesh requires more than one control point row." }
    require(controlPoints[0].size > 1) { "BezierMesh requires more than one control point column." }
    val d1 = controlPoints.size
    val d0 = controlPoints[0].size
    val points = Array(d1) { row ->
        Array(d0) { col ->
            val point = controlPoints[row][col]
            Vector3(point.x, point.y, point.z)
        }
    }
    val epsilon = 1.1920929e-7f
    return { t ->
        val vertex = MeshVertex()
        vertex.mPosition = bezierSurfacePoint(points, t)
        var jacobian = bezierSurfaceJacobian(points, t)
        var normal = cross(jacobian.first, jacobian.second)
        if (dot(normal, normal) < epsilon) {
            jacobian = bezierSurfaceJacobian(
                points,
                Vector2(t.x + 10.0f * epsilon, t.y + 10.0f * epsilon)
            )
            normal = cross(jacobian.first, jacobian.second)
        }
        vertex.mNormal = normalize(normal)
        vertex.mTexCoord = Vector2(t.x, t.y)
        vertex
    }
}

private fun bezierSurfacePoint(points: Array<Array<Vector3>>, t: Vector2): Vector3 {
    val d1 = points.size
    val temp = Array(d1) { row -> bezierCurvePoint(points[row], t.x) }
    return bezierCurvePoint(temp, t.y)
}

private fun bezierSurfaceJacobian(points: Array<Array<Vector3>>, t: Vector2): Pair<Vector3, Vector3> {
    val d1 = points.size
    val d0 = points[0].size
    val temp0 = Array(d0) { col ->
        val column = Array(d1) { row -> points[row][col] }
        bezierCurvePoint(column, t.y)
    }
    val temp1 = Array(d1) { row -> bezierCurvePoint(points[row], t.x) }
    return Pair(bezierCurveDerivative(temp0, t.x), bezierCurveDerivative(temp1, t.y))
}

private fun bezierCurvePoint(points: Array<Vector3>, t: Float): Vector3 {
    val count = points.size
    val temp = Array(count) { index ->
        val point = points[index]
        Vector3(point.x, point.y, point.z)
    }
    val u = 1.0f - t
    for (k in count - 1 downTo 1) {
        for (i in 0 until k) {
            temp[i] = Vector3(
                u * temp[i].x + t * temp[i + 1].x,
                u * temp[i].y + t * temp[i + 1].y,
                u * temp[i].z + t * temp[i + 1].z
            )
        }
    }
    return temp[0]
}

private fun bezierCurveDerivative(points: Array<Vector3>, t: Float): Vector3 {
    val count = points.size
    val factor = (count - 1).toFloat()
    val derivativePoints = Array(count - 1) { index ->
        Vector3(
            factor * (points[index + 1].x - points[index].x),
            factor * (points[index + 1].y - points[index].y),
            factor * (points[index + 1].z - points[index].z)
        )
    }
    return bezierCurvePoint(derivativePoints, t)
}
