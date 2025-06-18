plugins {
    kotlin("jvm")
    scala
    `multiloader-common`
    alias(libs.plugins.neoforge.moddev)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

neoForge {
    neoFormVersion = property("neo_form_version")!!.toString()
    // Automatically enable AccessTransformers if the file exists
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = property("parchment_minecraft")!!.toString()
        mappingsVersion = property("parchment_version")!!.toString()
    }
}

lateinit var library: Configuration

configurations {
    library = create("library") {
        isCanBeResolved = true
    }
}

dependencies {
    implementation(libs.mixin)
    implementation(libs.mixinextras)
    implementation("org.ow2.asm:asm-tree:9.6")
    annotationProcessor(libs.mixinextras)

    library(libs.scala3.library)
    library(libs.scala3.toolkit)
}

scala {
    zincVersion = "1.10.8"
}

tasks.compileScala {
    targetCompatibility = libs.versions.java.get()
    scalaCompileOptions.apply {
        isForce = true

        forkOptions.apply {
            memoryMaximumSize = "8g"
            jvmArgs = listOf("-XX:MaxMetaspaceSize=512m")
        }

        additionalParameters = listOf("-Yexplicit-nulls", "-experimental")
    }
}

configurations {
    create("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonKotlin") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonScala") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile) {
        builtBy(tasks.compileJava)
    }

    sourceSets.main.get().kotlin.sourceDirectories.files.forEach { file ->
        add("commonKotlin", file) {
            builtBy(tasks.compileKotlin)
        }
    }

    sourceSets.main.get().scala.sourceDirectories.files.forEach { file ->
        add("commonScala", file) {
            builtBy(tasks.compileScala)
        }
    }
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile) {
        builtBy(tasks.processResources)
    }
}

sourceSets {
    main {
        compileClasspath += library
        runtimeClasspath += library
    }
}

tasks {
    jar {
        enabled = false
    }

    javadocJar {
        enabled = false
    }
}