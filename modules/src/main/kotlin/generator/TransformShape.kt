package generator

open class TransformShape(
    private val _shape: GeneratorShape,
    private val _mutate: (ShapeVertex) -> Unit
) : GeneratorShape {

    override fun vertices(): Sequence<ShapeVertex> = _shape.vertices().map { vertex ->
        _mutate(vertex)
        vertex
    }

    override fun edges(): Sequence<Edge> = _shape.edges()
}
