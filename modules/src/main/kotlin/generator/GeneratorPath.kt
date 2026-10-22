package generator

// ============================================================================
// ======================= GeneratorPath ======================================
// ============================================================================

interface GeneratorPath {
    fun vertices(): Sequence<PathVertex>
    fun edges(): Sequence<Edge>
}
