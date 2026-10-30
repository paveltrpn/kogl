package nudge

import algebra.*

data class Transform(
    var position: Vector3 = Vector3(),
    var rotation: Quaternion = Quaternion(),
    var body: Int = 0
) {
    init {
        require(body >= 0)
    }
}

data class BodyProperties(
    var inertiaInverse: Vector3 = Vector3(),
    var massInverse: Float = 0.0f
) {
    init {
        require(massInverse >= 0.0f && massInverse.isFinite())
        require(inertiaInverse.x >= 0.0f && inertiaInverse.x.isFinite())
        require(inertiaInverse.y >= 0.0f && inertiaInverse.y.isFinite())
        require(inertiaInverse.z >= 0.0f && inertiaInverse.z.isFinite())
    }
}

data class BodyMomentum(
    var velocity: Vector3 = Vector3(),
    var angularVelocity: Vector3 = Vector3()
)

data class BodyPair(
    val a: Int,
    val b: Int
) {
    init {
        require(a >= 0 && b >= 0)
    }
}

class BodyData(
    val transforms: MutableList<Transform>,
    val properties: MutableList<BodyProperties>,
    val momentum: MutableList<BodyMomentum>,
    val idleCounters: MutableList<Int>
) {
    constructor(count: Int) : this(
        MutableList(count) { index: Int -> Transform(body = index) },
        MutableList(count) { index: Int -> BodyProperties() },
        MutableList(count) { index: Int -> BodyMomentum() },
        MutableList(count) { index: Int -> 0 }
    ) {
        require(count >= 0)
    }

    val count: Int
        get(): Int {
            return transforms.size
        }

    fun validate(): Unit {
        require(properties.size == transforms.size)
        require(momentum.size == transforms.size)
        require(idleCounters.size == transforms.size)
        for (counter: Int in idleCounters) {
            require(counter in 0..255)
        }
        for (bodyProperties: BodyProperties in properties) {
            require(bodyProperties.massInverse >= 0.0f && bodyProperties.massInverse.isFinite())
            require(bodyProperties.inertiaInverse.x >= 0.0f && bodyProperties.inertiaInverse.x.isFinite())
            require(bodyProperties.inertiaInverse.y >= 0.0f && bodyProperties.inertiaInverse.y.isFinite())
            require(bodyProperties.inertiaInverse.z >= 0.0f && bodyProperties.inertiaInverse.z.isFinite())
        }
    }
}

class BodyConnections(
    val data: MutableList<BodyPair> = mutableListOf()
) {
    val count: Int
        get(): Int {
            return data.size
        }
}

class ActiveBodies(
    val indices: MutableList<Int> = mutableListOf()
) {
    val count: Int
        get(): Int {
            return indices.size
        }
}
