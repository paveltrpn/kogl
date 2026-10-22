package kogl.maps

import algebra.*
import generator.*
import graph.*
import graphdsl.*
import map.*
import mesh.*
import render.*

// m04 - "Ferryman's Crossing".
// Four brooding rocks stand chained in a line at the world-origin, each
// crowned with a thick deck of planks sunk into the stone, and a fifth
// juts aside to make
// the chain a T. Rope bridges sag between them all, dipping toward a
// river that exists only in the imagination of the grid. Above the four
// decks of the line hover crowned structures, each with a light of its
// own hue held a step outside the structure: a violet (3,4) torus knot,
// a copper teapot, a sea-green dodecahedron, and an amber spherical
// cone - while crystals spin above the planks, a swarm of ten bright
// icosahedrons wanders the sky, and a (2,3) torus knot turns on the
// spur, lit by a magenta heart.
private fun _ropeBridgePath(a: Vector3, b: Vector3, sag: Float, segments: Int = 24): ParametricPath {
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
    val platformMesh: Mesh = SeparatedArraysMesh(BoxMesh(Vector3(3.6f, 0.7f, 3.6f)).toObjMesh("platform"))
    val bridgeABMesh: Mesh = SeparatedArraysMesh(
        ExtrudeMesh(
            RectangleShape(Vector2(0.1f, 0.7f), IntPair(1, 1)),
            _ropeBridgePath(Vector3(3.2f, 6.5f, -0.6f), Vector3(16.9f, 6.7f, -1.8f), 1.6f)
        ).toObjMesh("bridgeAB")
    )
    val bridgeBCMesh: Mesh = SeparatedArraysMesh(
        ExtrudeMesh(
            RectangleShape(Vector2(0.1f, 0.7f), IntPair(1, 1)),
            _ropeBridgePath(Vector3(23.0f, 6.7f, -1.2f), Vector3(35.2f, 6.2f, 2.6f), 1.4f)
        ).toObjMesh("bridgeBC")
    )
    val bridgeCDMesh: Mesh = SeparatedArraysMesh(
        ExtrudeMesh(
            RectangleShape(Vector2(0.1f, 0.7f), IntPair(1, 1)),
            _ropeBridgePath(Vector3(41.0f, 6.2f, 2.4f), Vector3(53.2f, 7.4f, -1.4f), 1.7f)
        ).toObjMesh("bridgeCD")
    )
    val bridgeBEMesh: Mesh = SeparatedArraysMesh(
        ExtrudeMesh(
            RectangleShape(Vector2(0.1f, 0.7f), IntPair(1, 1)),
            _ropeBridgePath(Vector3(20.0f, 6.7f, 0.6f), Vector3(20.0f, 6.7f, 11.4f), 1.4f)
        ).toObjMesh("bridgeBE")
    )
    val knotTubeMesh: Mesh = SeparatedArraysMesh(TorusKnotMesh(2, 3, 16, 96).toObjMesh("knotTube"))
    val knotAMesh: Mesh = SeparatedArraysMesh(TorusKnotMesh(3, 4, 12, 96).toObjMesh("knotA"))
    val teapotMesh: Mesh = SeparatedArraysMesh(TeapotMesh(10).toObjMesh("teapot"))
    val dodecaMesh: Mesh = SeparatedArraysMesh(DodecahedronMesh(1.0f, 2, 2).toObjMesh("dodeca"))
    val spherConeMesh: Mesh = SeparatedArraysMesh(SphericalConeMesh(1.0f, 1.0f, 24, 8, 4).toObjMesh("spherCone"))

    val l1 = buildLocale {
        origin = Vector3(0.0f, 0.0f, 0.0f)
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
                        offset = Vector3(20.0f, 1.0f, 14.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = rockMesh
                                color = Vector3(0.5f, 0.46f, 0.52f)
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
                        offset = Vector3(0.0f, 6.0f, 0.0f)
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
                        offset = Vector3(20.0f, 6.2f, -2.0f)
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
                        offset = Vector3(38.0f, 5.7f, 3.0f)
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
                        offset = Vector3(56.0f, 6.9f, -2.0f)
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
                        offset = Vector3(20.0f, 6.2f, 14.0f)
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
                attachDrawable {
                    spinableDrawable {
                        mesh = bridgeBEMesh
                        color = Vector3(0.4f, 0.28f, 0.18f)
                        origin = Vector3(0.0f, 0.0f, 0.0f)
                        axis = Vector3(0.0f, 1.0f, 0.0f)
                        scale = Vector3(1.0f, 1.0f, 1.0f)
                        anglSpeed = 0.0f
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(20.0f, 10.0f, 14.0f)
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
                        attachLight {
                            omniLight {
                                color = Vector3(1.0f, 0.6f, 0.9f)
                                intensity = 1.0f
                                radius = 20.0f
                            }
                        }
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
                attachTransform {
                    offset {
                        offset = Vector3(0.0f, 8.2f, 0.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = knotAMesh
                                color = Vector3(0.6f, 0.35f, 1.0f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(1.2f, 1.2f, 1.2f)
                                anglSpeed = 0.35f
                            }
                        }
                        attachTransform {
                            offset {
                                offset = Vector3(2.2f, 1.0f, 1.5f)
                                attachLight {
                                    omniLight {
                                        color = Vector3(0.8f, 0.5f, 1.0f)
                                        intensity = 1.0f
                                        radius = 14.0f
                                    }
                                }
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(20.0f, 8.4f, -2.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = teapotMesh
                                color = Vector3(0.85f, 0.5f, 0.3f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(0.7f, 0.7f, 0.7f)
                                anglSpeed = -0.25f
                            }
                        }
                        attachTransform {
                            offset {
                                offset = Vector3(1.8f, 0.8f, -1.2f)
                                attachLight {
                                    omniLight {
                                        color = Vector3(1.0f, 0.7f, 0.4f)
                                        intensity = 1.0f
                                        radius = 14.0f
                                    }
                                }
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(38.0f, 7.9f, 3.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = dodecaMesh
                                color = Vector3(0.3f, 0.85f, 0.6f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(1.3f, 1.3f, 1.3f)
                                anglSpeed = 0.3f
                            }
                        }
                        attachTransform {
                            offset {
                                offset = Vector3(-1.8f, 1.0f, 1.4f)
                                attachLight {
                                    omniLight {
                                        color = Vector3(0.5f, 1.0f, 0.7f)
                                        intensity = 1.0f
                                        radius = 14.0f
                                    }
                                }
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(56.0f, 9.1f, -2.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = spherConeMesh
                                color = Vector3(0.95f, 0.75f, 0.3f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(1.4f, 1.4f, 1.4f)
                                anglSpeed = -0.35f
                            }
                        }
                        attachTransform {
                            offset {
                                offset = Vector3(2.0f, 0.6f, -1.6f)
                                attachLight {
                                    omniLight {
                                        color = Vector3(1.0f, 0.85f, 0.5f)
                                        intensity = 1.0f
                                        radius = 14.0f
                                    }
                                }
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
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(0.9f, 0.4f, 0.6f)
                        origin = Vector3(12.0f, 9.0f, 6.0f)
                        axis = Vector3(0.2f, 1.0f, 0.1f)
                        scale = Vector3(0.5f, 0.5f, 0.5f)
                        anglSpeed = 0.45f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(0.4f, 0.7f, 1.0f)
                        origin = Vector3(28.0f, 10.0f, -8.0f)
                        axis = Vector3(-0.3f, 1.0f, 0.2f)
                        scale = Vector3(0.7f, 0.5f, 0.6f)
                        anglSpeed = -0.35f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(1.0f, 0.8f, 0.3f)
                        origin = Vector3(44.0f, 8.0f, 10.0f)
                        axis = Vector3(0.1f, 1.0f, -0.4f)
                        scale = Vector3(0.4f, 0.6f, 0.4f)
                        anglSpeed = 0.55f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(0.5f, 1.0f, 0.6f)
                        origin = Vector3(8.0f, 12.0f, -10.0f)
                        axis = Vector3(0.4f, 1.0f, 0.0f)
                        scale = Vector3(0.6f, 0.6f, 0.6f)
                        anglSpeed = -0.5f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(0.8f, 0.5f, 1.0f)
                        origin = Vector3(52.0f, 11.0f, 6.0f)
                        axis = Vector3(-0.2f, 1.0f, 0.3f)
                        scale = Vector3(0.5f, 0.4f, 0.5f)
                        anglSpeed = 0.4f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(1.0f, 0.6f, 0.4f)
                        origin = Vector3(16.0f, 7.0f, 18.0f)
                        axis = Vector3(0.0f, 1.0f, 0.5f)
                        scale = Vector3(0.45f, 0.45f, 0.45f)
                        anglSpeed = -0.6f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(0.4f, 0.9f, 0.9f)
                        origin = Vector3(36.0f, 13.0f, -6.0f)
                        axis = Vector3(0.3f, 1.0f, -0.2f)
                        scale = Vector3(0.55f, 0.55f, 0.55f)
                        anglSpeed = 0.3f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(1.0f, 0.4f, 0.4f)
                        origin = Vector3(-8.0f, 8.0f, 4.0f)
                        axis = Vector3(-0.1f, 1.0f, 0.4f)
                        scale = Vector3(0.5f, 0.7f, 0.5f)
                        anglSpeed = -0.45f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(0.7f, 0.7f, 1.0f)
                        origin = Vector3(48.0f, 9.0f, -12.0f)
                        axis = Vector3(0.2f, 1.0f, 0.3f)
                        scale = Vector3(0.6f, 0.5f, 0.6f)
                        anglSpeed = 0.5f
                    }
                }
                attachDrawable {
                    flyaroundDrawable {
                        mesh = crystalMesh
                        color = Vector3(0.9f, 1.0f, 0.5f)
                        origin = Vector3(24.0f, 14.0f, 2.0f)
                        axis = Vector3(-0.4f, 1.0f, -0.1f)
                        scale = Vector3(0.4f, 0.4f, 0.4f)
                        anglSpeed = -0.35f
                    }
                }
            }
        }
    }

    return listOf(l1)
}
