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
            server = true
            host = "*"
            suspend = false
            port = 5005
        }
        environment("ENVIRONMENT", "TEST")
        environment("MONGO_DATABASE", "ranked_data")
        environment("MONGO_COLLECTION", "test_worlds")
        environment("MONGO_USERNAME", "test_user")
        environment("MONGO_PASSWORD", "test_password")
        environment("MONGO_AUTH_SOURCE", "admin")
        environment("MONGO_HOST", "127.0.0.1")
        environment("MONGO_PORT", "27017")
    }
}