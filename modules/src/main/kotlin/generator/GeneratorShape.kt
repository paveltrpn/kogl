package generator

interface GeneratorShape {
    fun vertices(): Sequence<ShapeVertex>
    fun edges(): Sequence<Edge>
}
