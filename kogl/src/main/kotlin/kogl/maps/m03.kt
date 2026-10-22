package kogl.maps

import algebra.*
import generator.*
import graph.*
import graphdsl.*
import map.*
import mesh.*
import render.*

// m03 - "The Clockwork Menagerie".
// East of the world-origin, a carnival of primitives performs under carnival
// lights: a sphere crowned at center stage, boxes waltzing around it, a torus
// stood on end like a hoop for a tiger that never jumps, a capsule and a
// torus knot tumbling in the rigging. Magenta and blue stage lamps throw
// long shadows across the sawdust floor of the grid.
fun m03(): List<Locale> {
    val forwardLightProgram = Program().apply {
        source = ShaderSource("forwardLight")
        build()
        set("color" to Vector3(1.0f, 0.0f, 0.0f))
    }

    val sphereMesh: Mesh = SeparatedArraysMesh(IcoSphereMesh(1.0f, 3).toObjMesh("sphere"))
    val torusMesh: Mesh = SeparatedArraysMesh(TorusMesh(0.5f, 1.5f, 24, 48).toObjMesh("torus"))
    val capsuleMesh: Mesh = SeparatedArraysMesh(CapsuleMesh(0.8f, 1.5f).toObjMesh("capsule"))
    val knotTubeMesh: Mesh = SeparatedArraysMesh(TorusKnotMesh(2, 3, 16, 96).toObjMesh("knotTube"))
    val boxMesh: Mesh = SeparatedArraysMesh(BoxMesh(Vector3(1.5f, 1.5f, 1.5f)).toObjMesh("box"))

    val l1 = buildLocale {
        origin = Vector3(60.0f, 0.0f, 0.0f)
        attachState {
            stateGroup {
                program = forwardLightProgram
                attachTransform {
                    offset {
                        offset = Vector3(0.0f, 10.0f, 0.0f)
                        attachLight {
                            omniLight {
                                color = Vector3(1.0f, 0.6f, 0.9f)
                                intensity = 1.0f
                                radius = 40.0f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(12.0f, 4.0f, -6.0f)
                        attachLight {
                            omniLight {
                                color = Vector3(0.5f, 0.8f, 1.0f)
                                intensity = 1.0f
                                radius = 25.0f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(0.0f, 4.0f, 0.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = sphereMesh
                                color = Vector3(1.0f, 0.6f, 0.2f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(2.0f, 2.0f, 2.0f)
                                anglSpeed = 0.4f
                            }
                        }
                        attachDrawable {
                            flyaroundDrawable {
                                mesh = boxMesh
                                color = Vector3(0.2f, 0.8f, 1.0f)
                                origin = Vector3(0.0f, 0.0f, 6.0f)
                                scale = Vector3(1.0f, 1.0f, 1.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                anglSpeed = 0.5f
                            }
                        }
                        attachDrawable {
                            flyaroundDrawable {
                                mesh = boxMesh
                                color = Vector3(0.2f, 1.0f, 0.4f)
                                origin = Vector3(0.0f, 0.0f, -6.0f)
                                scale = Vector3(1.0f, 1.0f, 1.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                anglSpeed = -0.5f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(12.0f, 2.0f, 0.0f)
                        attachTransform {
                            rotate {
                                axis = Vector3(1.0f, 0.0f, 0.0f)
                                angl = 90.0f
                                attachDrawable {
                                    spinableDrawable {
                                        mesh = torusMesh
                                        color = Vector3(1.0f, 0.3f, 0.7f)
                                        origin = Vector3(0.0f, 0.0f, 0.0f)
                                        axis = Vector3(0.0f, 0.0f, 1.0f)
                                        scale = Vector3(2.0f, 2.0f, 2.0f)
                                        anglSpeed = 0.6f
                                    }
                                }
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(-12.0f, 2.0f, 0.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = capsuleMesh
                                color = Vector3(0.9f, 0.9f, 0.2f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(1.5f, 1.5f, 1.5f)
                                anglSpeed = 0.5f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(0.0f, 8.0f, 0.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = knotTubeMesh
                                color = Vector3(0.6f, 0.4f, 1.0f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(2.0f, 2.0f, 2.0f)
                                anglSpeed = 0.3f
                            }
                        }
                    }
                }
            }
        }
    }

    return listOf(l1)
}
