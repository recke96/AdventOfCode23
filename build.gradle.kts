plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "com.github.recke96.aoc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ajalt.clikt:clikt:4.2.1")
}

kotlin {
    jvmToolchain(21)
    sourceSets.all {
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

application {
    mainClass.set("com.github.recke96.aoc.MainKt")
}
