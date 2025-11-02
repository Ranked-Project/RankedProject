import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "net.rankedproject"
version = "1.0-SNAPSHOT"

plugins {
    id("io.papermc.paperweight.userdev")
}

dependencies {
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")
    api(project(":Spigot:CommonSpigot"))
}

tasks.withType<ShadowJar> {
    minimize()
    archiveClassifier.set("")
    exclude("io/reactivex/**")
    exclude("net/bytebuddy/**")
}