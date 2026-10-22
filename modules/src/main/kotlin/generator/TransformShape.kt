package generator

// ============================================================================
// ======================= TransformShape =====================================
// ============================================================================
open class TransformShape(
    private val _shape: GeneratorShape,
    private val _mutate: (ShapeVertex) -> Unit
) : GeneratorShape {

    override fun vertices(): Sequence<ShapeVertex> = _shape.vertices().map { vertex: ShapeVertex ->
        _mutate(vertex)
        vertex
    }

    override fun edges(): Sequence<Edge> = _shape.edges()
}
