package map

import mesh.*
import java.io.File

class Storage private constructor(storagePath: String) {
    private val _bodyStorage: Map<String, Mesh>
//    private val _materialStorage: Map<String, Material>

    companion object {
        @Volatile
        private var instance: Storage? = null

        fun init(storagePath: String): Storage {
            return instance ?: synchronized(this) {
                instance ?: Storage(storagePath).also { instance = it }
            }
        }

        fun instance(): Storage {
            return instance ?: throw IllegalStateException(
                "BodyStorage must be initialized by calling init() first."
            )
        }
    }

    init {
        val storageDir = File("$storagePath/assets/bodies")
        if (!storageDir.exists() || !storageDir.isDirectory) {
            throw RuntimeException("Wrong storage directory \"$storagePath\"!")
        }

        val objFiles = storageDir.listFiles { file ->
            file.isFile && file.extension.equals("obj", ignoreCase = true)
        } ?: throw RuntimeException("Wrong storage directory content \"$storagePath\"!")

        if (objFiles.isEmpty()) {
            throw RuntimeException("No \"obj\" file found in directory \"$storagePath\"!")
        }

        val bs: MutableMap<String, Mesh> = mutableMapOf()
        for (file in objFiles) {
            val nameWithoutExtension = file.nameWithoutExtension
            val mesh = readWavefrontObjFile(file.absolutePath)
            bs[nameWithoutExtension] = SeparatedArraysMesh(mesh)
        }

        _bodyStorage = bs
    }

    val bodyStorage: Map<String, Mesh>
        get() {
            return _bodyStorage
        }

    operator fun get(name: String): Mesh {
        return _bodyStorage[name] ?: throw RuntimeException("No mesh named \"$name\" in storage!")
    }
}
