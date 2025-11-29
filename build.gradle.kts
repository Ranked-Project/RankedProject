import org.sonarqube.gradle.SonarExtension

plugins {
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.sonarqube) apply false
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "idea")
    apply(plugin = "org.sonarqube")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(rootProject.libs.versions.java.get()))
    }

    extensions.configure<SonarExtension> {
        properties {
            property("sonar.projectKey", "Ranked-Project_RankedProject")
            property("sonar.organization", "ranked-project")
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