plugins {
    kotlin("jvm") version "2.2.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

// Here for build fat jar ($ gradle shadowJar).
dependencies {
    implementation(project(":modules"))
    implementation(project(":kogl"))
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "kogl.MainKt"
    }
}



