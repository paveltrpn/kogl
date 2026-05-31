package context

import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.memAllocPointer
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR
import org.lwjgl.vulkan.VK10.vkEnumerateDeviceExtensionProperties
import org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices
import org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceQueueFamilyProperties
import org.lwjgl.vulkan.VK14.*


//import org.lwjgl.vulkan.KHRSwapchain.*

class Device(val instance: VkInstance, surface: Long) : ResourceBase() {
    // Data of picked physical device.
    val mPhysDevice: VkPhysicalDevice
    val mPhysDeviceName: String
    val mGraphicsQueueNodeIndex: Int
    val mPresentQueueNodeIndex: Int

    // Logical device properties.
    val mLogicalDevice: VkDevice

    init {
        mStack.push().use { stack ->
            //  Physical devices count.
            val physDevCount = stack.mallocInt(1)
            when (val result = vkEnumeratePhysicalDevices(instance, physDevCount, null)) {
                VK_SUCCESS -> {
                    println("number of enumerated physical devices: ${physDevCount.get(0)}")
                }

                else -> {
                    throw RuntimeException("failed to enumerate physical devices: $result")
                }
            }

            val physicalDevices: PointerBuffer = stack.callocPointer(physDevCount.get(0))

            // Obtain physical devices.
            when (val result = vkEnumeratePhysicalDevices(instance, physDevCount, physicalDevices)) {
                VK_SUCCESS -> {
                    println("physical devices acquired")
                }

                else -> {
                    throw RuntimeException("failed to acquire physical devices: $result")
                }
            }

            // Some lists for physical device to pick one later.
            val physDevList = ArrayList<VkPhysicalDevice>()
            val devPropList = ArrayList<VkPhysicalDeviceProperties>()
            val devFeatureList = ArrayList<VkPhysicalDeviceFeatures>()
            val queueFamilyPropsList = ArrayList<VkQueueFamilyProperties.Buffer>()

            // Get properties of each physical device.
            for (i in 0..<physDevCount.get(0)) {
                val device = VkPhysicalDevice(physicalDevices.get(i), instance)
                // Store physical device handle in list.
                physDevList.add(device);

                val devProps = VkPhysicalDeviceProperties.calloc(stack)
                vkGetPhysicalDeviceProperties(device, devProps)

                // Store physical device properties in list.
                devPropList.add(devProps)

                val devFeatures = VkPhysicalDeviceFeatures.calloc(stack);
                vkGetPhysicalDeviceFeatures(device, devFeatures)

                // Store physical device features in list.
                devFeatureList.add(devFeatures)

                //  Physical device queue families count.
                val queueFamilyCount = stack.mallocInt(1)
                vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);

                val queueFamilyProps = VkQueueFamilyProperties.calloc(queueFamilyCount.get(0), stack)
                vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilyProps)

                queueFamilyPropsList.add(queueFamilyProps)

                //  Physical device extensions count.
                val devExtCount = stack.mallocInt(1)
                when (val result = vkEnumerateDeviceExtensionProperties(device, null as String?, devExtCount, null)) {
                    VK_SUCCESS -> {
//                        println("physical device extensions enumerated for device")
                    }

                    else -> {
                        throw RuntimeException("can't enumerate physical device extensions: $result")
                    }
                }

                val deviceExtensions = VkExtensionProperties.calloc(devExtCount.get(0))

                when (val result =
                    vkEnumerateDeviceExtensionProperties(device, null as String?, devExtCount, deviceExtensions)) {
                    VK_SUCCESS -> {
//                        println("physical device extensions acquired for device")
                    }

                    else -> {
                        throw RuntimeException("can't acquire physical device extensions: $result")
                    }
                }
            }

            // Pick one suitable physical device across
            // all available devices to use it.
            val pickedDevIndex = pick(devPropList)
            mPhysDevice = physDevList[pickedDevIndex]
            mPhysDeviceName = devPropList[pickedDevIndex].deviceNameString()

            val (gq, pq) = getGraphicsAndPresentIndex(queueFamilyPropsList[pickedDevIndex], mPhysDevice, surface)
            mGraphicsQueueNodeIndex = gq
            mPresentQueueNodeIndex = pq

