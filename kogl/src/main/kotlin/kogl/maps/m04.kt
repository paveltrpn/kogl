package kogl.maps

import algebra.*
import generator.*
import graph.*
import graphdsl.*
import map.*
import mesh.*
import render.*

// m04 - "Ferryman's Crossing".
// West of the world-origin, four brooding rocks stand chained in a line,
// each crowned with a bare platform. Three rope bridges sag between them,
// dipping toward a river that exists only in the imagination of the grid -
// while crystals spin above the planks, catching an orange lantern-light
// on one shore and a cold cyan glimmer on the other.
private fun ropeBridgePath(a: Vector3, b: Vector3, sag: Float, segments: Int = 24): ParametricPath {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val dz = b.z - a.z
    return ParametricPath({ t: Float ->
        val vertex = PathVertex()
        vertex.mPosition = Vector3(
            a.x + t * dx,
            a.y + t * dy - sag * 4.0f * t * (1.0f - t),
            a.z + t * dz
        )
        vertex.mTangent = Vector3(dx, dy - sag * 4.0f * (1.0f - 2.0f * t), dz).normalize()
        vertex.mNormal = Vector3(0.0f, 1.0f, 0.0f)
        vertex.mTexCoord = t
        vertex
    }, segments)
}

fun m04(): List<Locale> {
    val forwardLightProgram = Program().apply {
        source = ShaderSource("forwardLight")
        build()
        set("color" to Vector3(1.0f, 1.0f, 1.0f))
    }

    val rockMesh: Mesh = SeparatedArraysMesh(IcoSphereMesh(1.0f, 1).toObjMesh("rock"))
    val crystalMesh: Mesh = SeparatedArraysMesh(IcosahedronMesh(1.0f, 1).toObjMesh("crystal"))
    val platformMesh: Mesh = SeparatedArraysMesh(BoxMesh(Vector3(1.8f, 0.25f, 1.8f)).toObjMesh("platform"))
    val bridgeABMesh: Mesh = SeparatedArraysMesh(
        ExtrudeMesh(
            RectangleShape(Vector2(0.1f, 0.7f), IntPair(1, 1)),
            ropeBridgePath(Vector3(3.2f, 6.5f, -0.6f), Vector3(16.9f, 6.7f, -1.8f), 1.6f)
        ).toObjMesh("bridgeAB")
    )
    val bridgeBCMesh: Mesh = SeparatedArraysMesh(
        ExtrudeMesh(
            RectangleShape(Vector2(0.1f, 0.7f), IntPair(1, 1)),
            ropeBridgePath(Vector3(23.0f, 6.7f, -1.2f), Vector3(35.2f, 6.2f, 2.6f), 1.4f)
        ).toObjMesh("bridgeBC")
    )
    val bridgeCDMesh: Mesh = SeparatedArraysMesh(
        ExtrudeMesh(
            RectangleShape(Vector2(0.1f, 0.7f), IntPair(1, 1)),
            ropeBridgePath(Vector3(41.0f, 6.2f, 2.4f), Vector3(53.2f, 7.4f, -1.4f), 1.7f)
        ).toObjMesh("bridgeCD")
    )

    val l1 = buildLocale {
        origin = Vector3(-60.0f, 0.0f, 0.0f)
        attachState {
            stateGroup {
                program = forwardLightProgram
                attachTransform {
                    offset {
                        offset = Vector3(28.0f, 12.0f, 0.0f)
                        attachLight {
                            omniLight {
                                color = Vector3(1.0f, 0.7f, 0.4f)
                                intensity = 1.0f
                                radius = 45.0f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(-6.0f, 4.0f, 8.0f)
                        attachLight {
                            omniLight {
                                color = Vector3(0.4f, 0.8f, 1.0f)
                                intensity = 1.0f
                                radius = 25.0f
                            }
                        }
                    }
                }
                attachDrawable {
                    spinableDrawable {
                        mesh = rockMesh
                        color = Vector3(0.55f, 0.5f, 0.45f)
                        origin = Vector3(0.0f, 0.0f, 0.0f)
                        axis = Vector3(0.0f, 1.0f, 0.0f)
                        scale = Vector3(10.0f, 7.0f, 8.0f)
                        anglSpeed = 0.0f
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(20.0f, 1.0f, -2.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = rockMesh
                                color = Vector3(0.5f, 0.45f, 0.5f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(9.0f, 6.0f, 7.0f)
                                anglSpeed = 0.0f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(38.0f, 0.0f, 3.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = rockMesh
                                color = Vector3(0.52f, 0.5f, 0.46f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(8.0f, 6.5f, 6.0f)
                                anglSpeed = 0.0f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(56.0f, 1.0f, -2.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = rockMesh
                                color = Vector3(0.5f, 0.48f, 0.45f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(9.0f, 7.0f, 7.0f)
                                anglSpeed = 0.0f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(0.0f, 6.4f, 0.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = platformMesh
                                color = Vector3(0.45f, 0.38f, 0.3f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(1.0f, 1.0f, 1.0f)
                                anglSpeed = 0.0f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(20.0f, 6.6f, -2.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = platformMesh
                                color = Vector3(0.45f, 0.38f, 0.3f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(1.0f, 1.0f, 1.0f)
                                anglSpeed = 0.0f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(38.0f, 6.1f, 3.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = platformMesh
                                color = Vector3(0.45f, 0.38f, 0.3f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(1.0f, 1.0f, 1.0f)
                                anglSpeed = 0.0f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(56.0f, 7.3f, -2.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = platformMesh
                                color = Vector3(0.45f, 0.38f, 0.3f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(1.0f, 1.0f, 1.0f)
                                anglSpeed = 0.0f
                            }
                        }
                    }
                }
                attachDrawable {
                    spinableDrawable {
                        mesh = bridgeABMesh
                        color = Vector3(0.4f, 0.28f, 0.18f)
                        origin = Vector3(0.0f, 0.0f, 0.0f)
                        axis = Vector3(0.0f, 1.0f, 0.0f)
                        scale = Vector3(1.0f, 1.0f, 1.0f)
                        anglSpeed = 0.0f
                    }
                }
                attachDrawable {
                    spinableDrawable {
                        mesh = bridgeBCMesh
                        color = Vector3(0.4f, 0.28f, 0.18f)
                        origin = Vector3(0.0f, 0.0f, 0.0f)
                        axis = Vector3(0.0f, 1.0f, 0.0f)
                        scale = Vector3(1.0f, 1.0f, 1.0f)
                        anglSpeed = 0.0f
                    }
                }
                attachDrawable {
                    spinableDrawable {
                        mesh = bridgeCDMesh
                        color = Vector3(0.4f, 0.28f, 0.18f)
                        origin = Vector3(0.0f, 0.0f, 0.0f)
                        axis = Vector3(0.0f, 1.0f, 0.0f)
                        scale = Vector3(1.0f, 1.0f, 1.0f)
                        anglSpeed = 0.0f
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(0.0f, 7.6f, 0.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = crystalMesh
                                color = Vector3(0.4f, 0.9f, 1.0f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(0.8f, 0.8f, 0.8f)
                                anglSpeed = 0.5f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(38.0f, 7.3f, 3.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = crystalMesh
                                color = Vector3(1.0f, 0.5f, 0.9f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(0.8f, 0.8f, 0.8f)
                                anglSpeed = -0.4f
                            }
                        }
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = rockMesh
                        color = Vector3(0.5f, 0.48f, 0.45f)
                        origin = Vector3(0.0f, 2.0f, 12.0f)
                        axis = Vector3(0.0f, 1.0f, 0.2f)
                        scale = Vector3(0.8f, 0.6f, 0.7f)
                        anglSpeed = 0.2f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = rockMesh
                        color = Vector3(0.48f, 0.45f, 0.5f)
                        origin = Vector3(-10.0f, -1.0f, 0.0f)
                        axis = Vector3(0.2f, 1.0f, 0.0f)
                        scale = Vector3(0.6f, 0.5f, 0.6f)
                        anglSpeed = -0.3f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = rockMesh
                        color = Vector3(0.52f, 0.5f, 0.46f)
                        origin = Vector3(7.0f, 6.0f, -14.0f)
                        axis = Vector3(0.0f, 1.0f, -0.3f)
                        scale = Vector3(1.0f, 0.7f, 0.8f)
                        anglSpeed = 0.15f
                    }
                }
            }
        }
    }

    return listOf(l1)
}
