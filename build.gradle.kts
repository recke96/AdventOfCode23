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
    implementation("me.alllex.parsus:parsus-jvm:0.6.0")
}

kotlin {
    sourceSets.all {
        jvmToolchain(21)
        languageSettings {
            languageVersion = "2.0"
        }
    }
}

application {
    mainClass.set("com.github.recke96.aoc.MainKt")
}
