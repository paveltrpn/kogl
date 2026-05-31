package context

import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.VkDevice

class Context(window: Long) {

    private var mInstance: Instance = Instance()
    private var mSurface: Surface
    private var mDevice: Device

    init {
        mSurface = Surface(instance, window)
        mDevice = Device(instance, surface)

        println("picked physical device name: $deviceName")
    }

    val instance: VkInstance
        get(): VkInstance {
            return mInstance.mInstance
        }

    val surface: Long
        get(): Long {
            return mSurface.mSurface
        }

    val deviceName: String
        get(): String {
            return mDevice.mPhysDeviceName
        }

    val device: VkDevice
        get(): VkDevice {
            return mDevice.mLogicalDevice
        }

    fun free() {
    }
}
