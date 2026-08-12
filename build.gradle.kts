plugins {
    kotlin("jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(project(":modules"))
    implementation(project(":kogl"))
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "kogl.MainKt"
    }
}



