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
        throw RuntimeException("Expected StateGroupData, got ${nodeData.type}")
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
        is GroupData -> {
            buildGroup(nodeData, meshStorage)
        }

        is TransformData -> {
            buildTransform(nodeData, meshStorage)
        }

        is DrawableData -> {
            buildDrawable(nodeData, meshStorage)
        }

        is FlyaroundDrawableData -> {
            buildFlyaroundDrawable(nodeData, meshStorage)
        }

        is GenericNodeData -> {
            null
        }

        is StateGroupData -> {
            null
        }

        else -> {
            null
        }
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

private fun buildTransform(transformData: TransformData, meshStorage: Map<String, Mesh>): TransformGroup {
    fun constructTransform(type: String, rawMatrix: FloatArray, rawData: FloatArray): TransformGroup {
        if (!rawMatrix.isEmpty()) {
            if (rawMatrix.size != 16) throw RuntimeException("Transform matrix array wrong size - \"${rawMatrix.size}\"!")
            return TransformGroup(rawMatrix)
        }

        when (type) {
            "offset" -> {
                if (rawData.size != 3) throw RuntimeException("Transform data array wrong size - \"${rawData.size}\"!")
                return TransformGroup().apply {
                    matrix = algebra.offset(rawData[0], rawData[1], rawData[2])
                }
            }

            "rotateEuler" -> {
                if (rawData.size != 3) throw RuntimeException("Transform data array wrong size - \"${rawData.size}\"!")
                return TransformGroup().apply {
                    matrix = algebra.rotation(rawData[0], rawData[1], rawData[2])
                }
            }

            "rotateAxisAngl" -> {
                if (rawData.size != 4) throw RuntimeException("Transform data array wrong size - \"${rawData.size}\"!")
                val ax = Vector3(rawData[0], rawData[1], rawData[2])
                val an = rawData[3]
                return TransformGroup().apply {
                    matrix = algebra.rotation(ax, an)
                }
            }

            "scale" -> {
                if (rawData.size != 3) throw RuntimeException("Transform data array wrong size - \"${rawData.size}\"!")
                return TransformGroup().apply {
                    matrix = algebra.scale(rawData[0], rawData[1], rawData[2])
                }
            }

        }

        return TransformGroup()
    }

    val p = transformData.payload

    val transform = constructTransform(p.trnasform_type, p.matrix, p.data)

    val childNode = buildNode(p.child, meshStorage)
    if (childNode != null) {
        transform.addChild(childNode)
    }

    return transform
}

private fun buildDrawable(drawableData: DrawableData, meshStorage: Map<String, Mesh>): Drawable? {
    val mesh = meshStorage[drawableData.payload.mesh] ?: return null
    return Drawable(mesh)
}

private fun buildFlyaroundDrawable(flyaroundData: FlyaroundDrawableData, meshStorage: Map<String, Mesh>): Drawable? {
    val p = flyaroundData.payload

    if (meshStorage[p.mesh] == null) {
        throw RuntimeException("FlyaroundDrawable no such mesh - \"${p.mesh}\"!")
    }

    val originArray = p.origin
    if (originArray.size != 3) throw RuntimeException("FlyaroundDrawable origin array wrong size - \"${originArray.size}\"!")

    val axisArray = p.axis
    if (axisArray.size != 3) throw RuntimeException("FlyaroundDrawable axis array wrong size - \"${axisArray.size}\"!")

    val ax = Vector3(axisArray[0], axisArray[1], axisArray[2])
    if (ax.length() == 0.0f) throw RuntimeException("FlyaroundDrawable axis length is zero!")

    ax.normalize()

    return FlyaroundDrawable(meshStorage[p.mesh]!!).apply {
        origin = Vector3(originArray[0], originArray[1], originArray[2])
        axis = ax
        anglSpeed = p.anglSpeed
    }
}

fun printNode(node: NodeData, indent: String = "") {
    println("${indent}type=${node.type}")
    when (node) {
        is StateGroupData -> {
            println("${indent}  program: ${node.payload.program}")
            for (child in node.payload.children) {
                printNode(child, indent + "  ")
            }
        }

        is GroupData -> {
            for (child in node.payload.children) {
                printNode(child, indent + "  ")
            }
        }

        is TransformData -> {
            println("${indent}  transform_type: ${node.payload.trnasform_type}")
            println("${indent}  matrix: ${node.payload.matrix.contentToString()}")
            println("${indent}  data: ${node.payload.data.contentToString()}")
            printNode(node.payload.child, indent + "  ")
        }

        is DrawableData -> {
            println("${indent}  mesh: ${node.payload.mesh}")
            println("${indent}  material: ${node.payload.material}")
        }

        is FlyaroundDrawableData -> {
            println("${indent}  mesh: ${node.payload.mesh}")
            println("${indent}  material: ${node.payload.material}")
            println("${indent}  origin: ${node.payload.origin.contentToString()}")
            println("${indent}  axis: ${node.payload.axis.contentToString()}")
            println("${indent}  anglSpeed: ${node.payload.anglSpeed}")
        }

        is GenericNodeData -> {
            // Generic node with no payload
        }
    }
}

fun printMapStructure(mapData: MapData): Unit {
    println("Map name: ${mapData.name}")
    println("Graph entries: ${mapData.graph.size}")
    for ((index, node) in mapData.graph.withIndex()) {
        println("Graph[$index]:")
        printNode(node, "  ")
    }
}