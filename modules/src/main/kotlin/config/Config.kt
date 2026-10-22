package config

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

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

private val DEFAULT_KONFIG = Konfig(
    application_name = "kogl",
    engine_name = "kogl",
    fullscreen = false,
    resizeable = true,
    window_width = 1920,
    window_height = 1080,
    window_pos_x = 100,
    window_pos_y = 100,
    background_color = "#0c0c1cff",
    doublebuffer = true,
    frame_count = 3,
    enable_vsync = true,
    enable_debug_output = false,
    ui_font = "DejaVu-Sans-Mono-1024-512-32-64-alpha.tga"
)

class Config private constructor() {
    private val _basePath: String
    private var _konfig: Konfig
    private var _dirty: Boolean = false

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

    fun updatePosition(x: Int, y: Int) {
        _konfig = _konfig.copy(window_pos_x = x, window_pos_y = y)
        _dirty = true
    }

    fun updateSize(width: Int, height: Int) {
        _konfig = _konfig.copy(window_width = width, window_height = height)
        _dirty = true
    }

    fun save() {
        if (!_dirty) {
            return
        }

        saveKonfig(_konfig)
        _dirty = false
    }

    private fun initBasePath(): String {
        val pathString = System.getProperty("user.dir")
        return pathString
    }

    private fun saveKonfig(konfig: Konfig) {
        val file = File("${_basePath}/assets/config.json")
        val json = Json {
            prettyPrint = true
        }

        file.writeText(json.encodeToString(konfig))
    }

    private fun initConfigFromJson(): Konfig {
        val file = File("${_basePath}/assets/config.json")

        if (!file.exists()) {
            saveKonfig(DEFAULT_KONFIG)
            return DEFAULT_KONFIG
        }

        val jsonString = file.readText()

        val json = Json {
            ignoreUnknownKeys = true
        }

        return json.decodeFromString<Konfig>(jsonString)
    }
}
