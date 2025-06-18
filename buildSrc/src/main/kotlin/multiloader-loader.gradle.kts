import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("multiloader-common")
}

configurations {
    create("commonJava") {
        isCanBeResolved = true
    }
    create("commonResources") {
        isCanBeResolved = true
    }
    create("commonKotlin") {
        isCanBeResolved = true
    }
    create("commonScala") {
        isCanBeResolved = true
    }
}

val commonJava = configurations.getByName("commonJava")
val commonKotlin = configurations.getByName("commonKotlin")
val commonScala = configurations.getByName("commonScala")
val commonResources = configurations.getByName("commonResources")

dependencies {
    compileOnly(project(":common")) {
        capabilities {
            requireCapability("$group:${property("mod_id")}")
        }
    }
    commonJava(project(":common", configuration = "commonJava"))
    commonKotlin(project(":common", configuration = "commonKotlin"))
    commonScala(project(":common", configuration = "commonScala"))
    commonResources(project(":common", configuration = "commonResources"))
}

tasks.named("compileJava", JavaCompile::class) {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named("compileKotlin", KotlinCompile::class) {
    dependsOn(commonKotlin)
    source(commonKotlin)
}

tasks.named("compileScala", ScalaCompile::class) {
    dependsOn(commonScala)
    source(commonScala)
}

tasks.processResources {
    dependsOn(commonResources)
    from(commonResources)
}

tasks.named("javadoc", Javadoc::class).configure {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named("sourcesJar", Jar::class) {
    dependsOn(commonJava)
    from(commonJava)
    dependsOn(commonKotlin)
    from(commonKotlin)
    dependsOn(commonScala)
    from(commonScala)
    dependsOn(commonResources)
    from(commonResources)
}
