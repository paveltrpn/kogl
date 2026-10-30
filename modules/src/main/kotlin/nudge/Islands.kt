package nudge

internal class UnionFind(
    size: Int
) {
    private val _parent: IntArray = IntArray(size) { index: Int -> index }
    private val _height: IntArray = IntArray(size)

    fun find(value: Int): Int {
        var root: Int = value
        while (_parent[root] != root) {
            root = _parent[root]
        }
        var current: Int = value
        while (_parent[current] != current) {
            val next: Int = _parent[current]
            _parent[current] = root
            current = next
        }
        return root
    }

    fun union(a: Int, b: Int): Unit {
        var rootA: Int = find(a)
        var rootB: Int = find(b)
        if (rootA == rootB) {
            return
        }
        if (_height[rootA] < _height[rootB]) {
            val temporary: Int = rootA
            rootA = rootB
            rootB = temporary
        }
        _parent[rootB] = rootA
        if (_height[rootA] == _height[rootB]) {
            _height[rootA]++
        }
    }
}

internal class IslandData(
    val sets: IntArray,
    val activeSets: BooleanArray
) {
    fun isActive(bodyA: Int, bodyB: Int): Boolean {
        if (bodyA != 0 && activeSets[sets[bodyA]]) {
            return true
        }
        if (bodyB != 0 && activeSets[sets[bodyB]]) {
            return true
        }
        return false
    }
}

internal fun buildIslands(
    bodies: BodyData,
    connections: BodyConnections,
    pairs: List<BodyPair>
): IslandData {
    val unionFind = UnionFind(bodies.count)
    for (connection: BodyPair in connections.data) {
        require(connection.a < bodies.count && connection.b < bodies.count)
        if (connection.a != 0 && connection.b != 0) {
            unionFind.union(connection.a, connection.b)
        }
    }
    for (pair: BodyPair in pairs) {
        if (pair.a != 0 && pair.b != 0) {
            unionFind.union(pair.a, pair.b)
        }
    }

    val sets: IntArray = IntArray(bodies.count)
    val rootSets: MutableMap<Int, Int> = mutableMapOf()
    var setCount: Int = 0
    for (body: Int in 1 until bodies.count) {
        val root: Int = unionFind.find(body)
        val set: Int = rootSets.getOrPut(root) {
            val newSet: Int = setCount
            setCount++
            newSet
        }
        sets[body] = set
    }

    val activeSets: BooleanArray = BooleanArray(setCount)
    for (body: Int in 1 until bodies.count) {
        if (bodies.idleCounters[body] != 255) {
            activeSets[sets[body]] = true
        }
    }
    return IslandData(sets, activeSets)
}

internal fun sleepingPairKey(tagA: Int, tagB: Int): Int {
    val low: Int = minOf(tagA, tagB)
    val high: Int = maxOf(tagA, tagB)
    return high or (low shl 16)
}
