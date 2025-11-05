plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19" apply false
    id("xyz.jpenilla.run-paper") version "3.0.2" apply false
    id("com.gradleup.shadow") version "9.2.2" apply false
}

group = "net.rankedproject"
version = "1.0-SNAPSHOT"

subprojects {
    plugins.apply("com.gradleup.shadow")

    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.infernalsuite.com/repository/maven-snapshots/")
        maven("https://repo.infernalsuite.com/repository/maven-releases/")
        gradlePluginPortal()

        mavenLocal()
        mavenCentral()
    }

    dependencies {
        api(project(":Common"))
        api("com.infernalsuite.asp:mongo-loader:4.0.0-SNAPSHOT")
        api("org.incendo:cloud-paper:2.0.0-beta.10")
        api("org.reflections:reflections:0.10.2")
        compileOnly("org.projectlombok:lombok:1.18.42")
        compileOnly("com.infernalsuite.asp:api:4.0.0-SNAPSHOT")
        annotationProcessor("org.projectlombok:lombok:1.18.42")
        testImplementation("org.mockito:mockito-core:5.20.0")
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
        testImplementation("org.junit.platform:junit-platform-launcher:6.0.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}