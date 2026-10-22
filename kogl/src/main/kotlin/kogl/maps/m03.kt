package kogl.maps

import algebra.*
import generator.*
import graph.*
import graphdsl.*
import map.*
import mesh.*
import render.*

fun m03(): List<Locale> {
    val flatshadeProgram = Program().apply {
        source = ShaderSource("flatshade")
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
                program = flatshadeProgram
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
