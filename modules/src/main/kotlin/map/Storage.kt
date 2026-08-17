package map

import mesh.*

class Storage private constructor(storagePath: String) {
    private val _bodyStorage: HashMap<String, Mesh>? = null
    private val _materialStorage: HashMap<String, Material>? = null

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

    }
}
