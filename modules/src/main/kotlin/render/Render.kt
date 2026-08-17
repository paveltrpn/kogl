package render

import kotlin.random.Random

import org.lwjgl.opengl.GL46.*

import algebra.*
import config.*
import event.*
import graph.*
import scene.*
import mesh.*
import ui.*
import image.*
import map.*
import java.io.File

class PrintTypeVisitor : Visitor {
    override fun apply(node: StateGroup): Unit {
        println("my type is ${node::class}")
        node.traverse(this)
    }

    override fun apply(node: Transform): Unit {
        println("my type is ${node::class}")
        node.traverse(this)
    }

    override fun apply(node: Drawable): Unit {
        println("my type is ${node::class}")
        node.traverse(this)
    }
}

class Render : EventObserver {
    private var _run: Boolean
    private val _scene: Scene
    private val _ui: UiGL

    init {
        _run = true

        val sparseObjectsGraph = sparseObjectsGraph()
        //val testBoxGraph = testCubesGraph()
        val flyAroundGraph = testFlyaroundsGraph()

        _scene = Scene()

        _scene.addStateGroup(sparseObjectsGraph)
        //_scene.addStateGroup(testBoxGraph)
        // _scene.addStateGroup(flyAroundGraph)

        val file = File("/mnt/main/code/kogl/kogl/assets/m01.json")
        val jsonString = file.readText()
        val mapData = parseMapJson(jsonString)

        val sg = buildStateGroups(mapData, Storage.instance().bodyStorage)
        _scene.addStateGroup(sg[0])
        
//        val printtype = PrintTypeVisitor()
//        sparseObjectsGraph.accept(printtype)


        // TEST
//        val file = File("/mnt/main/code/kogl/kogl/assets/m01.json")
//        val jsonString = file.readText()
//        val mapData = parseMapJson(jsonString)
//
//        fun printNode(node: NodeData, indent: String = "") {
//            println("${indent}type=${node.type}")
//            when (node) {
//                is StateGroupData -> {
//                    println("${indent}  program: ${node.payload.program}")
//                    for (child in node.payload.children) {
//                        printNode(child, indent + "  ")
//                    }
//                }
//
//                is GroupData -> {
//                    for (child in node.payload.children) {
//                        printNode(child, indent + "  ")
//                    }
//                }
//
//                is TransformData -> {
//                    println("${indent}  transform_type: ${node.payload.trnasform_type}")
//                    println("${indent}  matrix: ${node.payload.matrix.contentToString()}")
//                    println("${indent}  data: ${node.payload.data.contentToString()}")
//                    printNode(node.payload.child, indent + "  ")
//                }
//
//                is DrawableData -> {
//                    println("${indent}  mesh: ${node.payload.mesh}")
//                    println("${indent}  material: ${node.payload.material}")
//                }
//
//                is SpinableDrawableData -> {
//                    println("${indent}  mesh: ${node.payload.mesh}")
//                    println("${indent}  material: ${node.payload.material}")
//                    println("${indent}  axis: ${node.payload.axis.contentToString()}")
//                    println("${indent}  anglSpeed: ${node.payload.anglSpeed}")
//                }
//
//                is FlyaroundDrawableData -> {
//                    println("${indent}  mesh: ${node.payload.mesh}")
//                    println("${indent}  material: ${node.payload.material}")
//                    println("${indent}  origin: ${node.payload.origin.contentToString()}")
//                    println("${indent}  axis: ${node.payload.axis.contentToString()}")
//                    println("${indent}  anglSpeed: ${node.payload.anglSpeed}")
//                }
//
//                is GenericNodeData -> {
//                    // Generic node with no payload
//                }
//            }
//        }
//
//        println("Map name: ${mapData.name}")
//        println("Graph entries: ${mapData.graph.size}")
//        for ((index, node) in mapData.graph.withIndex()) {
//            println("Graph[$index]:")
//            printNode(node, "  ")
//        }

        //

        _ui = UiGL()
    }

    var run: Boolean
        get(): Boolean {
            return _run
        }
        set(value) {
            _run = value
        }

    fun preLoop(): Unit {

    }

    fun frame(): Unit {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable(GL_DEPTH_TEST)
        glDepthFunc(GL_LESS)

        _scene.walk()

        glDisable(GL_DEPTH_TEST)

        val TEXT_START_POS_X = -30.0f
        val TEXT_START_POS_Y = 26.0f
        val TEXT_PLANE_Z = 0.4f

        val l1 = Label().apply {
            text = "test string"
            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y)
            z = TEXT_PLANE_Z
            letterSpace = 0.0f
            color = Color(255, 255, 255, 255)
        }

