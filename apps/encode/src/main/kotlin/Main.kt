package encode

import image.*

fun main(args: Array<String>) {
    println("encode")

    var canvas = Canvas(128, 128)
    canvas.fillWith(Color(125, 12, 12, 255))
    canvas.save("out.ppm")
}
