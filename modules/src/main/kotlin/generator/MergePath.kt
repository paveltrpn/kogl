package generator

class MergePath(
    vararg val mPaths: GeneratorPath
) : GeneratorPath {

    override fun vertices(): Sequence<PathVertex> = sequence {
        for (path in mPaths) {
            for (vertex in path.vertices()) {
                yield(vertex)
            }
        }
    }

    override fun edges(): Sequence<Edge> = sequence {
        var offset = 0
        for (path in mPaths) {
            for (edge in path.edges()) {
                yield(Edge(
                    edge.mVertices[0] + offset,
                    edge.mVertices[1] + offset
                ))
            }
            offset += path.vertices().count()
        }
    }
}
