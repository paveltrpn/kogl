package graphdsl

import algebra.*
import graph.*

class LocaleBuilder : NodeBuilder() {

}

fun buildLocale(builder: LocaleBuilder.() -> Unit): Locale {
    return Locale().apply { origin = Vector3() }
}
