plugins {
    id("java")
    alias(libs.plugins.shadow) apply true
}

group = "org.rankedproject"
version = "1.0"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    mavenCentral()
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    api(project(":Common"))
}

tasks.shadowJar {
    archiveBaseName.set("velocity-proxy")
    archiveVersion.set("")
    archiveClassifier.set("")
}