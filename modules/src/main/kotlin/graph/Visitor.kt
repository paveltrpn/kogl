package  graph

abstract class Visitor {
    open fun apply(node: Node): Unit {

    }

    open fun apply(node: Leaf): Unit {

    }

    open fun apply(node: Group): Unit {

    }

    open fun apply(node: StateGroup): Unit {

    }

    open fun apply(node: TransformGroup): Unit {

    }

    open fun apply(node: Drawable): Unit {

    }

    open fun apply(node: SwitchGroup): Unit {

    }

    open fun apply(node: View): Unit {

    }

    open fun apply(node: Light): Unit {

    }

    open fun apply(node: OmniLight): Unit {

    }

    open fun apply(node: SpotLight): Unit {

    }
}
