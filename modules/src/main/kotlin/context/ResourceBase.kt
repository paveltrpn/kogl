package context

import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.*
import java.nio.IntBuffer
import java.nio.LongBuffer

open class ResourceBase {
    protected val mStack = MemoryStack.create()
}
