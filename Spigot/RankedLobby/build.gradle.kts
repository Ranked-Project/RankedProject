import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "net.rankedproject"
version = "1.0-SNAPSHOT"
val minecraftVersion = "1.21.10"

plugins {
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        url = uri("https://cache-redirector.jetbrains.com/intellij-dependencies")
    }
}

dependencies {
    paperweight.paperDevBundle("${minecraftVersion}-R0.1-SNAPSHOT")
    api(project(":Spigot:CommonSpigot"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
        vendor.set(JvmVendorSpec.JETBRAINS)
    }
}

tasks {
    runServer {
        legacyPluginLoading()
        disablePluginRemapping()
        minecraftVersion(minecraftVersion)
        serverJar(File("../Server/asp-server.jar"))
        val jvmArgsFile = project.file("../jvm.args")
        if (jvmArgsFile.exists()) {
            val argsFromFile = jvmArgsFile.readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() && !it.startsWith("#") }
            jvmArgs(argsFromFile)
        }
        debugOptions {
            enabled = true
            suspend = false
            port = 5005
        }
        environment("ENVIRONMENT", "TEST")
    }
}

tasks.withType<ShadowJar> {
    minimize()
    archiveClassifier.set("")
    exclude("io/reactivex/**")
    exclude("net/bytebuddy/**")
}