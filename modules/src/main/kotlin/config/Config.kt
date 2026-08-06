package config

import kotlin.io.path.Path
import kotlin.io.path.pathString

import kotlinx.serialization.Serializable

@Serializable
data class Konfig(
    val applicationName: String,
    val engineName: String,
    val fullscreen: Boolean,
    val resizeable: Boolean,
    val windowWidth: Int,
    val windowHeight: Int,
    val windowPosX: Int,
    val windowPosY: Int,
    val backgroundColor: String,
    val doublebuffer: Boolean,
    val frameCount: Int,
    val enableVsync: Boolean,
    val enableDebugOutput: Boolean,
    val uiFont: String
)

class Config private constructor(path: String) {
    private val _basePath: String

    init {
        val pathString = System.getProperty("user.dir")

        val path = Path(pathString)

        _basePath = path.pathString
    }

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


    val basePath: String
        get(): String {
            return _basePath
        }
}
