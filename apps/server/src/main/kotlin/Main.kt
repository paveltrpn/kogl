package server

import java.io.File

import io.javalin.Javalin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import image.*

class WTireServer {
    val log: Logger = LoggerFactory.getLogger(WTireServer::class.java)

    val host = "localhost"
    val port = 8081

    val app: Javalin = Javalin.create { config -> { config.bundledPlugins.enableDevLogging() } }

    constructor() {
    }

    fun run() {
        app.get("/") { ctx ->
                log.info("Request received")

                val fileName = "someFile"
                val fileContents: String = File(fileName).readText()

                if (fileContents != null) {
                    ctx.contentType("application/pdf")

                    ctx.header("Content-Disposition", "attachment; filename=\"$fileName\"")

                    ctx.result(fileContents)

                    ctx.status(200)

                } else {
                    ctx.status(404)
                    ctx.result("File not found")
                    println("===== request $ctx.path")
                }
        }.get("/test") { ctx ->
                log.debug("Request received")
                log.trace("Request received")

                ctx.result("<html><body><h1>This is a custom response</h1></body></html>")
                    .status(200)
                    .contentType("text/html")
        }.start(host, port)
    }
}

fun main(args: Array<String>) {

    val server = WTireServer()

    server.run()

    // app.get("/", (req, res) => {
    // res.sendFile(path.join(workpath, "apps/tire", "index.html"));
    // });
//
    // app.get("/modules/*js", (req, res) => {
    // const file = path . join (workpath, "dist/", req.url);
    // res.sendFile(file);
    // });
//
    // app.get("/shaders/*glsl", (req, res) => {
    // const file = path . join (workpath, "./", req.url);
    // res.sendFile(file);
    // });
//
    // app.get("/*js", (req, res) => {
    // const file = path . join (workpath, "dist/apps/tire", req.url);
    // res.sendFile(file);
    // });
//
    // app.listen(port, () => {
    // console.log(`Example app listening on port ${port}`);
    // });


}
