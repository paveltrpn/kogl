package generator

import algebra.toRadians

class TorusKnotMesh(
    mP: Int = 2,
    mQ: Int = 3,
    mSlices: Int = 8,
    mSegments: Int = 96
) : GeneratorMesh {

    private val _impl: GeneratorMesh = ExtrudeMesh(
        CircleShape(0.25f, mSlices, 0.0f, toRadians(360.0f)),
        KnotPath(mP, mQ, mSegments)
    )

    override fun vertices(): Sequence<MeshVertex> = _impl.vertices()

    override fun triangles(): Sequence<Triangle> = _impl.triangles()
}
