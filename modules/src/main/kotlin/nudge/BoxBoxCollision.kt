package nudge

import algebra.*
import kotlin.math.*

private const val BOX_AXIS_EPSILON = 1e-3f
private const val BOX_FACE_BIAS = 1e-3f

internal fun collideBoxBox(a: WorldCollider, b: WorldCollider): List<Contact> {
    val aBox: BoxCollider = requireNotNull(a.box)
    val bBox: BoxCollider = requireNotNull(b.box)
    val aRotation = a.transform.rotation
    val bRotation = b.transform.rotation
    val aRotationVector = Vector3(aRotation.x, aRotation.y, aRotation.z)
    val bRotationVector = Vector3(bRotation.x, bRotation.y, bRotation.z)
    val relativeCross = bRotationVector.cross(aRotationVector)
    val relativeRotation = Quaternion(
        aRotation.x * bRotation.w - bRotation.x * aRotation.w - relativeCross.x,
        aRotation.y * bRotation.w - bRotation.y * aRotation.w - relativeCross.y,
        aRotation.z * bRotation.w - bRotation.z * aRotation.w - relativeCross.z,
        aRotation.x * bRotation.x + aRotation.y * bRotation.y +
                aRotation.z * bRotation.z + aRotation.w * bRotation.w
    )
    val aToB = relativeRotation.toMatrix3()
    val delta = a.transform.position.subtracted(b.transform.position)
    val aOffsetT = bRotationVector.cross(delta).scale(2.0f)
    val aOffsetU = bRotationVector.cross(aOffsetT)
    val aOffset = aOffsetU.added(delta).subtracted(aOffsetT.scale(bRotation.w))
    val bOffsetT = delta.cross(aRotationVector).scale(2.0f)
    val bOffsetU = aRotationVector.cross(bOffsetT)
    val bOffset = bOffsetU.subtracted(delta).subtracted(bOffsetT.scale(aRotation.w))
    val aSize = aBox.size
    val bSize = bBox.size
    val aPenetrations = FloatArray(3) { row: Int ->
        bSize[row] + abs(aToB.data[row * 3]) * aSize.x +
                abs(aToB.data[row * 3 + 1]) * aSize.y +
                abs(aToB.data[row * 3 + 2]) * aSize.z - abs(aOffset[row])
    }
    val bPenetrations = FloatArray(3) { column: Int ->
        aSize[column] + abs(aToB.data[column]) * bSize.x +
                abs(aToB.data[3 + column]) * bSize.y +
                abs(aToB.data[6 + column]) * bSize.z - abs(bOffset[column])
    }
    val pa = minFirst(aPenetrations[0], minFirst(aPenetrations[1], aPenetrations[2]))
    val pb = minFirst(bPenetrations[0], minFirst(bPenetrations[1], bPenetrations[2]))
    val facePenetration = minFirst(pa, pb)
    if (!(facePenetration > 0.0f)) {
        return emptyList()
    }

    var penetration = facePenetration
    var aEdge = 0
    var bEdge = 0
    for (i: Int in 0..2) {
        val ac = matrixRow(aToB, i)
        for (j: Int in 0..2) {
            val bc = matrixColumn(aToB, j)
            val next = (j + 1) % 3
            val previous = (j + 2) % 3
            val ra = ac[next] * ac[next] + ac[previous] * ac[previous]
            val bNext = (i + 1) % 3
            val bPrevious = (i + 2) % 3
            val rb = bc[bNext] * bc[bNext] + bc[bPrevious] * bc[bPrevious]
            if (ra <= BOX_AXIS_EPSILON || rb <= BOX_AXIS_EPSILON) {
                continue
            }
            val projectionA = abs(ac[next]) * aSize[previous] +
                    abs(ac[previous]) * aSize[next]
            val offset = abs(ac[next] * bOffset[previous] - ac[previous] * bOffset[next])
            val projectionB = abs(bc[bNext]) * bSize[bPrevious] +
                    abs(bc[bPrevious]) * bSize[bNext]
            val p = (projectionA - offset) / sqrt(ra) + projectionB / sqrt(rb)
            if (penetration > p) {
                penetration = p
                aEdge = j
                bEdge = i
            }
        }
    }

    if (!(penetration > 0.0f)) {
        return emptyList()
    }

    if (facePenetration > penetration + BOX_FACE_BIAS) {
        return collideBoxEdges(a, b, aBox, bBox, aEdge, bEdge, penetration)
    }

    val referenceA: WorldCollider
    val referenceB: WorldCollider
    val referenceABox: BoxCollider
    val referenceBBox: BoxCollider
    val referenceAToB: Matrix3
    val referenceBOffset: Vector3
    val referenceAFace: Int
    if (pa <= pb) {
        referenceA = b
        referenceB = a
        referenceABox = bBox
        referenceBBox = aBox
        referenceAToB = aToB.transposed()
        referenceBOffset = aOffset
        referenceAFace = leastPenetratingFace(aPenetrations)
    } else {
        referenceA = a
        referenceB = b
        referenceABox = aBox
        referenceBBox = bBox
        referenceAToB = aToB
        referenceBOffset = bOffset
        referenceAFace = leastPenetratingFace(bPenetrations)
    }

    return collideBoxFaces(
        referenceA,
        referenceB,
        referenceABox,
        referenceBBox,
        referenceAToB,
        referenceBOffset,
        referenceAFace
    )
}

