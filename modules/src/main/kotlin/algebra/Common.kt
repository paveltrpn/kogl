package algebra

import kotlin.math.PI

fun toRadians(degrees: Float): Float {
    return degrees * PI.toFloat() / 180.0f
}

fun toRadians(degrees: Double): Double {
    return degrees * PI / 180.0
}

fun toDegrees(radians: Float): Float {
    return radians * 180.0f / PI.toFloat()
}

fun toDegrees(radians: Double): Double {
    return radians * 180.0 / PI
}

fun toGradians(degrees: Float): Float {
    return degrees * 400.0f / 360.0f
}

fun toGradians(degrees: Double): Double {
    return degrees * 400.0 / 360.0
}

fun toDegreesGradians(gradians: Float): Float {
    return gradians * 360.0f / 400.0f
}

fun toDegreesGradians(gradians: Double): Double {
    return gradians * 360.0 / 400.0
}
