package generator

// ============================================================================
// ======================= GeneratorShape =====================================
// ============================================================================

interface GeneratorShape {
    fun vertices(): Sequence<ShapeVertex>
    fun edges(): Sequence<Edge>
}
