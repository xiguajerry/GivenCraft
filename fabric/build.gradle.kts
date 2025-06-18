plugins {
    kotlin("jvm")
    scala
    `multiloader-loader`
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

loom {
    val aw = project(":common").file("src/main/resources/${property("mod_id")}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }
    mixin {
        defaultRefmapName.set("${property("mod_id")}.refmap.json")
    }
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

val modLibrary by configurations.creating

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${property("parchment_minecraft")}:${property("parchment_version")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    modLibrary(libs.scala3.library)
    modLibrary(libs.scala3.toolkit)

    modLibrary(project(path = ":common", configuration = "library")) {
        exclude("org.apache.commons", "commons-lang3")
        exclude("org.slf4j", "slf4j-api")
        exclude("org.lwjgl", "lwjgl-opengl")
        exclude("org.lwjgl", "lwjgl-glfw")
        exclude("org.lwjgl", "lwjgl")
    }
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

sourceSets {
    main {
        resources { srcDir("src/generated/resources") }
        compileClasspath += modLibrary
        runtimeClasspath += modLibrary
    }
}

tasks.shadowJar {
    archiveClassifier = "named"
    configurations = listOf(modLibrary)
}

tasks.remapJar {
    dependsOn(tasks.shadowJar)
    inputFile = tasks.shadowJar.get().archiveFile
}

tasks.javadocJar { enabled = false }
tasks.sourcesJar { enabled = false }
tasks.remapSourcesJar { enabled = false }