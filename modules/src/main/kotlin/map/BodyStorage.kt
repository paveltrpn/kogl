package map

import mesh.*

class BodyStorage private constructor(storagePath: String) {
    private var _storage: HashMap<String, Mesh>? = null

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
