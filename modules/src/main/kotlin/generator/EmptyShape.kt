package generator

class EmptyShape : GeneratorShape {

    override fun vertices(): Sequence<ShapeVertex> = emptySequence()

    override fun edges(): Sequence<Edge> = emptySequence()
}
