import java.net.URI

plugins {
    `java-library`
    `maven-publish`
}

base {
    archivesName = "${findProperty("mod_id")}-${project.name}-${findProperty("minecraft_version")}"
}

val libs = versionCatalogs.named("libs")

java {
    toolchain.languageVersion = JavaLanguageVersion.of(libs.findVersion("java").get().toString().toInt())
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    // https://docs.gradle.org/current/userguide/declaring_repositories.html#declaring_content_exclusively_found_in_one_repository
    exclusiveContent {
        forRepository {
            maven {
                name = "Sponge"
                url = URI("https://repo.spongepowered.org/repository/maven-public")
            }
        }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
    exclusiveContent {
        forRepositories(
            maven {
                name = "ParchmentMC"
                url = URI("https://maven.parchmentmc.org/")
            },
            maven {
                name = "NeoForge"
                url = URI("https://maven.neoforged.net/releases")
            }
        )
        filter { includeGroup("org.parchmentmc.data") }
    }
    maven {
        name = "BlameJared"
        url = URI("https://maven.blamejared.com")
    }
}

listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations[variant].outgoing {
        capability("$group:${project.name}:$version")
        capability("$group:${base.archivesName.get()}:$version")
        capability("$group:${findProperty("mod_id")}-${project.name}-${findProperty("minecraft_version")}:$version")
        capability("$group:${findProperty("mod_id")}:$version")
    }
    publishing.publications.configureEach {
        this as MavenPublication
        suppressPomMetadataWarningsFor(variant)
    }
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${findProperty("mod_name")}" }
    }

    val attributes = mapOf(
        "Specification-Title"       to findProperty("mod_name"),
        "Specification-Vendor"      to findProperty("mod_author"),
        "Specification-Version"     to project.tasks.jar.get().archiveVersion,
        "Implementation-Title"      to project.name,
        "Implementation-Version"    to project.tasks.jar.get().archiveVersion,
        "Implementation-Vendor"     to findProperty("mod_author"),
        "Built-On-Minecraft"        to findProperty("minecraft_version")
    )

    manifest {
        attributes(
            attributes
        )
    }
}

tasks.processResources {
    val expandProps = mapOf(
        "version"                       to version,
        "group"                         to project.group, // Else we target the task's group.
        "minecraft_version"             to findProperty("minecraft_version"),
        "minecraft_version_range"       to findProperty("minecraft_version_range"),
        "fabric_version"                to findProperty("fabric_version"),
        "fabric_loader_version"         to findProperty("fabric_loader_version"),
        "mod_name"                      to findProperty("mod_name"),
        "mod_author"                    to findProperty("mod_author"),
        "mod_id"                        to findProperty("mod_id"),
        "license"                       to findProperty("license"),
        "description"                   to project.description,
        "neoforge_version"              to findProperty("neoforge_version"),
        "neoforge_loader_version_range" to findProperty("neoforge_loader_version_range"),
        "forge_version"                 to findProperty("forge_version"),
        "forge_loader_version_range"    to findProperty("forge_loader_version_range"),
        "credits"                       to findProperty("credits"),
        "java_version"                  to libs.findVersion("java").get().toString()
    )

    val jsonExpandProps = expandProps.mapValues { (key, value) ->
        if (value is String) value.replace("\n", "\\\\n")
        else value
    }

    filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) {
        expand(expandProps)
    }

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "*.mixins.json")) {
        expand(jsonExpandProps)
    }

    inputs.properties(expandProps)
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components.findByName("java"))
        }
    }

    repositories {
        System.getenv("local_maven_url")?.let {
            maven(it)
        }
    }
}