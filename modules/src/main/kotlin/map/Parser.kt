package map

import kotlinx.serialization.json.*

data class Meta(val version: Int)
data class MapJson(
    val name: String,
    val graph: List<GraphEntry>,
    val lights: List<JsonElement>,
    val cameras: List<JsonElement>
)

data class Root(
    val meta: Meta,
    val map: MapJson
)

data class GraphEntry(val node: NodeWrapper)
data class NodeWrapper(
    val type: String,
    val payload: JsonObject?
)

fun parseMapJson(jsonString: String): MapData {
    val parser = Json { ignoreUnknownKeys = true }
    val root = parser.decodeFromString<Root>(jsonString)

    val graphData = root.map.graph.map { parseNode(it.node) }

    return MapData(
        name = root.map.name,
        graph = graphData,
        lights = root.map.lights,
        cameras = root.map.cameras
    )
}

fun parseNode(nodeWrapper: NodeWrapper): NodeData {
    val payloadJson = nodeWrapper.payload ?: return GenericNodeData(nodeWrapper.type)

    return when (nodeWrapper.type) {
        "state_group" -> {
            val payload = payloadJson.jsonObject
            val program = payload["program"]?.jsonPrimitive?.content ?: ""
            val children = payload["children"]?.jsonArray?.map {
                parseNode(it.jsonObject.parseNodeWrapper())
            }?.toMutableList() ?: mutableListOf()

            StateGroupData(
                type = nodeWrapper.type,
                payload = StateGroupPayload(program, children)
            )
        }

        "group" -> {
            val payload = payloadJson.jsonObject
            val children = payload["children"]?.jsonArray?.map {
                parseNode(it.jsonObject.parseNodeWrapper())
            }?.toMutableList() ?: mutableListOf()

            GroupData(
                type = nodeWrapper.type,
                payload = GroupPayload(children)
            )
        }

        "transform" -> {
            val payload = payloadJson.jsonObject
            val matrix = payload["matrix"]?.jsonArray?.map { it.jsonPrimitive.float }?.toFloatArray() ?: floatArrayOf()
            val transformType =
                payload["transform_type"]?.jsonPrimitive?.content ?: payload["type"]?.jsonPrimitive?.content ?: ""
            val data = payload["data"]?.jsonArray?.map { it.jsonPrimitive.float }?.toFloatArray() ?: floatArrayOf()
            val childWrapper = payload["child"]?.jsonObject?.parseNodeWrapper()
                ?: return TransformData(
                    nodeWrapper.type,
                    TransformPayload(transformType, matrix, data, DummyNodeData())
                )

            val child = parseNode(childWrapper)
            TransformData(
                type = nodeWrapper.type,
                payload = TransformPayload(transformType, matrix, data, child)
            )
        }

        "drawable", "spinable_drawable" -> {
            val payload = payloadJson.jsonObject
            val mesh = payload["mesh"]?.jsonPrimitive?.content ?: ""
            val material = payload["material"]?.jsonPrimitive?.content ?: ""
            val data = payload["data"]?.jsonArray?.map { it.jsonPrimitive.float }?.toFloatArray() ?: floatArrayOf()

            Drawable(
                type = nodeWrapper.type,
                payload = DrawablePayload(mesh, material, data)
            )
        }

        else -> {
            GenericNodeData(nodeWrapper.type)
        }
    }
}

fun JsonObject.parseNodeWrapper(): NodeWrapper {
    val type = this["type"]?.jsonPrimitive?.content ?: ""
    val payload = this["payload"]?.jsonObject
    return NodeWrapper(type, payload)
}

class DummyNodeData : NodeData("dummy")

class GenericNodeData(override val type: String) : NodeData(type)

data class MapData(
    val name: String,
    val graph: List<NodeData>,
    val lights: List<JsonElement>,
    val cameras: List<JsonElement>
)
