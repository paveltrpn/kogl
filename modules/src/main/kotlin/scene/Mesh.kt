package scene

class Mesh {
    var vertecies: FloatArray? = null
        set(value: FloatArray?): Unit {
            field = value
        }

    var indicies: IntArray? = null
        set(value: IntArray?): Unit {
            field = value
        }

    var normals: FloatArray? = null
        set(value: FloatArray?): Unit {
            field = value
        }

    var texcrds: FloatArray? = null
        set(value: FloatArray?): Unit {
            field = value
        }

    var vertcolors: FloatArray? = null
        set(value: FloatArray?): Unit {
            field = value
        }
}
