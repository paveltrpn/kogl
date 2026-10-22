package generator

import algebra.Vector2
import algebra.Vector3

enum class Axis(val mIndex: Int) {
    X(0),
    Y(1),
    Z(2)
}

class IntPair(val mX: Int, val mY: Int)

class IntTriple(val mX: Int, val mY: Int, val mZ: Int)

internal fun add(a: Vector2, b: Vector2): Vector2 {
    return Vector2(a.x + b.x, a.y + b.y)
}

internal fun sub(a: Vector2, b: Vector2): Vector2 {
    return Vector2(a.x - b.x, a.y - b.y)
}

internal fun scale(v: Vector2, s: Float): Vector2 {
    return Vector2(v.x * s, v.y * s)
}

internal fun dot(a: Vector2, b: Vector2): Float {
    return a.x * b.x + a.y * b.y
}

internal fun length(v: Vector2): Float {
    return kotlin.math.sqrt(v.x * v.x + v.y * v.y)
}

internal fun normalize(v: Vector2): Vector2 {
    val len = length(v)
    if (len > 0.0f) {
        return Vector2(v.x / len, v.y / len)
    }
    return Vector2()
}

internal fun perp(v: Vector2): Vector2 {
    return Vector2(-v.y, v.x)
}

internal fun mix(a: Vector2, b: Vector2, t: Float): Vector2 {
    return Vector2(a.x * (1.0f - t) + b.x * t, a.y * (1.0f - t) + b.y * t)
}

internal fun add(a: Vector3, b: Vector3): Vector3 {
    return Vector3(a.x + b.x, a.y + b.y, a.z + b.z)
}

internal fun sub(a: Vector3, b: Vector3): Vector3 {
    return Vector3(a.x - b.x, a.y - b.y, a.z - b.z)
}

internal fun scale(v: Vector3, s: Float): Vector3 {
    return Vector3(v.x * s, v.y * s, v.z * s)
}

internal fun mul(a: Vector3, b: Vector3): Vector3 {
    return Vector3(a.x * b.x, a.y * b.y, a.z * b.z)
}

internal fun length(v: Vector3): Float {
    return kotlin.math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
}

internal fun normalize(v: Vector3): Vector3 {
    val len = length(v)
    if (len > 0.0f) {
        return Vector3(v.x / len, v.y / len, v.z / len)
    }
    return Vector3()
}

internal fun dot(a: Vector3, b: Vector3): Float {
    return a.x * b.x + a.y * b.y + a.z * b.z
}

internal fun mix(a: Vector3, b: Vector3, t: Float): Vector3 {
    return add(scale(a, 1.0f - t), scale(b, t))
}

internal fun cross(a: Vector3, b: Vector3): Vector3 {
    return Vector3(
        a.y * b.z - a.z * b.y,
        a.z * b.x - a.x * b.z,
        a.x * b.y - a.y * b.x
    )
}

internal fun componentAt(v: Vector3, axis: Axis): Float {
    if (axis == Axis.X) {
        return v.x
    }
    if (axis == Axis.Y) {
        return v.y
    }
    return v.z
}

internal fun div(a: Vector2, b: Vector2): Vector2 {
    return Vector2(a.x / b.x, a.y / b.y)
}

internal fun min(a: Vector2, b: Vector2): Vector2 {
    return Vector2(kotlin.math.min(a.x, b.x), kotlin.math.min(a.y, b.y))
}

internal fun max(a: Vector2, b: Vector2): Vector2 {
    return Vector2(kotlin.math.max(a.x, b.x), kotlin.math.max(a.y, b.y))
}

internal fun normal(p1: Vector3, p2: Vector3, p3: Vector3): Vector3 {
    return normalize(cross(sub(p2, p1), sub(p3, p1)))
}

internal fun angle(v1: Vector3, v2: Vector3): Float {
    val len = kotlin.math.sqrt(dot(v1, v1) * dot(v2, v2))
    if (len <= 1.1920929E-7f) {
        return 0.0f
    }
    return kotlin.math.acos(kotlin.math.min(1.0f, kotlin.math.max(-1.0f, dot(v1, v2) / len)))
}

internal fun slerp(v1: Vector3, v2: Vector3, a: Float): Vector3 {
    val theta = angle(v1, v2)
    val sine = kotlin.math.sin(theta)
    return add(
        scale(v1, kotlin.math.sin((1.0f - a) * theta) / sine),
        scale(v2, kotlin.math.sin(a * theta) / sine)
    )
}

// Right-hand rotation of v around a unit axis (Rodrigues' formula).
// Matches gml::transform(gml::qrotate(angle, axis), v) from the C++ library.
internal fun rotate(v: Vector3, axis: Vector3, angle: Float): Vector3 {
    val c = kotlin.math.cos(angle)
    val s = kotlin.math.sin(angle)
    val term1 = scale(v, c)
    val term2 = scale(cross(axis, v), s)
    val term3 = scale(axis, dot(axis, v) * (1.0f - c))
    return add(add(term1, term2), term3)
}
