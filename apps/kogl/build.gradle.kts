plugins {
    application
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.6"
val lwjglNatives = "natives-linux"

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl", classifier = lwjglNatives)

    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)

    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)

    implementation(project(":modules"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    compilerOptions {
        // optIn.add("kotlin.RequiresOptIn")
    }
}

application {
    mainClass = "kogl.MainKt"
}

