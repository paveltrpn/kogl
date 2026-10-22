package generator

// ============================================================================
// ======================= TransformPath ======================================
// ============================================================================
open class TransformPath(
    private val _path: GeneratorPath,
    private val _mutate: (PathVertex) -> Unit
) : GeneratorPath {

    override fun vertices(): Sequence<PathVertex> = _path.vertices().map { vertex: PathVertex ->
        _mutate(vertex)
        vertex
    }

    override fun edges(): Sequence<Edge> = _path.edges()
}
