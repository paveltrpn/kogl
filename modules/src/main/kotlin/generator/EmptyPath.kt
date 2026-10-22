package generator

// ============================================================================
// ======================= EmptyPath ==========================================
// ============================================================================

class EmptyPath : GeneratorPath {

    override fun vertices(): Sequence<PathVertex> = emptySequence()

    override fun edges(): Sequence<Edge> = emptySequence()
}