private fun collideBoxFaces(
    a: WorldCollider,
    b: WorldCollider,
    aBox: BoxCollider,
    bBox: BoxCollider,
    aToB: Matrix3,
    bOffset: Vector3,
    aFace: Int
): List<Contact> {
    val aSize = aBox.size
    val bSize = bBox.size
    val directions = matrixColumn(aToB, aFace)
    val absoluteDirections = Vector3(abs(directions.x), abs(directions.y), abs(directions.z))
    var bFace = 0
    var largestDirection = absoluteDirections.x
    for (i: Int in 1..2) {
        if (absoluteDirections[i] >= largestDirection) {
            largestDirection = absoluteDirections[i]
            bFace = i
        }
    }

    val bAxes = arrayOf(
        matrixRow(aToB, 0).scale(bSize.x),
        matrixRow(aToB, 1).scale(bSize.y),
        matrixRow(aToB, 2).scale(bSize.z)
    )
    val incidentNormal: Vector3
    val incidentX: Vector3
    val incidentY: Vector3
    if (bFace == 2) {
        incidentNormal = bAxes[2]
        incidentX = bAxes[0]
        incidentY = bAxes[1]
    } else {
        if (bFace == 1) {
            incidentNormal = bAxes[1]
            incidentX = bAxes[2]
            incidentY = bAxes[0]
        } else {
            incidentNormal = bAxes[0]
            incidentX = bAxes[1]
            incidentY = bAxes[2]
        }
    }

    val bOffsetNegative = signBit(bOffset[aFace])
    val bPositiveFace = signBit(bOffset[aFace]) != signBit(incidentNormal[aFace])
    var center = incidentNormal
    if (!bPositiveFace) {
        center = center.scale(-1.0f)
    }
    center = center.added(bOffset)

    val xAxis = (aFace + 1) % 3
    val yAxis = (aFace + 2) % 3
    val sx = aSize[xAxis]
    val sy = aSize[yAxis]
    val cx = center[xAxis]
    val cy = center[yAxis]
    val dxx = incidentX[xAxis]
    val dxy = incidentX[yAxis]
    val dyx = incidentY[xAxis]
    val dyy = incidentY[yAxis]
    val supportX = FloatArray(16) { index: Int -> 0.0f }
    val supportY = FloatArray(16) { index: Int -> 0.0f }
    val supportZ = FloatArray(16) { index: Int -> 0.0f }
    val valid = BooleanArray(16) { index: Int -> false }
    val aInside = BooleanArray(4) { index: Int -> false }
    val bInside = BooleanArray(4) { index: Int -> false }
    val determinant = dxx * dyy - dxy * dyx
    val deltaMax = abs(determinant)
    val ox = cx * dyy - cy * dyx
    val oy = cx * dxy - cy * dxx

    for (i: Int in 0..3) {
        val npnp: Float
        if (i == 1 || i == 3) {
            npnp = 1.0f
        } else {
            npnp = -1.0f
        }
        val nnpp: Float
        if (i >= 2) {
            nnpp = 1.0f
        } else {
            nnpp = -1.0f
        }
        supportX[i] = -npnp * sx
        supportY[i] = nnpp * sy
        val deltaX = ox + dyx * sy * nnpp + dyy * sx * npnp
        val deltaY = oy + dxx * sy * nnpp + dxy * sx * npnp
        aInside[i] = maxOf(abs(deltaX), abs(deltaY)) <= deltaMax
        valid[i] = aInside[i]

        supportX[4 + i] = cx + dxx * npnp + dyx * nnpp
        supportY[4 + i] = cy + dxy * npnp + dyy * nnpp
        bInside[i] = abs(supportX[4 + i]) <= sx && abs(supportY[4 + i]) <= sy
        valid[4 + i] = bInside[i]
    }

    val aEdgesInside = booleanArrayOf(
        aInside[3] && aInside[1],
        aInside[2] && aInside[0],
        aInside[0] && aInside[1],
        aInside[2] && aInside[3]
    )
    val bEdgesInside = booleanArrayOf(
        bInside[0] && bInside[1],
        bInside[2] && bInside[3],
        bInside[0] && bInside[2],
        bInside[1] && bInside[3]
    )
    val edgeAxisNear = BooleanArray(4) { index: Int -> false }
    val edgeAxisFar = BooleanArray(4) { index: Int -> false }

    for (i: Int in 0..3) {
        val offsetX: Float
        val offsetY: Float
        val pivotX: Float
        val pivotY: Float
        if (i < 2) {
            offsetX = dxx
            offsetY = dxy
            if (i == 0) {
                pivotX = cx - dyx
                pivotY = cy - dyy
            } else {
                pivotX = cx + dyx
                pivotY = cy + dyy
            }
        } else {
            offsetX = dyx
            offsetY = dyy
            if (i == 2) {
                pivotX = cx - dxx
                pivotY = cy - dxy
            } else {
                pivotX = cx + dxx
                pivotY = cy + dxy
            }
        }

        val posX = withSign(sx, offsetX)
        val posY = withSign(sy, offsetY)
        val reciprocalX = 1.0f / offsetX
        val reciprocalY = 1.0f / offsetY
        val nearX = (posX + pivotX) * reciprocalX
        val farX = (posX - pivotX) * reciprocalX
        val nearY = (posY + pivotY) * reciprocalY
        val farY = (posY - pivotY) * reciprocalY
        var near = minFirst(1.0f, nearX)
        var far = minFirst(1.0f, farX)
        edgeAxisNear[i] = near > nearY
        edgeAxisFar[i] = far > farY
        near = minFirst(near, nearY)
        far = minFirst(far, farY)
        val intervalValid = near + far > 0.0f
        supportX[8 + i] = pivotX - offsetX * near
        supportY[8 + i] = pivotY - offsetY * near
        supportX[12 + i] = pivotX + offsetX * far
        supportY[12 + i] = pivotY + offsetY * far
        valid[8 + i] = near != 1.0f && intervalValid && !aEdgesInside[i]
        valid[12 + i] = far != 1.0f && intervalValid && !bEdgesInside[i]
    }

    val aSignFaceBit: Int
    if (bOffsetNegative) {
        aSignFaceBit = 1 shl aFace
    } else {
        aSignFaceBit = 0
    }
    val bSignFaceBit: Int
    if (bPositiveFace) {
        bSignFaceBit = 0
    } else {
        bSignFaceBit = 1 shl bFace
    }
    var edgeAxisWinding = 0
    val directions2D = floatArrayOf(dxx, dxy, dyx, dyy)
    for (i: Int in 0..3) {
        if (signBit(directions2D[i])) {
            edgeAxisWinding = edgeAxisWinding or (1 shl i)
        }
    }
    val supportTags = boxFaceSupportTags(
        aFace,
        bFace,
        aSignFaceBit,
        bSignFaceBit,
        edgeAxisWinding,
        edgeAxisNear,
        edgeAxisFar
    )

    val planeNormal = incidentX.cross(incidentY)
    val planeDenominator = planeNormal[aFace]
    if (planeDenominator == 0.0f || !planeDenominator.isFinite()) {
        return emptyList()
    }
    val planeX = -planeNormal[xAxis] / planeDenominator
    val planeY = -planeNormal[yAxis] / planeDenominator
    val planeZ = center.dot(planeNormal) / planeDenominator
    for (i: Int in 0..15) {
        if (!valid[i]) {
            continue
        }
        val z = supportX[i] * planeX + supportY[i] * planeY + planeZ
        val signedZ: Float
        val midpointScale: Float
        if (bOffsetNegative) {
            signedZ = -z
            midpointScale = -0.5f
        } else {
            signedZ = z
            midpointScale = 0.5f
        }
        val contactPenetration = aSize[aFace] - signedZ
        supportZ[i] = z + contactPenetration * midpointScale
        valid[i] = contactPenetration > 0.0f && contactPenetration.isFinite() && supportZ[i].isFinite()
    }

    val aToWorld = a.transform.rotation.toMatrix3()
    var worldNormal = matrixColumn(aToWorld, aFace)
    if (bOffsetNegative) {
        worldNormal = worldNormal.scale(-1.0f)
    }
    var bodyA = a.body
    var bodyB = b.body
    var tagA = a.tag
    var tagB = b.tag
    var tagSwap = false
    if (b.tag > a.tag) {
        val temporaryBody = bodyA
        bodyA = bodyB
        bodyB = temporaryBody
        val temporaryTag = tagA
        tagA = tagB
        tagB = temporaryTag
        tagSwap = true
        worldNormal = worldNormal.scale(-1.0f)
    }

    val contacts = mutableListOf<Contact>()
    for (i: Int in 0..15) {
        if (!valid[i]) {
            continue
        }
        val localPosition: Vector3
        if (aFace == 0) {
            localPosition = Vector3(supportZ[i], supportX[i], supportY[i])
        } else {
            if (aFace == 1) {
                localPosition = Vector3(supportY[i], supportZ[i], supportX[i])
            } else {
                localPosition = Vector3(supportX[i], supportY[i], supportZ[i])
            }
        }
        val worldPosition = aToWorld.vecMultiply(localPosition).added(a.transform.position)
        if (!worldPosition.x.isFinite() || !worldPosition.y.isFinite() || !worldPosition.z.isFinite()) {
            continue
        }
        var featureTag = supportTags[i]
        if (tagSwap) {
            featureTag = swappedFeatureTag(featureTag)
        }
        val z = supportX[i] * planeX + supportY[i] * planeY + planeZ
        val signedZ: Float
        if (bOffsetNegative) {
            signedZ = -z
        } else {
            signedZ = z
        }
        contacts.add(
            Contact(
                worldPosition,
                worldNormal,
                aSize[aFace] - signedZ,
                0.5f,
                BodyPair(bodyA, bodyB),
                packedContactTag(featureTag, tagA, tagB)
            )
        )
    }
    return contacts
}

