plugins {
    application
    kotlin("jvm") version "2.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.ajalt.clikt:clikt:4.4.0")
    implementation("com.google.code.gson:gson:2.11.0")
}

application {
    mainClass = "objrect.ObjRectKt"
}
