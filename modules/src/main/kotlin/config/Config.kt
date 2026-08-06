package config

import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import java.io.File
import java.io.FileNotFoundException

@Serializable
data class Konfig(
    val application_name: String,
    val engine_name: String,
    val fullscreen: Boolean,
    val resizeable: Boolean,
    val window_width: Int,
    val window_height: Int,
    val window_pos_x: Int,
    val window_pos_y: Int,
    val background_color: String,
    val doublebuffer: Boolean,
    val frame_count: Int,
    val enable_vsync: Boolean,
    val enable_debug_output: Boolean,
    val ui_font: String
)

class Config private constructor() {
    private val _basePath: String
    private val _konfig: Konfig

    init {
        _basePath = initBasePath()
        _konfig = initConfigFromJson()
    }

    companion object {
        @Volatile
        private var instance: Config? = null

        fun init(): Config {
            return instance ?: synchronized(this) {
                instance ?: Config().also { instance = it }
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

    val konfig: Konfig
        get(): Konfig {
            return _konfig
        }

    private fun initBasePath(): String {
        val pathString = System.getProperty("user.dir")
        return pathString
    }

    private fun initConfigFromJson(): Konfig {
        val file = File("${_basePath}/assets/config.json")

        if (!file.exists()) {
            throw FileNotFoundException("File \"${_basePath}/assets/config.json\" not exist!")
        }

        val jsonString = file.readText()

//        val json = Json {
//            ignoreUnknownKeys = true
//            namingStrategy = JsonNamingStrategy.SnakeCase
//        }

        return Json.decodeFromString<Konfig>(jsonString)
    }
}
