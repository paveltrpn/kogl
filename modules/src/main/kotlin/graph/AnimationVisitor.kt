package graph

import render.*

// ============================================================================
// ======================= AnimationVisitor ===================================
// ============================================================================

class AnimationVisitor(frameTime: FrameTimeData) : TransformVisitor() {
    private val _frameTime = frameTime

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
                val local = node.updateLocal(_frameTime.deltaSeconds)

                val top = mModelMatrixStack.top.copy()
                top.transpose()

                node.applyTransform(local.multiply(top))
            }

            is FlyaroundDrawable -> {
                val local = node.updateLocal(_frameTime.deltaSeconds)

                val top = mModelMatrixStack.top.copy()
                top.transpose()

                node.applyTransform(local.multiply(top))
            }
        }
    }
}
