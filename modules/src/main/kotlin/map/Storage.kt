package map

import mesh.*

class BodyStorage private constructor(storagePath: String) {
    private val _bodyStorage: HashMap<String, Mesh>? = null
    private val _materialStorage: HashMap<String, Material>? = null

    companion object {
        @Volatile
        private var instance: BodyStorage? = null

        fun init(storagePath: String): BodyStorage {
            return instance ?: synchronized(this) {
                instance ?: BodyStorage(storagePath).also { instance = it }
            }
        }

        fun instance(): BodyStorage {
            return instance ?: throw IllegalStateException(
                "BodyStorage must be initialized by calling init() first."
            )
        }
    }

    init {

    }
}