private fun collideBoxEdges(
    originalA: WorldCollider,
    originalB: WorldCollider,
    originalABox: BoxCollider,
    originalBBox: BoxCollider,
    originalAEdge: Int,
    originalBEdge: Int,
    penetration: Float
): List<Contact> {
    val a: WorldCollider
    val b: WorldCollider
    val aBox: BoxCollider
    val bBox: BoxCollider
    val aEdge: Int
    val bEdge: Int
    if (originalA.tag > originalB.tag) {
        a = originalA
        b = originalB
        aBox = originalABox
        bBox = originalBBox
        aEdge = originalAEdge
        bEdge = originalBEdge
    } else {
        a = originalB
        b = originalA
        aBox = originalBBox
        bBox = originalABox
        aEdge = originalBEdge
        bEdge = originalAEdge
    }

    val aBasis = a.transform.rotation.toMatrix3()
    val bBasis = b.transform.rotation.toMatrix3()
    val u = matrixColumn(aBasis, aEdge)
    val v = matrixColumn(bBasis, bEdge)
    var normal = u.cross(v)
    val delta = b.transform.position.subtracted(a.transform.position)
    if (signBit(normal.dot(delta))) {
        normal = normal.scale(-1.0f)
    }
    val normalLengthSquared = normal.lengthSquared()
    if (!(normalLengthSquared > 0.0f) || !normalLengthSquared.isFinite()) {
        return emptyList()
    }

    val aLocalNormal = aBasis.vecMultiplyTransposed(normal)
    val bLocalNormal = bBasis.vecMultiplyTransposed(normal)
    var aSigns = 0
    var bSigns = 0
    for (i: Int in 0..2) {
        if (signBit(aLocalNormal[i])) {
            aSigns = aSigns or (1 shl i)
        }
        if (signBit(bLocalNormal[i])) {
            bSigns = bSigns or (1 shl i)
        }
    }
    val featureTag = boxEdgeFeatureTag(aEdge, bEdge, aSigns, bSigns)
    val aSignedSize = Vector3(
        withSign(aBox.size.x, aLocalNormal.x),
        withSign(aBox.size.y, aLocalNormal.y),
        withSign(aBox.size.z, aLocalNormal.z)
    )
    val bSignedSize = Vector3(
        withSign(bBox.size.x, bLocalNormal.x),
        withSign(bBox.size.y, bLocalNormal.y),
        withSign(bBox.size.z, bLocalNormal.z)
    )
    val aCenter = matrixColumn(aBasis, 0).scale(aSignedSize.x)
            .added(matrixColumn(aBasis, 1).scale(aSignedSize.y))
            .added(matrixColumn(aBasis, 2).scale(aSignedSize.z))
            .added(a.transform.position)
    val bCenter = matrixColumn(bBasis, 0).scale(bSignedSize.x)
            .added(matrixColumn(bBasis, 1).scale(bSignedSize.y))
            .added(matrixColumn(bBasis, 2).scale(bSignedSize.z))
            .subtracted(b.transform.position)
    val offset = aCenter.added(bCenter)
    val ia = u.dot(u)
    val ib = u.dot(v)
    val ic = v.dot(v)
    val id = offset.dot(u)
    val ie = offset.dot(v)
    val denominator = ia * ic - ib * ib
    if (denominator == 0.0f || !denominator.isFinite()) {
        return emptyList()
    }
    val inverse = 0.5f / denominator
    val sa = (ib * ie - ic * id) * inverse
    val sb = (ia * ie - ib * id) * inverse
    val position = aCenter.subtracted(bCenter).scale(0.5f)
            .added(u.scale(sa))
            .added(v.scale(sb))
    if (!position.x.isFinite() || !position.y.isFinite() || !position.z.isFinite()) {
        return emptyList()
    }
    normal = normal.scale(1.0f / sqrt(normalLengthSquared))
    return listOf(
        Contact(
            position,
            normal,
            penetration,
            0.5f,
            BodyPair(a.body, b.body),
            packedContactTag(featureTag, a.tag, b.tag)
        )
    )
}

private fun leastPenetratingFace(penetrations: FloatArray): Int {
    var face = 0
    var penetration = penetrations[0]
    for (i: Int in 1..2) {
        if (penetrations[i] <= penetration) {
            penetration = penetrations[i]
            face = i
        }
    }
    return face
}

private fun matrixColumn(matrix: Matrix3, column: Int): Vector3 {
    return Vector3(matrix.data[column], matrix.data[3 + column], matrix.data[6 + column])
}

private fun matrixRow(matrix: Matrix3, row: Int): Vector3 {
    return Vector3(matrix.data[row * 3], matrix.data[row * 3 + 1], matrix.data[row * 3 + 2])
}

private fun minFirst(a: Float, b: Float): Float {
    if (b < a) {
        return b
    }
    return a
}

private fun signBit(value: Float): Boolean {
    return value.toRawBits() < 0
}

private fun withSign(value: Float, signSource: Float): Float {
    if (signBit(signSource)) {
        return -value
    }
    return value
}
