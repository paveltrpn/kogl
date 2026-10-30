package nudge

internal fun packedContactTag(featureTag: UInt, aTag: Int, bTag: Int): ULong {
    return featureTag.toULong() or
            (aTag.toULong() shl 32) or
            (bTag.toULong() shl 48)
}

internal fun swappedFeatureTag(tag: UInt): UInt {
    return (tag shr 16) or (tag shl 16)
}

internal fun boxEdgeFeatureTag(aEdge: Int, bEdge: Int, aSigns: Int, bSigns: Int): UInt {
    val edgeBits = (1 shl aEdge) or ((1 shl bEdge) shl 16)
    var high = aSigns or ((bSigns xor 0x7) shl 16) or edgeBits
    val low = high and edgeBits.inv()
    high = high shl 8
    return (low or high).toUInt()
}

internal fun boxFaceSupportTags(
    aFace: Int,
    bFace: Int,
    aSignFaceBit: Int,
    bSignFaceBit: Int,
    edgeAxisWinding: Int,
    edgeAxisNear: BooleanArray,
    edgeAxisFar: BooleanArray
): UIntArray {
    val tags = UIntArray(16) { index: Int -> 0u }
    val aVertices = 0x12003624u shr (3 - aFace)
    val bVertices = 0x00122436u shr (3 - bFace)
    val aFaceBits = 0xffff0000u or aSignFaceBit.toUInt()
    val bFaceBits = 0x0000ffffu or (bSignFaceBit.toUInt() shl 16)

    for (i: Int in 0..3) {
        tags[i] = ((aVertices shr (i * 8)) and 0x7u) or aFaceBits
    }

    tags[4] = ((bVertices shl 16) and 0x70000u) or bFaceBits
    tags[5] = ((bVertices shl 8) and 0x70000u) or bFaceBits
    tags[6] = (bVertices and 0x70000u) or bFaceBits
    tags[7] = ((bVertices shr 8) and 0x70000u) or bFaceBits

    val aEdgeMap = 0x1200362424003612uL shr (3 - aFace)
    val bEdgeMap = 0x2400361212003624uL shr (3 - bFace)
    val faceBits = aSignFaceBit or (aSignFaceBit shl 8) or
            (bSignFaceBit shl 16) or (bSignFaceBit shl 24)
    val bEdges = UIntArray(4) { index: Int ->
        ((((bEdgeMap shr (index * 16)) and 0x0707uL).toUInt()) shl 16) or faceBits.toUInt()
    }

    for (i: Int in 0..3) {
        val axisOffset: Int
        if (i < 2) {
            axisOffset = 0
        } else {
            axisOffset = 2
        }
        val nearAxis: Int
        if (edgeAxisNear[i]) {
            nearAxis = 1
        } else {
            nearAxis = 0
        }
        val farAxis: Int
        if (edgeAxisFar[i]) {
            farAxis = 1
        } else {
            farAxis = 0
        }
        val nearEdge = nearAxis * 2 + ((edgeAxisWinding shr (axisOffset + nearAxis)) and 1)
        val farEdge = farAxis * 2 + (((edgeAxisWinding xor 0xf) shr (axisOffset + farAxis)) and 1)
        val aNear = ((aEdgeMap shr (nearEdge * 16)) and 0x0707uL).toUInt()
        val aFar = ((aEdgeMap shr (farEdge * 16)) and 0x0707uL).toUInt()
        tags[8 + i] = aNear or bEdges[i]
        tags[12 + i] = aFar or bEdges[i]
    }

    return tags
}
