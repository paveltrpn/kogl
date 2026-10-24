package graph

// ============================================================================
// ======================= AnimationVisitor ===================================
// ============================================================================

class AnimationVisitor(delta: Float) : TransformVisitor() {
    private val _delta = delta

    override fun apply(node: Group): Unit {
        node.traverse(this)
    }

    override fun apply(node: Switch): Unit {
        node.traverse(this)
    }

    override fun apply(node: StateGroup): Unit {
        node.traverse(this)
    }

    override fun apply(node: Leaf): Unit {
        when (node) {
            is SpinableDrawable -> {
                val local = node.updateLocal(_delta)

                val top = mModelMatrixStack.top.copy()
                top.transpose()

                node.applyTransform(local.multiply(top))
            }

            is FlyaroundDrawable -> {
                val local = node.updateLocal(_delta)

                val top = mModelMatrixStack.top.copy()
                top.transpose()

                node.applyTransform(local.multiply(top))
            }
        }
    }
}
