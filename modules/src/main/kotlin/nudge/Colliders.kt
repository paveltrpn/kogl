package nudge

import algebra.*

data class BoxCollider(
    val size: Vector3
) {
    init {
        require(size.x >= 0.0f && size.y >= 0.0f && size.z >= 0.0f)
        require(size.x.isFinite() && size.y.isFinite() && size.z.isFinite())
    }
}

data class SphereCollider(
    val radius: Float
) {
    init {
        require(radius >= 0.0f && radius.isFinite())
    }
}

class BoxColliders(
    val tags: MutableList<Int> = mutableListOf(),
    val data: MutableList<BoxCollider> = mutableListOf(),
    val transforms: MutableList<Transform> = mutableListOf()
) {
    val count: Int
        get(): Int {
            return data.size
        }

    fun validate(): Unit {
        validateParallelStorage(tags.size, data.size, transforms.size)
        validateTags(tags)
        for (box: BoxCollider in data) {
            validateHalfExtents(box.size)
        }
    }
}

class SphereColliders(
    val tags: MutableList<Int> = mutableListOf(),
    val data: MutableList<SphereCollider> = mutableListOf(),
    val transforms: MutableList<Transform> = mutableListOf()
) {
    val count: Int
        get(): Int {
            return data.size
        }

    fun validate(): Unit {
        validateParallelStorage(tags.size, data.size, transforms.size)
        validateTags(tags)
    }
}

class ColliderData(
    val boxes: BoxColliders = BoxColliders(),
    val spheres: SphereColliders = SphereColliders()
) {
    fun validate(): Unit {
        boxes.validate()
        spheres.validate()
        val allTags: List<Int> = boxes.tags + spheres.tags
        require(allTags.toSet().size == allTags.size) { "Collider tags must be globally unique" }
    }
}

internal class WorldCollider(
    val index: Int,
    val tag: Int,
    val transform: Transform,
    val box: BoxCollider?,
    val sphere: SphereCollider?,
    val minimum: Vector3,
    val maximum: Vector3
) {
    val body: Int
        get(): Int {
            return transform.body
        }
}

internal fun composeTransform(parent: Transform, local: Transform): Transform {
    return Transform(
        parent.rotation.rotate(local.position).added(parent.position),
        parent.rotation * local.rotation,
        local.body
    )
}

private fun validateParallelStorage(tagCount: Int, shapeCount: Int, transformCount: Int): Unit {
    require(tagCount == shapeCount && transformCount == shapeCount)
}

private fun validateTags(tags: List<Int>): Unit {
    require(tags.all { tag: Int -> tag in 0..0xffff }) { "Collider tags must fit unsigned 16 bits" }
    require(tags.toSet().size == tags.size) { "Collider tags must be unique" }
}

private fun validateHalfExtents(size: Vector3): Unit {
    require(size.x >= 0.0f && size.x.isFinite())
    require(size.y >= 0.0f && size.y.isFinite())
    require(size.z >= 0.0f && size.z.isFinite())
}
