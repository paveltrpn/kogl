package scene

import kotlin.io.path.Path
import kotlin.io.path.exists
import java.io.FileNotFoundException

fun reaadObjFile(path: String): Mesh {
    val file = Path(path)
    if (!file.exists()) {
        throw FileNotFoundException("object file not found at path \"${path}\"")
    }
    return Mesh()
}