            mLogicalDevice = initLogicalDevice(devFeatureList[pickedDevIndex])
        }
    }

    fun pick(props: ArrayList<VkPhysicalDeviceProperties>): Int {
        // Some types of GPU's that can be available on the machine.
        var discreetGpuId: Int = -1
        var integratedGpuId: Int = -1
        var otherGpuId: Int = -1
        var virtualGpuId: Int = -1
        var cpuGpuId: Int = -1

        for (i in 0..<props.size) {
            val deviceProps = props[i];

            // Store id of appropriate GPU type.
            when (deviceProps.deviceType()) {
                VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU -> {
                    discreetGpuId = i;
                }

                VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU -> {
                    integratedGpuId = i;
                }

                VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU -> {
                    virtualGpuId = i;
                }

                VK_PHYSICAL_DEVICE_TYPE_CPU -> {
                    cpuGpuId = i;
                }

                VK_PHYSICAL_DEVICE_TYPE_OTHER -> {
                    otherGpuId = i;
                }
            }
        }

        // First try to pick discrete GPU.
        return if (discreetGpuId != -1) {
            discreetGpuId
        } else if (integratedGpuId != -1) {
            integratedGpuId
        } else if (cpuGpuId != -1) {
            cpuGpuId;
        } else {
            throw RuntimeException("no suitable vulkan devices found");
        }
    }

    fun getGraphicsAndPresentIndex(
        queueProps: VkQueueFamilyProperties.Buffer,
        gpu: VkPhysicalDevice,
        surface: Long
    ): Pair<Int, Int> {
        mStack.push().use { stack ->
            // Iterate over each queue to learn whether it supports presenting:
            val supportsPresent = stack.mallocInt(queueProps.capacity());
            var graphicsQueueNodeIndex: Int = Integer.MAX_VALUE
            var presentQueueNodeIndex: Int = Integer.MAX_VALUE
            for (i in 0..<supportsPresent.capacity()) {
                supportsPresent.position(i);
                vkGetPhysicalDeviceSurfaceSupportKHR(gpu, i, surface, supportsPresent);

                // Search for a graphics and a present queue in the array of queue
                // families, try to find one that supports both
                graphicsQueueNodeIndex = Integer.MAX_VALUE
                presentQueueNodeIndex = Integer.MAX_VALUE
                for (i in 0..<supportsPresent.capacity()) {
                    if ((queueProps.get(i).queueFlags() and VK_QUEUE_GRAPHICS_BIT) != 0) {
                        if (graphicsQueueNodeIndex == Integer.MAX_VALUE) {
                            graphicsQueueNodeIndex = i;
                        }

                        if (supportsPresent.get(i) == VK_TRUE) {
                            graphicsQueueNodeIndex = i;
                            presentQueueNodeIndex = i;
                            break;
                        }
                    }
                }
                if (presentQueueNodeIndex == Integer.MAX_VALUE) {
                    // If didn't find a queue that supports both graphics and present, then
                    // find a separate present queue.
                    for (i in 0..<supportsPresent.capacity()) {
                        if (supportsPresent.get(i) == VK_TRUE) {
                            presentQueueNodeIndex = i;
                            break;
                        }
                    }
                }
            }

            // Generate error if could not find both a graphics and a present queue
            if (graphicsQueueNodeIndex == Integer.MAX_VALUE || presentQueueNodeIndex == Integer.MAX_VALUE) {
                throw RuntimeException("Could not find a graphics and a present queue");
            }

            // TODO: Add support for separate queues, including presentation,
            //       synchronization, and appropriate tracking for QueueSubmit.
            // NOTE: While it is possible for an application to use a separate graphics
            //       and a present queues, this program assumes it is only using
            //       one:
            if (graphicsQueueNodeIndex != presentQueueNodeIndex) {
                throw RuntimeException("Could not find a common graphics and a present queue");
            }

            return Pair(graphicsQueueNodeIndex, presentQueueNodeIndex)
        }
    }

    fun initLogicalDevice(deviceFeatures: VkPhysicalDeviceFeatures): VkDevice {
        val desiredExtensions: PointerBuffer = memAllocPointer(32)

//        NOTE: This call return array with two extensions - "VK_KHR_surface" and
//        "VK_KHR_xcb_surface" on Linux. Adding this two to list cause fail creating logical device.
        val loadPlatformExt: (Boolean) -> Unit = { use ->
            if (use) {
                val requiredExtensions: PointerBuffer? = glfwGetRequiredInstanceExtensions()
                requiredExtensions
                    ?: throw RuntimeException("glfwGetRequiredInstanceExtensions failed to find the platform surface extensions.")
                for (i in 0..<requiredExtensions.limit()) {
                    desiredExtensions.put(requiredExtensions.get(i))
                }
            }
        }

        val loadHazardExt: (Boolean) -> Unit = { use ->
            if (use) {
                arrayOf(
                    "VK_KHR_surface",
                    "VK_KHR_xcb_surface",
                ).forEach { desiredExtensions.put(MemoryUtil.memASCII(it, true)) }
            }
        }

        val loadBasicExt: (Boolean) -> Unit = { use ->
            if (use) {
                arrayOf(
                    "VK_KHR_swapchain",
                    "VK_KHR_spirv_1_4",
                ).forEach { desiredExtensions.put(MemoryUtil.memASCII(it, true)) }
            }
        }

        val loadRTExt: (Boolean) -> Unit = { use ->
            if (use) {
                arrayOf(
                    "VK_KHR_ray_query",
                    "VK_KHR_ray_tracing_pipeline",
                    "VK_KHR_ray_tracing_maintenance1",
                    "VK_KHR_ray_tracing_position_fetch",
                    "VK_KHR_acceleration_structure",
                    "VK_EXT_descriptor_indexing",
                    "VK_KHR_buffer_device_address",
                    "VK_KHR_deferred_host_operations",
                    "VK_KHR_shader_float_controls"
                ).forEach { desiredExtensions.put(MemoryUtil.memASCII(it, true)) }
            }
        }

        loadPlatformExt(false)
        loadHazardExt(false)
        loadBasicExt(true)
        loadRTExt(false)

        desiredExtensions.flip()

        val desiredValidationLayers: PointerBuffer = memAllocPointer(32)

        val loadBasicLayers: (Boolean) -> Unit = { use ->
            if (use) {
                arrayOf(
                    "VK_LAYER_KHRONOS_validation",
                ).forEach { desiredValidationLayers.put(MemoryUtil.memASCII(it, true)) }
            }
        }

        loadBasicLayers(true)
        desiredValidationLayers.flip()

        val reportExtensions: (Boolean) -> Unit = { use ->
            if (use) {
                for (item in 0..<desiredExtensions.limit()) {
                    println("${MemoryUtil.memASCII(desiredExtensions.get(item))}")
                }
            }
        }

        val reportLayers: (Boolean) -> Unit = { use ->
            if (use) {
                for (item in 0..<desiredValidationLayers.limit()) {
                    println("${MemoryUtil.memASCII(desiredValidationLayers.get(item))}")
                }
            }
        }

        reportExtensions(true)
        reportLayers(true)

        mStack.push().use { stack ->
            val gq = VkDeviceQueueCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                .queueFamilyIndex(mGraphicsQueueNodeIndex)
                .pQueuePriorities(stack.floats(0.0f))

//            val pq = VkDeviceQueueCreateInfo.calloc(stack)
//                .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
//                .queueFamilyIndex(mPresentQueueNodeIndex)
//                .pQueuePriorities(stack.floats(0.0f))

            val queues = VkDeviceQueueCreateInfo.calloc(1, stack)

            queues.put(gq)
//            queues.put(pq)
            queues.flip()

            val features = VkPhysicalDeviceFeatures.calloc(stack)
            if (deviceFeatures.shaderClipDistance()) {
                features.shaderClipDistance(true)
            }

            val deviceCreateInfo = VkDeviceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                .pQueueCreateInfos(queues)
                .ppEnabledLayerNames(desiredValidationLayers)
                .ppEnabledExtensionNames(desiredExtensions)
                .pEnabledFeatures(features)

            val devPtr = stack.mallocPointer(1)
            when (val result = vkCreateDevice(mPhysDevice, deviceCreateInfo, null, devPtr)) {
                VK_SUCCESS -> {
                    println("logical device created")
                }

                else -> {
                    throw RuntimeException("failed to create logical device: $result")
                }
            }

            return VkDevice(devPtr.get(0), mPhysDevice, deviceCreateInfo)
        }


    }

    // NOTE: unused!
    fun graphicsFamilyQueueId(flags: ArrayList<Int>): Int {
        // Choose queue family with VK_QUEUE_GRAPHICS_BIT.
        // The good news is that
        // any queue family with VK_QUEUE_GRAPHICS_BIT or VK_QUEUE_COMPUTE_BIT
        // capabilities already implicitly support VK_QUEUE_TRANSFER_BIT operations.
        for (i in 0..<flags.size) {
            if ((flags[i] and VK_QUEUE_GRAPHICS_BIT) == 1) {
                return i;
            }
        }

        throw RuntimeException("failed to get device with queue family that VK_QUEUE_GRAPHICS_BIT")
    }
}
