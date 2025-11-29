plugins {
    alias(libs.plugins.protobuf) apply false
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "idea")

    extensions.configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(rootProject.libs.versions.java.get()))
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