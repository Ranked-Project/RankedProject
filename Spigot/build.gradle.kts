import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import io.papermc.paperweight.util.download

plugins {
    id("java")
    alias(libs.plugins.paperweight) apply false
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.shadow) apply false
}

group = "net.rankedproject"
version = "1.0-SNAPSHOT"

val minecraftVersion = libs.versions.minecraft.get()

subprojects {
    plugins.apply("com.gradleup.shadow")
    plugins.apply("xyz.jpenilla.run-paper")
    plugins.apply("io.papermc.paperweight.userdev")

    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.infernalsuite.com/repository/maven-snapshots/")
        maven("https://repo.infernalsuite.com/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
        maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        val isCommonSpigot = project.name == "CommonSpigot"
        if (!isCommonSpigot) {
            api(project(":Spigot:CommonSpigot"))
        }

        api(project(":Common"))
        api("com.infernalsuite.asp:mongo-loader:4.0.0-SNAPSHOT")
        api("com.infernalsuite.asp:file-loader:4.0.0-SNAPSHOT")
        api(rootProject.libs.cloud.paper)
        api(rootProject.libs.reflections)
        compileOnly(rootProject.libs.lombok)
        compileOnly("com.infernalsuite.asp:api:4.0.0-SNAPSHOT")
        compileOnly("io.github.toxicity188:bettermodel:1.14.2")
        compileOnly(rootProject.libs.packetevents.spigot)
        annotationProcessor(rootProject.libs.lombok)
    }

    plugins.withId("java") {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(rootProject.libs.versions.java.get()))
                vendor.set(JvmVendorSpec.JETBRAINS)
            }
        }
    }

    plugins.withId("io.papermc.paperweight.userdev") {
        dependencies {
            val paperWeight = the<PaperweightUserDependenciesExtension>()
            paperWeight.paperDevBundle("${minecraftVersion}-R0.1-SNAPSHOT")
        }
    }

    plugins.withId("xyz.jpenilla.run-paper") {
        tasks.named<xyz.jpenilla.runpaper.task.RunServer>("runServer") {
            legacyPluginLoading()
            disablePluginRemapping()
            minecraftVersion(minecraftVersion)
            serverJar(file("../Server/asp-server.jar"))

            downloadPlugins {
                github("retrooper", "packetevents", "v2.10.1", "packetevents-spigot-2.10.1.jar")
                github("toxicity188", "BetterModel", "1.14.2", "BetterModel-1.14.2-paper.jar")
            }

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

    tasks.named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        exclude("io/reactivex/**")
        exclude("net/bytebuddy/**")
    }
}