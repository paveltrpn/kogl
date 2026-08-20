package testgraph

import kotlin.random.Random

import org.lwjgl.opengl.GL46.*

import algebra.*
import config.*
import graph.*
import mesh.*
import render.*

fun sparseObjectsGraph(): Triple<StateGroup, StateGroup, StateGroup> {
    val pathPrefix = Config.instance().basePath

    val frameObj = readWavefrontObjFile("${pathPrefix}/assets/bodies/frame.obj")
    val frameMesh = InterleavedMesh(frameObj)

    val diamondObj = readWavefrontObjFile("${pathPrefix}/assets/bodies/diamond.obj")
    val diamondMesh = SeparatedArraysMesh(diamondObj)

    val arch01dObj = readWavefrontObjFile("${pathPrefix}/assets/bodies/arch01.obj")
    val arch01dMesh = SeparatedArraysMesh(arch01dObj)

    // =================================================

    val flatshadeSource = ShaderSource("flatshade")
    val flatshadeProgram = Program().apply {
        source(flatshadeSource)
        define(GL_VERTEX_SHADER, "DUMMY_ONE 1")
        define(GL_VERTEX_SHADER, "DUMMY_TWO 2")
        build()
    }

    flatshadeProgram.setVectorUniform("color", Vector3(1.0f, 0.0f, 0.0f))

    val diamondStateGroup = StateGroup(flatshadeProgram)
    val frameStateGroup = StateGroup(flatshadeProgram)
    val arch01StateGroup = StateGroup(flatshadeProgram)

    val sum: (Int, Int) -> Int = { a: Int, b: Int ->
        val result = a + b
        result // The last expression acts as the return value
    }

    val randomFloat: (Float, Float) -> Float = { from: Float, to: Float ->
        from + Random.nextFloat() * (to - from)
    }

    val randomVector3: (Float, Float) -> Vector3 = { from: Float, to: Float ->
        val list = List(3) { from + Random.nextFloat() * (to - from) }
        Vector3(list[0], list[1], list[2])
    }

    for (i in 0..32) {
        val item = FlyaroundDrawable(diamondMesh).apply {
            color = randomVector3(0.1f, 0.9f)
            origin = Vector3(0.0f, 0.0f, 0.0f)
            axis = randomVector3(-0.6f, 0.6f).normalize()
            anglSpeed = randomFloat(-1.0f, 1.0f)
        }

        val scale = TransformGroup()
        val sf = randomFloat(0.2f, 2.0f)
        scale.matrix = algebra.scale(sf, sf, sf)

        val offset = TransformGroup()
        val rz = randomFloat(-4.0f, -12.0f)
        val rtv = randomVector3(-4.0f, 4.0f)
        offset.matrix = algebra.offset(rtv.x, rtv.y, rz)

        scale.addChild(item)
        offset.addChild(scale)
        diamondStateGroup.addChild(offset)
    }

    for (i in 0..16) {
        val item = FlyaroundDrawable(frameMesh).apply {
            color = randomVector3(0.1f, 0.9f)
            origin = Vector3(0.0f, 0.0f, 0.0f)
            axis = randomVector3(-0.6f, 0.6f).normalize()
            anglSpeed = randomFloat(0.4f, 1.2f)
        }

        val scale = TransformGroup()
        val sf = randomFloat(0.4f, 2.0f)
        scale.matrix = algebra.scale(sf, sf, sf)

        val offset = TransformGroup()
        val rz = randomFloat(-4.0f, -12.0f)
        val rtv = randomVector3(-4.0f, 4.0f)
        offset.matrix = algebra.offset(rtv.x, rtv.y, rz)

        scale.addChild(item)
        offset.addChild(scale)
        frameStateGroup.addChild(offset)
    }

    for (i in 0..16) {
        val item = FlyaroundDrawable(arch01dMesh).apply {
            color = randomVector3(0.1f, 0.9f)
            origin = Vector3(0.0f, 0.0f, 0.0f)
            axis = randomVector3(-0.6f, 0.6f).normalize()
            anglSpeed = randomFloat(0.4f, 1.2f)
        }

        val scale = TransformGroup()
        val sf = randomFloat(0.4f, 2.0f)
        scale.matrix = algebra.scale(sf, sf, sf)

        val offset = TransformGroup()
        val rz = randomFloat(-4.0f, -12.0f)
        val rtv = randomVector3(-4.0f, 4.0f)
        offset.matrix = algebra.offset(rtv.x, rtv.y, rz)

        scale.addChild(item)
        offset.addChild(scale)
        arch01StateGroup.addChild(offset)
    }

    return Triple(diamondStateGroup, frameStateGroup, arch01StateGroup)
}

fun testFlyaroundsGraph(): StateGroup {
    val pathPrefix = Config.instance().basePath

    val diamondObj = readWavefrontObjFile("${pathPrefix}/assets/bodies/diamond.obj")
    val diamondMesh = SeparatedArraysMesh(diamondObj)

    // =================================================

    val flatshadeSource = ShaderSource("flatshade")
    val flatshadeProgram = Program().apply {
        source(flatshadeSource)
        build()
    }

    flatshadeProgram.setVectorUniform("color", Vector3(1.0f, 0.0f, 0.0f))

    val rootStateGroup = StateGroup(flatshadeProgram)

    // =================================================

    val item = FlyaroundDrawable(diamondMesh)
    item.apply {
        color = Vector3(0.0f, 0.0f, 1.0f)
        axis = Vector3(0.0f, 0.0f, 1.0f).normalize()
        anglSpeed = 0.7f
        origin = Vector3(1.5f, 0.0f, 0.0f)
    }

    val scale = TransformGroup()
    scale.matrix = algebra.scale(0.5f, 0.5f, 0.5f)

    val offset = TransformGroup()
    offset.matrix = algebra.offset(0.0f, 0.0f, 0.0f)

    scale.addChild(item)
    offset.addChild(scale)
    rootStateGroup.addChild(offset)

    return rootStateGroup
}

fun testCubesGraph(): StateGroup {
    val boxMesh = boxFactory()
    boxMesh.transform(algebra.rotation(0.0f, 45.0f, 45.0f))

    // =================================================

    val flatshadeSource = ShaderSource("flatshade")
    val flatshadeProgram = Program().apply {
        source(flatshadeSource)
        build()
    }

    flatshadeProgram.setVectorUniform("color", Vector3(0.0f, 1.0f, 0.0f))

    val rootStateGroup = StateGroup(flatshadeProgram)

    // =================================================

    val item = Drawable(boxMesh)
    item.color = Vector3(0.0f, 0.0f, 1.0f)

    val scale = TransformGroup()
    scale.matrix = algebra.scale(1.0f, 1.0f, 1.0f)

    val offset = TransformGroup()
    offset.matrix = algebra.offset(0.0f, 0.0f, 0.0f)

    scale.addChild(item)
    offset.addChild(scale)
    rootStateGroup.addChild(offset)

    return rootStateGroup
}