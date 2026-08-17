package map

import graph.*
import algebra.*
import mesh.*
import render.*

fun buildStateGroups(mapData: MapData, meshStorage: Map<String, Mesh>): List<StateGroup> {
    val stateGroups = mutableListOf<StateGroup>()

    for (graphEntry in mapData.graph) {
        val stateGroup = buildStateGroup(graphEntry, meshStorage)
        stateGroups.add(stateGroup)
    }

    return stateGroups
}

private fun buildStateGroup(nodeData: NodeData, meshStorage: Map<String, Mesh>): StateGroup {
    if (nodeData !is StateGroupData) {
        throw IllegalArgumentException("Expected StateGroupData, got ${nodeData.type}")
    }

    val programSource = ShaderSource(nodeData.payload.program)
    val program = Program().apply {
        source(programSource)
        build()
    }
    val stateGroup = StateGroup(program)

    for (childData in nodeData.payload.children) {
        val childNode = buildNode(childData, meshStorage)
        if (childNode != null) {
            stateGroup.addChild(childNode)
        }
    }

    return stateGroup
}

private fun buildNode(nodeData: NodeData, meshStorage: Map<String, Mesh>): Node? {
    return when (nodeData) {
        is StateGroupData -> null
        is GroupData -> buildGroup(nodeData, meshStorage)
        is TransformData -> buildTransform(nodeData, meshStorage)
        is DrawableData -> buildDrawable(nodeData, meshStorage)
        is SpinableDrawableData -> buildSpinableDrawable(nodeData, meshStorage)
        is FlyaroundDrawableData -> buildFlyaroundDrawable(nodeData, meshStorage)
        is GenericNodeData -> null
        else -> null
    }
}

private fun buildGroup(groupData: GroupData, meshStorage: Map<String, Mesh>): Group {
    val group = Group()

    for (childData in groupData.payload.children) {
        val childNode = buildNode(childData, meshStorage)
        if (childNode != null) {
            group.addChild(childNode)
        }
    }

    return group
}

private fun buildTransform(transformData: TransformData, meshStorage: Map<String, Mesh>): Transform {
    val transform = Transform()
    val payload = transformData.payload

    val matrixArray = payload.matrix
    if (matrixArray.size == 16) {
        val matrix = Matrix4()
        val data = matrix.data
        for (i in 0..15) {
            data[i] = matrixArray[i]
        }
        transform.matrix = matrix
    }

    val childNode = buildNode(payload.child, meshStorage)
    if (childNode != null) {
        transform.addChild(childNode)
    }

    return transform
}

private fun buildDrawable(drawableData: DrawableData, meshStorage: Map<String, Mesh>): Drawable? {
    val mesh = meshStorage[drawableData.payload.mesh] ?: return null
    return Drawable(mesh)
}

private fun buildSpinableDrawable(spinableData: SpinableDrawableData, meshStorage: Map<String, Mesh>): Drawable? {
    val mesh = meshStorage[spinableData.payload.mesh] ?: return null
    val spinable = SpinableDrawable(mesh)

    val axisArray = spinableData.payload.axis
    if (axisArray.size >= 3) {
        spinable.axis = Vector3(axisArray[0], axisArray[1], axisArray[2])
    }

    spinable.anglSpeed = spinableData.payload.anglSpeed

    return spinable
}

private fun buildFlyaroundDrawable(flyaroundData: FlyaroundDrawableData, meshStorage: Map<String, Mesh>): Drawable? {
    val mesh = meshStorage[flyaroundData.payload.mesh] ?: return null
    val flyaround = FlyaroundDrawable(mesh)

    val originArray = flyaroundData.payload.origin
    if (originArray.size >= 3) {
        flyaround.origin = Vector3(originArray[0], originArray[1], originArray[2])
    }

    val axisArray = flyaroundData.payload.axis
    if (axisArray.size >= 3) {
        flyaround.axis = Vector3(axisArray[0], axisArray[1], axisArray[2])
    }

    flyaround.anglSpeed = flyaroundData.payload.anglSpeed

    return flyaround
}