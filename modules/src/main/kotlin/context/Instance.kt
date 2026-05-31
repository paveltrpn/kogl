package context

import org.lwjgl.PointerBuffer

import org.lwjgl.glfw.GLFWVulkan

import org.lwjgl.vulkan.VK14.*
import org.lwjgl.vulkan.VkInstance
import org.lwjgl.vulkan.VkLayerProperties
import org.lwjgl.vulkan.VkApplicationInfo
import org.lwjgl.vulkan.VkInstanceCreateInfo

import java.nio.IntBuffer


class Instance : ResourceBase() {
    var mInstance: VkInstance

    init {
        mStack.push().use { stack ->
            val layerPropCount = stack.mallocInt(1)
            when (val result = vkEnumerateInstanceLayerProperties(layerPropCount, null)) {
                VK_SUCCESS -> {
                    println("number of enumerated layers: ${layerPropCount.get(0)}")
                }

                else -> {
                    throw RuntimeException("failed to enumerate layers: $result")
                }
            }

            val availableLayers = VkLayerProperties.calloc(layerPropCount.get(0), stack)

            when (val result = vkEnumerateInstanceLayerProperties(layerPropCount, availableLayers)) {
                VK_SUCCESS -> {
                    println("instance layers properties acquired")
                }

                else -> {
                    throw RuntimeException("failed to acquire instance layers properties: $result")
                }
            }

            val appInfo = VkApplicationInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(stack.UTF8("another tire"))
                .applicationVersion(VK_MAKE_VERSION(0, 1, 0))
                .pEngineName(stack.UTF8("another tire"))
                .engineVersion(VK_MAKE_VERSION(0, 1, 0))
                .apiVersion(VK_MAKE_VERSION(1, 4, 0))


            val ppEnabledExtensionNames = GLFWVulkan.glfwGetRequiredInstanceExtensions()
                ?: throw RuntimeException("failed to find the GLFW required Vulkan extensions")

            val requiredLayers = checkLayers(
                availableLayers,
                arrayOf<String>("VK_LAYER_KHRONOS_validation")
            )

            val createInfo = VkInstanceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pApplicationInfo(appInfo)
                .ppEnabledLayerNames(requiredLayers)
                .ppEnabledExtensionNames(ppEnabledExtensionNames)

            val instPtr = stack.mallocPointer(1)
            when (val result = vkCreateInstance(createInfo, null, instPtr)) {
                VK_SUCCESS -> {
                    println("vulkan instance created successfully")
                }

                else -> {
                    throw RuntimeException("Failed to create Vulkan instance: $result")
                }
            }
            mInstance = VkInstance(instPtr.get(0), createInfo)
        }
    }

    private fun checkLayers(
        available: VkLayerProperties.Buffer,
        layers: Array<String>
    ): PointerBuffer? {
        val required = mStack.mallocPointer(layers.size)
        for (i in layers.indices) {
            var found = false

            for (j in 0..<available.capacity()) {
                available.position(j)
                if (layers[i] == available.layerNameString()) {
                    found = true
                    break
                }
            }

            if (!found) {
                println("Cannot find layer: ${layers[i]}")
                return null
            }

            required.put(i, mStack.ASCII(layers[i]))
        }

        return required
    }

}
