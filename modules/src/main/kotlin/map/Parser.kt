package map

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class Meta(val version: Int)

@Serializable
data class MapJson(
    val name: String,
    val graph: List<GraphEntry>,
    val lights: List<JsonElement>,
    val cameras: List<JsonElement>
)

@Serializable
data class Root(
    val meta: Meta,
    val map: MapJson
)

@Serializable
data class GraphEntry(val node: NodeWrapper)

@Serializable
data class NodeWrapper(
    val type: String,
    val payload: JsonObject?
)

class DummyNodeData : NodeData("dummy")

class GenericNodeData(override val type: String) : NodeData(type)

data class MapData(
    val name: String,
    val graph: List<NodeData>,
    val lights: List<JsonElement>,
    val cameras: List<JsonElement>
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
            val children = payload["children"]?.jsonArray?.map { childElem ->
                val nodeObj = (childElem as? JsonObject)?.get("node") as? JsonObject
                if (nodeObj != null) {
                    parseNode(nodeObj.parseNodeWrapper())
                } else {
                    GenericNodeData("unknown")
                }
            }?.toMutableList() ?: mutableListOf()

            StateGroupData(
                type = nodeWrapper.type,
                payload = StateGroupPayload(program, children)
            )
        }

        "group" -> {
            val payload = payloadJson.jsonObject
            val children = payload["children"]?.jsonArray?.map { childElem ->
                val nodeObj = (childElem as? JsonObject)?.get("node") as? JsonObject
                if (nodeObj != null) {
                    parseNode(nodeObj.parseNodeWrapper())
                } else {
                    GenericNodeData("unknown")
                }
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
            
            val child = when (val childVal = payload["child"]) {
                is JsonObject -> {
                    val nodeVal = childVal["node"]
                    when (nodeVal) {
                        is JsonObject -> {
                            parseNode(nodeVal.parseNodeWrapper())
                        }
                        is JsonPrimitive -> {
                            // child.node is a string like "drawable"
                            GenericNodeData(nodeVal.jsonPrimitive.content)
                        }
                        else -> DummyNodeData()
                    }
                }
                is JsonPrimitive -> {
                    GenericNodeData(childVal.jsonPrimitive.content)
                }
                else -> DummyNodeData()
            }

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

            DrawableData(
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
