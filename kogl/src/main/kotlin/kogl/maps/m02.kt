package maps

import algebra.*
import graph.*
import graphdsl.*
import map.*
import render.*

fun m02(): List<Locale> {
    val flatshadeProgram = Program().apply {
        source = ShaderSource("flatshade")
        build()
        set("color" to Vector3(1.0f, 0.0f, 0.0f))
    }

    val storage = Storage.instance()

    val l1 = buildLocale {
        origin = Vector3(0.0f, 0.0f, 0.0f)

        attachState {
            stateGroup {
                program = flatshadeProgram
                attachTransform {
                    offset {
                        offset = Vector3(0.0f, 0.0f, 10.0f)
                        attachDrawable {
                            flyaroundDrawable {
                                mesh = storage["diamond"]
                                color = Vector3(1.0f, 1.0f, 1.0f)
                                origin = Vector3(0.0f, 0.0f, 2.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                anglSpeed = 0.5f
                            }
                        }
                        attachDrawable {
                            flyaroundDrawable {
                                mesh = storage["box"]
                                color = Vector3(1.0f, 1.0f, 1.0f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                // axis = Vector3(0.0f, 1.0f, 0.0f)
                                // anglSpeed = 0.5f
                            }
                        }
                    }
                }
            }
        }
    }

    return listOf(l1)
}
