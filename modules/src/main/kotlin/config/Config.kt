package config

import kotlin.io.path.Path
import kotlin.io.path.pathString

class Config private constructor(path: String) {
    private val workPath: String

    companion object {
        @Volatile
        private var instance: Config? = null

        fun init(path: String): Config {
            return instance ?: synchronized(this) {
                instance ?: Config(path).also { instance = it }
            }
        }

        fun instance(): Config {
            return instance ?: throw IllegalStateException(
                "Config must be initialized by calling init() first."
            )
        }
    }

    init {
        val pathString = System.getProperty("user.dir")
        val path = Path(pathString)
        workPath = path.pathString
    }

    fun workPath(): String {
        return workPath
    }
}
