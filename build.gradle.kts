plugins {
    id("org.graalvm.buildtools.native") version "0.9.28"
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

graalvmNative {
    binaries.all {
        resources.autodetect()
    }
    toolchainDetection = false
    binaries.named("main") {
        imageName = "advent-of-code"
        javaLauncher = javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
            vendor = JvmVendorSpec.matching("GraalVM Community")
        }
    }
}
