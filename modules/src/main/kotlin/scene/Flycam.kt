package scene

import algebra.*

enum class FlycamMoveBits {
    NONE, FORWARD, BACKWARD, LEFT, RIGHT, UP, DOWN
}

class Flycam {
    var fov: Double = 45.0
    var aspect: Double = 16.0 / 9.0
    var ncp: Double = 0.1
    var fcp: Double = 100.0

    var eye: Vector3 = Vector3(0.0f, 0.0f, 0.0f)
    var zenith: Vector3 = Vector3(0.0f, 1.0f, 0.0f)
    var right: Vector3 = Vector3(1.0f, 0.0f, 0.0f)
    var look: Vector3 = Vector3(0.0f, 0.0f, 1.0f)

    var azimuth: Double = 0.0
    var elevation: Double = 0.0
    var roll: Double = 0.0

    private var moveMask: Int = 0

    var name: String = ""

    constructor(eye: Vector3, azimuth: Double, elevation: Double) {
        this.eye = eye
        this.azimuth = azimuth
        this.elevation = elevation
        this.roll = 0.0
    }

    constructor() : this(Vector3(0.0f, 0.0f, 0.0f), 0.0, 0.0)

    fun setMoveBit(bit: FlycamMoveBits) {
        moveMask = moveMask or (1 shl bit.ordinal)
    }

    fun unsetMoveBit(bit: FlycamMoveBits) {
        moveMask = moveMask and (1 shl bit.ordinal).inv()
    }

    fun unsetMoveAll() {
        moveMask = 0
    }

    fun rotate(azimuthOffset: Double, elevationOffset: Double) {
        azimuth += azimuthOffset

        if (azimuth > 360.0 || azimuth < -360.0) azimuth = 0.0

        elevation += elevationOffset

        val elevationBound = 80.0
        if (elevation > elevationBound) elevation = elevationBound
        if (elevation < -elevationBound) elevation = -elevationBound
    }

    fun setPosition(pos: Vector3) {
        eye = pos
    }

    fun setAngles(azimuth: Double, elevation: Double, roll: Double) {
        this.azimuth = azimuth
        this.elevation = elevation
        this.roll = roll
    }

    fun traverse() {
        val velocity = Vector3(0.0f, 0.0f, 0.0f)

        if ((moveMask shr FlycamMoveBits.FORWARD.ordinal) and 1 == 1)
            velocity.x += look.x
        if ((moveMask shr FlycamMoveBits.BACKWARD.ordinal) and 1 == 1)
            velocity.x -= look.x
        if ((moveMask shr FlycamMoveBits.RIGHT.ordinal) and 1 == 1)
            velocity.x -= right.x
        if ((moveMask shr FlycamMoveBits.LEFT.ordinal) and 1 == 1)
            velocity.x += right.x

        eye.x += velocity.x * 0.5f
        eye.y += velocity.y * 0.5f
        eye.z += velocity.z * 0.5f
    }
}
