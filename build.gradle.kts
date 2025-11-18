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
    plugins.apply("com.google.protobuf")

    configure<com.google.protobuf.gradle.ProtobufExtension> {
        protoc {
            artifact = rootProject.libs.protobuf.protoc.get().toString()
        }
    }
}