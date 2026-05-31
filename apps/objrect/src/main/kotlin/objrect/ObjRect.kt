package objrect

import java.io.File
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.CliktError
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt

// Usage:
// $ gradle :objrect:run --args="-i {what}.obj -out {outname}"

class ObjRectifier : CliktCommand() {
    private val inputObjFilePath: String by option("-i").prompt().help("Path to input wavefront obj file")
    private val out: String by option("-out").prompt().help("Output fileset prefix name (without extension")

    private val wd: String = "src/main/resources/"

    override fun run() {
        val obj = ObjFile(wd + inputObjFilePath)

        when {
            out.isNotEmpty() -> {
                val plain = obj.outPlainVertecies()
                File("$wd$out-plain.json").bufferedWriter().use { out ->
                    out.write(plain)
                }

                val indexed = obj.outIndexed()
                File("$wd$out-indexed.json").bufferedWriter().use { out ->
                    out.write(indexed)
                }

                val plainCsv = obj.outCsvPlainVertecies()
                File("$wd$out-plain.csv").bufferedWriter().use { out ->
                    out.write(plainCsv)
                }
            }
        }
        // println(obj.outIndexed())
        // obj.out()
    }
}

fun main(args: Array<String>) {
    try {
        ObjRectifier().main(args)
    } catch (e: Exception) {
        println("catch some exception")
        println(e.toString())
    }
}