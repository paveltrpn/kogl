package render

import scene.Scene
import config.Config

class Render {
    val scene = Scene()

    // NOTE: for test, see output, remove later.
    //var progOne: Program

    init {
        //progOne = Program("${Config.workPath()}/shaders", "flatshade")
    }

    fun preLoop(): Unit {
    }
    
    fun frame(): Unit {

    }

    fun postLoop(): Unit {

    }

    fun free() {

    }
}
