import org.ec4j.gradle.EditorconfigExtension
import org.sonarqube.gradle.SonarExtension

plugins {
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.sonarqube) apply false
    alias(libs.plugins.editorconfig) apply false
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "idea")
    apply(plugin = "org.sonarqube")
    apply(plugin = "org.ec4j.editorconfig")
    apply(plugin = "checkstyle")

    repositories {
        mavenCentral()
    }

    dependencies {
        "checkstyle"(rootProject.libs.stylecheck)
    }

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(rootProject.libs.versions.java.get()))
    }

    extensions.configure<SonarExtension> {
        properties {
            property("sonar.projectKey", "Ranked-Project_RankedProject")
            property("sonar.organization", "ranked-project")
        }
    }

    extensions.configure<EditorconfigExtension> {
        includes = listOf("src/**")
        file(rootProject.file(".editorconfig").path)
    }

    extensions.configure<CheckstyleExtension> {
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")

        isIgnoreFailures = true
        isShowViolations = true
        toolVersion = rootProject.libs.versions.checkstyle.get()
    }

    tasks.withType<Checkstyle> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}


subprojects {
    plugins.apply(rootProject.libs.plugins.protobuf.get().pluginId)

    configure<com.google.protobuf.gradle.ProtobufExtension> {
        protoc {
            artifact = rootProject.libs.protobuf.protoc.get().toString()
        }
    }

    extensions.configure<JavaPluginExtension> {
        sourceSets {
            val main by getting {
                java.srcDirs("build/generated/source/proto/main/java")
            }
        }
    }
}