        val l2 = Label().apply {
            text = "test string"
            position = Vector2(TEXT_START_POS_X + 0.0f, TEXT_START_POS_Y + 3.5f)
            z = TEXT_PLANE_Z
            letterSpace = 0.0f
            letterScale = 1.5f
            color = Color(128, 255, 128, 255)
        }

        val b1 = Billboard().apply {
            size = Vector2(10.0f, 10.0f)
            position = Vector2(-31.0f, 16.0f)
            z = TEXT_PLANE_Z
            color = Color(255, 200, 128, 128)
        }

        val b2 = Billboard().apply {
            size = Vector2(10.0f, 10.0f)
            position = Vector2(-31.0f, 30.0f)
            z = TEXT_PLANE_Z
            color = Color(128, 200, 255, 128)
        }

        _ui.add(listOf(l1, l2, b1, b2))

        _ui.flush()
    }

    fun postLoop(): Unit {

    }

    override fun handleEvent(event: EventBase) {
        if (event is EventKey) {
            if (event.keyAction == KeyAction.PRESS) {
                when (event.key) {
                    256 -> {
                        run = false
                    }
                }
            }
        }
    }

    private fun sparseObjectsGraph(): StateGroup {
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

        val rootStateGroup = StateGroup(flatshadeProgram)

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

        for (i in 0..64) {
            val item = SpinableDrawable(diamondMesh)
            item.apply {
                color = randomVector3(0.1f, 0.9f)
                axis = randomVector3(-0.6f, 0.6f).normalize()
                anglSpeed = randomFloat(-1.0f, 1.0f)
            }

            val scale = Transform()
            val sf = randomFloat(0.2f, 0.6f)
            scale.matrix = algebra.scale(sf, sf, sf)

            val offset = Transform()
            val rz = randomFloat(-1.0f, -6.0f)
            val rtv = randomVector3(-4.0f, 4.0f)
            offset.matrix = algebra.offset(rtv.x, rtv.y, rz);

            scale.addChild(item)
            offset.addChild(scale)
            rootStateGroup.addChild(offset)
        }

        for (i in 0..16) {
            val item = SpinableDrawable(frameMesh)
            item.color = randomVector3(0.1f, 0.9f)
            item.axis = randomVector3(-0.6f, 0.6f).normalize()
            item.anglSpeed = randomFloat(0.4f, 1.2f)

            val scale = Transform()
            val sf = randomFloat(0.8f, 1.8f)
            scale.matrix = algebra.scale(sf, sf, sf)

            val offset = Transform()
            val rz = randomFloat(-1.0f, -6.0f)
            val rtv = randomVector3(-5.0f, 5.0f)
            offset.matrix = algebra.offset(rtv.x, rtv.y, rz);

            scale.addChild(item)
            offset.addChild(scale)
            rootStateGroup.addChild(offset)
        }

        for (i in 0..16) {
            val item = SpinableDrawable(arch01dMesh)
            item.color = randomVector3(0.1f, 0.9f)
            item.axis = randomVector3(-0.6f, 0.6f).normalize()
            item.anglSpeed = randomFloat(0.4f, 1.2f)

            val scale = Transform()
            val sf = randomFloat(0.8f, 1.4f)
            scale.matrix = algebra.scale(sf, sf, sf)

            val offset = Transform()
            val rz = randomFloat(-1.0f, -6.0f)
            val rtv = randomVector3(-6.0f, 6.0f)
            offset.matrix = algebra.offset(rtv.x, rtv.y, rz);

            scale.addChild(item)
            offset.addChild(scale)
            rootStateGroup.addChild(offset)
        }

        return rootStateGroup
    }

    private fun testFlyaroundsGraph(): StateGroup {
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

        val scale = Transform()
        scale.matrix = algebra.scale(0.5f, 0.5f, 0.5f)

        val offset = Transform()
        offset.matrix = algebra.offset(0.0f, 0.0f, 0.0f)

        scale.addChild(item)
        offset.addChild(scale)
        rootStateGroup.addChild(offset)

        return rootStateGroup
    }

    private fun testCubesGraph(): StateGroup {
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

        val scale = Transform()
        scale.matrix = algebra.scale(1.0f, 1.0f, 1.0f)

        val offset = Transform()
        offset.matrix = algebra.offset(0.0f, 0.0f, 0.0f)

        scale.addChild(item)
        offset.addChild(scale)
        rootStateGroup.addChild(offset)

        return rootStateGroup
    }
}
