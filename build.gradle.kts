plugins {
    kotlin("jvm")
    alias(libs.plugins.shadow) apply false
    alias(libs.plugins.fabric.loom) apply false
    alias(libs.plugins.neoforge.moddev) apply false
}

group = "me.moeyinlo"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

allprojects {
    apply {
        plugin("java")
    }

    repositories {
        mavenCentral()
    }

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://jitpack.io")
        maven("https://maven.luna5ama.dev/")
    }

    tasks.javadoc {
        enabled = false
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        options.release = libs.versions.java.get().toInt()
    }

    tasks.jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    java {
        withSourcesJar()
    }
}