package kogl.maps

import algebra.*
import generator.*
import graph.*
import graphdsl.*
import map.*
import mesh.*
import render.*

fun m04(): List<Locale> {
    val forwardLightProgram = Program().apply {
        source = ShaderSource("forwardLight")
        build()
        set("color" to Vector3(1.0f, 1.0f, 1.0f))
    }

    val rockMesh: Mesh = SeparatedArraysMesh(IcoSphereMesh(1.0f, 1).toObjMesh("rock"))
    val bridgeDeckMesh: Mesh = SeparatedArraysMesh(BoxMesh(Vector3(14.0f, 0.4f, 2.0f)).toObjMesh("bridgeDeck"))
    val bridgeRailMesh: Mesh = SeparatedArraysMesh(BoxMesh(Vector3(14.0f, 0.6f, 0.2f)).toObjMesh("bridgeRail"))
    val crystalMesh: Mesh = SeparatedArraysMesh(IcosahedronMesh(1.0f, 1).toObjMesh("crystal"))

    val l1 = buildLocale {
        origin = Vector3(-60.0f, 0.0f, 0.0f)
        attachState {
            stateGroup {
                program = forwardLightProgram
                attachTransform {
                    offset {
                        offset = Vector3(7.0f, 8.0f, 0.0f)
                        attachLight {
                            omniLight {
                                color = Vector3(1.0f, 0.7f, 0.4f)
                                intensity = 1.0f
                                radius = 35.0f
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
                        scale = Vector3(6.0f, 4.0f, 5.0f)
                        anglSpeed = 0.1f
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(14.0f, 1.0f, 0.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = rockMesh
                                color = Vector3(0.5f, 0.45f, 0.5f)
                                origin = Vector3(0.0f, 0.0f, 0.0f)
                                axis = Vector3(0.0f, 1.0f, 0.0f)
                                scale = Vector3(5.0f, 3.5f, 4.5f)
                                anglSpeed = -0.08f
                            }
                        }
                    }
                }
                attachTransform {
                    offset {
                        offset = Vector3(7.0f, 1.5f, 0.0f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = bridgeDeckMesh
                                color = Vector3(0.45f, 0.3f, 0.2f)
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
                        offset = Vector3(7.0f, 2.2f, 1.1f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = bridgeRailMesh
                                color = Vector3(0.35f, 0.22f, 0.15f)
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
                        offset = Vector3(7.0f, 2.2f, -1.1f)
                        attachDrawable {
                            spinableDrawable {
                                mesh = bridgeRailMesh
                                color = Vector3(0.35f, 0.22f, 0.15f)
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
                        offset = Vector3(2.0f, 4.0f, 1.0f)
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
                        offset = Vector3(12.0f, 4.5f, -1.0f)
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
