plugins {
    kotlin("jvm") version "2.2.0"
}

repositories {
    mavenCentral()
}

val group = "com.example"
val version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.6"
val lwjglNatives = "natives-linux"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation(kotlin("test"))

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl", classifier = lwjglNatives)

    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)

    implementation("org.lwjgl", "lwjgl-opengl")
    // implementation("org.lwjgl", "lwjgl-unsafe")
}

kotlin {
    compilerOptions {
        // optIn.add("kotlin.RequiresOptIn")
    }
}

