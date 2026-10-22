package generator

class MergeShape(
    vararg val mShapes: GeneratorShape
) : GeneratorShape {

    override fun vertices(): Sequence<ShapeVertex> = sequence {
        for (shape in mShapes) {
            for (vertex in shape.vertices()) {
                yield(vertex)
            }
        }
    }

    override fun edges(): Sequence<Edge> = sequence {
        var offset = 0
        for (shape in mShapes) {
            for (edge in shape.edges()) {
                yield(Edge(
                    edge.mVertices[0] + offset,
                    edge.mVertices[1] + offset
                ))
            }
            offset += shape.vertices().count()
        }
    }
}
