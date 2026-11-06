package userinterface

import java.nio.*

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.*
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.nuklear.*
import org.lwjgl.nuklear.Nuklear.*

import config.*
import events.*
import render.*

// ============================================================================
// ======================= NuklearUi ==========================================
// ============================================================================

class NuklearUi(
    val mBackend: NuklearGlBackend,
    private val _eventBus: EventBus
) : UiDrawable, EventBusNode {
    private val _allocator: NkAllocator = NkAllocator.create()
        .alloc(NkPluginAllocI { handle: Long, old: Long, size: Long -> nmemAllocChecked(size) })
        .mfree(NkPluginFreeI { handle: Long, ptr: Long -> nmemFree(ptr) })

    private val _ctx: NkContext = NkContext.create()
    private val _atlas: NkFontAtlas = NkFontAtlas.create()
    private val _textureHandle: NkHandle = NkHandle.create()
    private val _drawNullTexture: NkDrawNullTexture = NkDrawNullTexture.create()
    private val _convertConfig: NkConvertConfig = NkConvertConfig.create()
    private val _vertexLayout: NkDrawVertexLayoutElement.Buffer = NkDrawVertexLayoutElement.create(4)

    private val _cmds: NkBuffer = NkBuffer.create()
    private val _vbuf: NkBuffer = NkBuffer.create()
    private val _ebuf: NkBuffer = NkBuffer.create()

    private val _panelRect: NkRect = NkRect.create()
        .x(10.0f)
        .y(10.0f)
        .w(220.0f)
        .h(160.0f)

    private val _eventQueue: ArrayDeque<WindowEvent> = ArrayDeque()

    private var _vsyncEnabled: Boolean

    private var _frameDurationText: String = "frame duration: --"

    private var _mouseX: Int = 0
    private var _mouseY: Int = 0

    init {
        if (!nk_init(_ctx, _allocator, null)) {
            throw RuntimeException("failed to initialize nuklear context")
        }

        nk_font_atlas_init(_atlas, _allocator)
        nk_font_atlas_begin(_atlas)

        var bakedFont: NkFont?

        MemoryStack.stackPush().use { stack: MemoryStack ->
            bakedFont =
                nk_font_atlas_add_from_file(
                    _atlas,
                    Config.instance().basePath + "/assets/LessPerfectDOSVGA.ttf",
                    18f,
                    null
                )

            val widthBuf = stack.mallocInt(1)
            val heightBuf = stack.mallocInt(1)

            val atlasPixels = nk_font_atlas_bake(_atlas, widthBuf, heightBuf, NK_FONT_ATLAS_RGBA32)

            if (atlasPixels != null) {
                mBackend.uploadFontTexture(atlasPixels, widthBuf.get(0), heightBuf.get(0))
            } else {
                throw RuntimeException("failed to bake nuklear font atlas")
            }
        }

        val TEXTURE_ID = 1
        nk_handle_id(TEXTURE_ID, _textureHandle)
        nk_font_atlas_end(_atlas, _textureHandle, _drawNullTexture)

        if (bakedFont != null) {
            nk_style_set_font(_ctx, bakedFont.handle())
        } else {
            throw RuntimeException("failed to add default font to nuklear font atlas")
        }

        _vertexLayout.get(0)
            .attribute(NK_VERTEX_POSITION)
            .format(NK_FORMAT_FLOAT)
            .offset(0)
        _vertexLayout.get(1)
            .attribute(NK_VERTEX_TEXCOORD)
            .format(NK_FORMAT_FLOAT)
            .offset(8)
        _vertexLayout.get(2)
            .attribute(NK_VERTEX_COLOR)
            .format(NK_FORMAT_R8G8B8A8)
            .offset(16)
        _vertexLayout.get(3)
            .attribute(NK_VERTEX_ATTRIBUTE_COUNT)
            .format(NK_FORMAT_COUNT)
            .offset(0)

        _convertConfig.vertex_layout(_vertexLayout)
        _convertConfig.vertex_size(20)
        _convertConfig.vertex_alignment(4)
        _convertConfig.tex_null(_drawNullTexture)
        _convertConfig.circle_segment_count(22)
        _convertConfig.curve_segment_count(22)
        _convertConfig.arc_segment_count(22)
        _convertConfig.global_alpha(1.0f)
        _convertConfig.shape_AA(NK_ANTI_ALIASING_ON)
        _convertConfig.line_AA(NK_ANTI_ALIASING_ON)

        nk_buffer_init(_cmds, _allocator, 4L * 1024L)
        nk_buffer_init(_vbuf, _allocator, 512L * 1024L)
        nk_buffer_init(_ebuf, _allocator, 128L * 1024L)

        _eventBus.subscribe<WindowEvent>(this)
        _eventBus.subscribe<UiNotification>(this)

        _vsyncEnabled = Config.instance().konfig.enable_vsync


    }

    override fun onEvent(event: Event): Unit {
        when (event) {
            is WindowMouseMoveEvent -> {
                _eventQueue.addLast(event)
            }

            is WindowMousePress -> {
                _eventQueue.addLast(event)
            }

            is WindowMouseRelease -> {
                _eventQueue.addLast(event)
            }

            is UiNotification.VsyncStateChanged -> {
                _vsyncEnabled = event.enabled
            }

            is UiNotification.FrameDuration -> {
                _frameDurationText = "frame duration: ${event.milliseconds} ms"
            }

            else -> {
            }
        }
    }

    override fun isMouseOver(posX: Int, posY: Int): Boolean {
        val x = posX.toFloat()
        val y = posY.toFloat()

        return x >= _panelRect.x() && x < _panelRect.x() + _panelRect.w() &&
                y >= _panelRect.y() && y < _panelRect.y() + _panelRect.h()
    }

    override fun update(): Unit {
        nk_buffer_clear(_cmds)
        nk_buffer_clear(_vbuf)
        nk_buffer_clear(_ebuf)

        nk_input_begin(_ctx)

        while (_eventQueue.isNotEmpty()) {
            val mapButton: (Int) -> Int = { key: Int ->
                when (key) {
                    GLFW_MOUSE_BUTTON_LEFT -> {
                        NK_BUTTON_LEFT
                    }

                    GLFW_MOUSE_BUTTON_RIGHT -> {
                        NK_BUTTON_RIGHT
                    }

                    GLFW_MOUSE_BUTTON_MIDDLE -> {
                        NK_BUTTON_MIDDLE
                    }

                    else -> {
                        NK_BUTTON_LEFT
                    }
                }
            }

            when (val event = _eventQueue.removeFirst()) {
                is WindowMouseMoveEvent -> {
                    _mouseX = event.posX
                    _mouseY = event.posY
                    nk_input_motion(_ctx, event.posX, event.posY)
                }

                is WindowMousePress -> {
                    _mouseX = event.posX
                    _mouseY = event.posY
                    nk_input_button(_ctx, mapButton(event.keyCode), _mouseX, _mouseY, true)
                }

                is WindowMouseRelease -> {
                    _mouseX = event.posX
                    _mouseY = event.posY
                    nk_input_button(_ctx, mapButton(event.keyCode), _mouseX, _mouseY, false)
                }

                else -> {
                }
            }
        }

        nk_input_end(_ctx)

        buildWidgets()

        mBackend.convert(_ctx, _cmds, _vbuf, _ebuf, _convertConfig)

        nk_clear(_ctx)
    }

    override fun draw() {
        mBackend.draw(_ctx, _cmds)
    }

    override fun free() {
        nk_buffer_free(_cmds)
        nk_buffer_free(_vbuf)
        nk_buffer_free(_ebuf)

        nk_font_atlas_clear(_atlas)
        nk_free(_ctx)

        _cmds.free()
        _vbuf.free()
        _ebuf.free()
        _atlas.free()
        _textureHandle.free()
        _drawNullTexture.free()
        _convertConfig.free()
        _vertexLayout.free()
        _panelRect.free()
        _ctx.free()
        _allocator.free()
    }

    private fun buildWidgets(): Unit {
        val panelVisible = nk_begin(
            _ctx,
            "panel",
            _panelRect,
            NK_WINDOW_BORDER or NK_WINDOW_MOVABLE
        )

        nk_window_get_bounds(_ctx, _panelRect)

        if (panelVisible) {
            nk_layout_row_dynamic(_ctx, 28.0f, 1)

            MemoryStack.stackPush().use { stack: MemoryStack ->
                val vsyncState = stack.malloc(4)

                if (_vsyncEnabled) {
                    vsyncState.putInt(0, 1)
                } else {
                    vsyncState.putInt(0, 0)
                }

                val vsyncChanged = nk_checkbox_label(_ctx, "VSync", vsyncState)


                if (vsyncChanged) {
                    val requestedVsyncEnabled = vsyncState.getInt(0) != 0
                    _eventBus.publish(AppCommand.SetVsync(requestedVsyncEnabled))
                }
            }

            nk_layout_row_dynamic(_ctx, 28.0f, 1)
            nk_label(_ctx, _frameDurationText, NK_TEXT_CENTERED)

            nk_spacing(_ctx, 0)

            nk_layout_row_dynamic(_ctx, 28.0f, 3)

            nk_spacing(_ctx, 1)

            if (nk_button_label(_ctx, "Exit")) {
                _eventBus.publish(AppCommand.Exit)
            }
        }

        nk_end(_ctx)
    }
}
