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
    api(project(":Common"))

    compileOnly(libs.velocity)
    compileOnly(libs.lombok)

    annotationProcessor(libs.velocity)
    annotationProcessor(libs.lombok)
}

tasks.shadowJar {
    archiveBaseName.set("velocity-proxy")
    archiveVersion.set("")
    archiveClassifier.set("")
}
